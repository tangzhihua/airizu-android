package cn.airizu.activity.room_map;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.activity.free_book_confirm_checkin_time.FreebookConfirmCheckinTimeActivity;
import cn.airizu.activity.room_detail_basic_information.RoomDetailOfBasicInformationActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRespondBean;
import cn.airizu.domain.protocol.room_search.RoomInfo;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import cn.airizu.toolutils.lbs.baidu.BaiduLbsSingleton;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;

public class RoomMapActivityByBaiduLBS extends MapActivity {
	private final String TAG = this.getClass().getSimpleName();
	
	private View popViewForMap = null; // 点击mark时弹出的气泡View
	private MapView baidu_MapView;
	private OverItemT overitem = null;
	
	private CustomTitleBar titleBar;
	
	public static enum IntentExtraDataKeyEnum {
		// UI类型, 是从房源列表进入此界面的, 还是从房间详情进入此界面的
		UI_TYPE,
		// 传入的数据, 如果是从 "房源列表" 进入此界面, 那么传递的是List<RoomInfo>, 如果从 "房间详情" 进入的, 传过来就是 RoomInfo
		DATA,
		// 如果是从 "房源列表"
		CITY_NAME
	};
	
	public static enum UiTypeEnum {
		// 一组房间(推荐城市)
		GROUP_ROOM_FOR_CITY,
		// 一组房间(用户附近)
		GROUP_ROOM_FOR_NEARBY,
		// 一个房间
		SINGLE_ROOM
	};
	
	private UiTypeEnum uiTypeEnum;
	
	private RoomDetailNetRespondBean dataForSingleRoom;
	private List<RoomInfo> dataForGroupRoom;
	
	private TextView userOrRoomAddressTextView;
	private String cityName = "";
	private TextView priceTextView;
	private SimpleLocationForBaiduLBS userLocationForBaiduLBS;
	private SimpleLocationForBaiduLBS.AddrInfoDelegate userAddress = new SimpleLocationForBaiduLBS.AddrInfoDelegate() {
		
		@Override
		public void addrInfoCallback(MKAddrInfo location) {
			
			if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
			} else if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum) || UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
				String userAddress = "您当前的位置 : ";
				userAddress += location.addressComponents.city + location.addressComponents.street;
				userOrRoomAddressTextView.setText(userAddress);
			}
		}
	};
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_map_activity_baidu_lbs);
		
		// 一定要先解析外部传入的Intent数据
		parseIncomingIntent(getIntent());
		
		configLBSByUiType(uiTypeEnum);
		
		configActivityByUiType(uiTypeEnum);
		
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		if (userLocationForBaiduLBS != null) {
			userLocationForBaiduLBS.stopLocationRequest();
		}
		BaiduLbsSingleton.getInstance().getBaiduMapManager().stop();
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
		
		BaiduLbsSingleton.getInstance().getBaiduMapManager().start();
		if (userLocationForBaiduLBS != null) {
			userLocationForBaiduLBS.startLocationRequest();
		}
	}
	
	private boolean parseIncomingIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			if (!intent.hasExtra(IntentExtraDataKeyEnum.UI_TYPE.name())) {
				assert false : "has not UI_TYPE in incoming";
				break;
			}
			uiTypeEnum = (UiTypeEnum) intent.getSerializableExtra(IntentExtraDataKeyEnum.UI_TYPE.name());
			if (uiTypeEnum == null) {
				assert false : "uiTypeEnum is null";
				break;
			}
			
			if (!intent.hasExtra(IntentExtraDataKeyEnum.DATA.name())) {
				assert false : "has not DATA in incoming";
				break;
			}
			
			if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum)) {
				dataForGroupRoom = intent.getParcelableArrayListExtra(IntentExtraDataKeyEnum.DATA.name());
			} else if (UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
				dataForGroupRoom = intent.getParcelableArrayListExtra(IntentExtraDataKeyEnum.DATA.name());
			} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
				dataForSingleRoom = (RoomDetailNetRespondBean) intent.getSerializableExtra(IntentExtraDataKeyEnum.DATA.name());
			} else {
				assert false : "incoming UiTypeEnum is error";
				break;
			}
			if (intent.hasExtra(IntentExtraDataKeyEnum.CITY_NAME.name())) {
				cityName = intent.getStringExtra(IntentExtraDataKeyEnum.CITY_NAME.name());
			}
			
			// 一切OK
			return true;
		} while (false);
		
		// 出现问题
		cityName = "";
		uiTypeEnum = null;
		dataForGroupRoom = null;
		dataForSingleRoom = null;
		return false;
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	private void setTitleTextForTitleBarByUiType(UiTypeEnum uiTypeEnum) {
		String titleBarTitle = "";
		if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum)) {
			titleBarTitle = cityName;
		} else if (UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
			titleBarTitle = cityName;
		} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
			titleBarTitle = "房间:" + dataForSingleRoom.getNumber();
		}
		
		titleBar.setTitleByString(titleBarTitle);
	}
	
	private void setTextForUserOrRoomAddressTextViewByUiType(UiTypeEnum uiTypeEnum) {
		String userOrRoomAddress = "您当前的位置 : " + "定位中...";
		if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum)) {
			
			if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
				userOrRoomAddress = "您当前的位置 : " + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.street;
			}
		} else if (UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
			if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
				userOrRoomAddress = "您当前的位置 : " + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.street;
			}
		} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
			userOrRoomAddress = "房间位置 : " + dataForSingleRoom.getAddress();
			
		} else {
			return;
		}
		userOrRoomAddressTextView.setText(userOrRoomAddress);
	}
	
	private void configLBSByUiType(UiTypeEnum uiTypeEnum) {
		// 如果使用地图SDK，请初始化地图Activity
		BaiduLbsSingleton.getInstance().getBaiduMapManager().start();
		if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() == null) {
			userLocationForBaiduLBS = new SimpleLocationForBaiduLBS(null, userAddress, false);
		}
		super.initMapActivity(BaiduLbsSingleton.getInstance().getBaiduMapManager());
		
		baidu_MapView = (MapView) findViewById(R.id.bmapView);
		baidu_MapView.setBuiltInZoomControls(true);
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		baidu_MapView.setDrawOverlayWhenZooming(true);
		
		Drawable mapMarketDrawable = null;
		if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum) || UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
			mapMarketDrawable = getResources().getDrawable(R.drawable.biao);
		} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
			mapMarketDrawable = getResources().getDrawable(R.drawable.roop);
			
			// 创建点击mark时的弹出泡泡
			popViewForMap = super.getLayoutInflater().inflate(R.layout.map_popview, null);
			baidu_MapView.addView(popViewForMap, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.TOP_LEFT));
			final TextView infoTextView = (TextView) findViewById(R.id.info_TextView);
			infoTextView.setText(dataForSingleRoom.getTitle());
			popViewForMap.setVisibility(View.GONE);
		} else {
			return;
		}
		
		// 为maker定义位置和边界
		mapMarketDrawable.setBounds(0, 0, mapMarketDrawable.getIntrinsicWidth(), mapMarketDrawable.getIntrinsicHeight());
		overitem = new OverItemT(mapMarketDrawable);
		// List<Overlay> getOverlays()
		// 获取Overlay列表。 这个列表中的任何一个 Overlay都将被绘制（以升序方式），都能收到事件（以降序方式，直到返回true）。
		baidu_MapView.getOverlays().add(overitem); // 添加ItemizedOverlay实例到mMapView
	}
	
	private void configUIWidgetByUiType(UiTypeEnum uiTypeEnum) {
		
		if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum)) {
			final View freebookLayout = findViewById(R.id.free_book_toolbar_layout);
			freebookLayout.setVisibility(View.GONE);
		} else if (UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
			final View freebookLayout = findViewById(R.id.free_book_toolbar_layout);
			freebookLayout.setVisibility(View.GONE);
		} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
			final Button freebookButton = (Button) findViewById(R.id.freebook_Button);
			freebookButton.setOnClickListener(freebookButtonClickListener);
			
			priceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(dataForSingleRoom.getPrice()));
		} else {
			return;
		}
	}
	
	private void configActivityByUiType(UiTypeEnum uiTypeEnum) {
		// --------- titlebar -----------
		// TitleBar
		titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		// --------- titlebar -----------
		
		userOrRoomAddressTextView = (TextView) findViewById(R.id.user_or_room_address_TextView);
		priceTextView = (TextView) findViewById(R.id.price_TextView);
		
		configUIWidgetByUiType(uiTypeEnum);
		setTitleTextForTitleBarByUiType(uiTypeEnum);
		setTextForUserOrRoomAddressTextViewByUiType(uiTypeEnum);
		
	}
	
	private View.OnClickListener freebookButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Intent intent = new Intent(RoomMapActivityByBaiduLBS.this, FreebookConfirmCheckinTimeActivity.class);
			intent.putExtra(FreebookConfirmCheckinTimeActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), dataForSingleRoom.getNumber());
			startActivity(intent);
		}
	};
	
	private void gotoRoomDetailActivityWithRoomInfo(final RoomInfo roomInfo) {
		if (roomInfo == null) {
			return;
		}
		final Intent intent = new Intent(RoomMapActivityByBaiduLBS.this, RoomDetailOfBasicInformationActivity.class);
		intent.putExtra(RoomDetailOfBasicInformationActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomInfo.getRoomId());
		startActivity(intent);
	}
	
	/**
	 * OverlayItem(ItemizedOverlay的基本组件)
	 */
	
	// ItemizedOverlay是Overlay的一个基类，包含了一个OverlayItem列表。
	// 从南到北的处理item，用于绘制、创建平移边界、为每个点绘制标记点，和维护一个焦点选中的item，
	// 同时也负责把一个屏幕点击匹配到item上去，分发焦点改变事件给备选的监听器。
	/**
	 * @author zhihua.tang
	 */
	private class OverItemT extends ItemizedOverlay<OverlayItem> {
		private final String TAG = this.getClass().getSimpleName();
		public List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
		private Drawable defaultMarker;
		private Bitmap markerBitmap;
		
		public OverItemT(Drawable marker) {
			super(boundCenterBottom(marker));
			
			defaultMarker = marker;
			markerBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
			
			setMapCenterGeoPoint();
			
			initOverlayItemList();
			
			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}
		
		private void setMapCenterGeoPoint() {
			do {
				
				GeoPoint centerGeoPoint = null;
				// 纬度
				Double latitude = null;
				// 经度
				Double longitude = null;
				
				if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
					if (dataForSingleRoom == null) {
						break;
					}
					if (TextUtils.isEmpty(dataForSingleRoom.getLat()) || TextUtils.isEmpty(dataForSingleRoom.getLen())) {
						DebugLog.e(TAG, "lat or len is empty ! ");
						break;
					}
					// 纬度
					latitude = Double.valueOf(dataForSingleRoom.getLat());
					// 经度
					longitude = Double.valueOf(dataForSingleRoom.getLen());
				} else if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum)) {
					if (dataForGroupRoom == null) {
						break;
					}
					final RoomInfo firstRoomInfo = dataForGroupRoom.get(0);
					if (firstRoomInfo == null) {
						break;
					}
					// 纬度
					latitude = Double.valueOf(firstRoomInfo.getLat());
					// 经度
					longitude = Double.valueOf(firstRoomInfo.getLen());
				} else if (UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
					if (SimpleLocationForBaiduLBS.getLastLocation() == null) {
						break;
					}
					// 纬度
					latitude = SimpleLocationForBaiduLBS.getLastLocation().getLatitude();
					// 经度
					longitude = SimpleLocationForBaiduLBS.getLastLocation().getLongitude();
				} else {
					return;
				}
				centerGeoPoint = new GeoPoint((int) (latitude.floatValue() * 1E6), (int) (longitude.floatValue() * 1E6));
				// MapView.getController() : 返回地图的MapController，这个对象可用于控制和驱动平移和缩放。
				// MapController.setCenter()在给定的中心点GeoPoint上设置地图视图。
				baidu_MapView.getController().setCenter(centerGeoPoint);
			} while (false);
			
		}
		
		private void initOverlayItemList() {
			do {
				
				if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
					if (dataForSingleRoom == null) {
						break;
					}
					// 纬度
					final Double latitude = Double.valueOf(dataForSingleRoom.getLat());
					// 经度
					final Double longitude = Double.valueOf(dataForSingleRoom.getLen());
					final GeoPoint roomGeoPoint = new GeoPoint((int) (latitude.floatValue() * 1E6), (int) (longitude.floatValue() * 1E6));
					final OverlayItem overlayItem = new OverlayItem(roomGeoPoint, "", "");
					overlayItemList.add(overlayItem);
				} else {
					if (dataForGroupRoom == null) {
						break;
					}
					
					for (RoomInfo roomInfo : dataForGroupRoom) {
						DebugLog.i(TAG, roomInfo.toString());
						
						// 纬度
						final Double latitude = Double.valueOf(roomInfo.getLat());
						// 经度
						final Double longitude = Double.valueOf(roomInfo.getLen());
						
						// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
						// 这里创建 GeoPoint 必须使用 float 值, 而不能使用 double
						final GeoPoint roomGeoPoint = new GeoPoint((int) (latitude.floatValue() * 1E6), (int) (longitude.floatValue() * 1E6));
						
						// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
						final OverlayItem overlayItem = new OverlayItem(roomGeoPoint, ToolsFunctionForThisProgect.getFormattedPriceString(roomInfo.getPrice()), roomInfo.getDistance());
						overlayItemList.add(overlayItem);
					}
				}
				
			} while (false);
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// 先绘制 defaultMarker
			// super.draw(canvas, mapView, shadow);
			
			// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
			final Projection projection = mapView.getProjection();
			for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
				final OverlayItem overLayItem = getItem(index); // 得到给定索引的item
				
				final String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				final Point point = projection.toPixels(overLayItem.getPoint(), null);
				// 可在此处添加您的绘制代码
				final Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setTextSize(15);
				// paint.setTypeface(Typeface.DEFAULT_BOLD);
				
				if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
					canvas.drawBitmap(markerBitmap, point.x - 16, point.y - 24, paint);
				} else {
					canvas.drawBitmap(markerBitmap, point.x - 34, point.y - 35, paint);
					canvas.drawText(title, point.x - 24, point.y - 17, paint); // 绘制文本
				}
			}
			
			// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(defaultMarker);
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return overlayItemList.get(i);
		}
		
		@Override
		public int size() {
			return overlayItemList.size();
		}
		
		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			
			if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
				setFocus(overlayItemList.get(i));
				// 更新气泡位置,并使之显示
				final GeoPoint pt = overlayItemList.get(i).getPoint();
				baidu_MapView.updateViewLayout(popViewForMap, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt, MapView.LayoutParams.BOTTOM_CENTER));
				popViewForMap.setVisibility(View.VISIBLE);
			} else {
				final RoomInfo roomInfo = dataForGroupRoom.get(i);
				if (roomInfo != null) {
					gotoRoomDetailActivityWithRoomInfo(roomInfo);
				} else {
					DebugLog.e(TAG, "onTap--> roomInfo is null ");
				}
			}
			
			return true;
		}
		
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			if (UiTypeEnum.GROUP_ROOM_FOR_CITY.equals(uiTypeEnum) || UiTypeEnum.GROUP_ROOM_FOR_NEARBY.equals(uiTypeEnum)) {
				
			} else if (UiTypeEnum.SINGLE_ROOM.equals(uiTypeEnum)) {
				// 消去弹出的气泡
				popViewForMap.setVisibility(View.GONE);
			} else {
				
			}
			
			return super.onTap(arg0, arg1);
		}
	}
}
