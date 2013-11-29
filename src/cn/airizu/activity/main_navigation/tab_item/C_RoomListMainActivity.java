package cn.airizu.activity.main_navigation.tab_item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.room_detail_basic_information.RoomDetailOfBasicInformationActivity;
import cn.airizu.activity.room_filter.RoomFilterActivity;
import cn.airizu.activity.room_map.RoomMapActivityByBaiduLBS;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.custom_control.adapter.RoomInfoListAdapter;
import cn.airizu.custom_control.calendar.CustomCalendar;
import cn.airizu.custom_control.calendar.DayCellForCustomCalendar;
import cn.airizu.custom_control.pop_list.RadioPopList;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.room_search.RoomInfo;
import cn.airizu.domain.protocol.room_search.RoomSearchDatabaseFieldsConstant;
import cn.airizu.domain.protocol.room_search.RoomSearchNetRequestBean;
import cn.airizu.domain.protocol.room_search.RoomSearchNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import cn.airizu.toolutils.lbs.baidu.BaiduLbsSingleton;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;

import com.baidu.mapapi.MKAddrInfo;

/**
 * "附近" 界面, 同时也是房源列表界面
 * 
 * @author zhihua.tang
 */
public class C_RoomListMainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private boolean isNearbyActivity = false;
	
	public static enum IntentExtraTagEnum {
		// 当前界面为 "附近" 界面
		IS_NEARBY_ACTIVITY,
		// 房间搜索条件
		ROOM_SEARCH_CRITERIA
	};
	
	private static enum IntentRequestCodeEnum {
		// 去 房间筛选 界面
		TO_ROOM_FILTER_ACTIVITY
	};
	
	private static enum NetRequestTagEnum {
		ROOM_SEARCH
	};
	
	private int netRequestIndexForRoomInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private CustomTitleBar titleBar;
	
	// 房源列表ListView
	private ListView roomInfoListView;
	
	// 房源信息业务Bean列表
	private List<RoomInfo> roomInfoBeanList = new ArrayList<RoomInfo>();
	// 房源信息适配器数据源
	private List<Map<String, String>> dataSourceForRoomInfoListView = new ArrayList<Map<String, String>>();
	// 本次从网络侧获取的新数据
	private List<Map<String, String>> newDataForRoomInfoList;
	// 房间信息列表适配器
	private RoomInfoListAdapter adapterForRoomInfoListView;
	// 已经是最后一屏数据了
	private boolean isLastRow = false;
	
	// 房源列表界面中, "入住时间" "退房时间" "房源数量", 等控件所在的布局
	private RelativeLayout checkDataRelativeLayout;
	// 入住时间
	private TextView checkinDateTextView;
	// 退房时间
	private TextView checkoutDateTextView;
	// 房源数量
	private TextView roomTotalTextView;
	
	// "距离筛选" "价格筛选" "入住时间"
	private LinearLayout filterLinearLayout;
	// 当前搜索条件下, 搜索到的房间总数
	private int roomCountInt = 0;
	// 房间搜索时, 查询从哪行开始
	private int roomSearchStartIndex = 0;
	// 房间搜索时, 最大请求条数
	private final int roomSearchMaxNumber = 10;
	
	// 房间搜索条件Bundle
	private Bundle roomSearchCriteriaBundle;
	
	// 会触发 弹出窗口的控件
	// "排序筛选"
	private Button orderFilterButton;
	// "距离筛选"
	private View distanceFilterLayout;
	// "价格筛选"
	private View priceFilterLayout;
	// "入住时间筛选"
	private View checkInDateFilterLayout;
	
	// "距离筛选"
	private TextView distanceFilterTextView;
	// "价格筛选"
	private TextView priceFilterLayoutTextView;
	// "入住时间筛选"
	private TextView checkInDateFilterLayoutTextView;
	
	// "暂无数据" 的提示信息View
	private TextView hintForNoDataTextView;
	
	private SimpleLocationForBaiduLBS userNearbyLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		// 必须在最前面
		if (!parseIntent(getIntent())) {
			// 入参错误
			return;
		} else {
			loadRealUI();
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		DebugLog.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
		
		if (isNearbyActivity) {
			queryNearbyRoomsForUser();
		} else {
			
		}
	}
	
	@Override
	protected void onRestart() {
		DebugLog.i(TAG, "onRestart");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		
		SimpleProgressDialog.resetByThisContext(this);
		
		dismissAllPopupWindowForThisActivity();
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
		
		if (isNearbyActivity) {
			if (userNearbyLocation != null) {
				userNearbyLocation.stopLocationRequest();
			}
		} else {
			
		}
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		
		// 注销广播接收器
		unregisterReceiverForUserLocation();
		// 停止位置查询
		if (userNearbyLocation != null) {
			userNearbyLocation.stopLocationRequest();
		}
		
		super.onDestroy();
	}
	
	private void loadRealUI() {
		setContentView(R.layout.room_list_main_activity);
		
		//
		hintForNoDataTextView = (TextView) findViewById(R.id.no_data_TextView);
		
		// --------- titlebar -----------
		// TitleBar
		titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		// --------- titlebar -----------
		
		// --------- checkDatabar -----------
		// 房源列表界面中, "入住时间" "退房时间" "房源数量"
		checkDataRelativeLayout = (RelativeLayout) findViewById(R.id.check_data_RelativeLayout);
		// 入住时间
		final java.util.Date utildate = new java.util.Date();
		final java.sql.Date date = new java.sql.Date(utildate.getTime());
		checkinDateTextView = (TextView) findViewById(R.id.checkin_date_TextView);
		checkinDateTextView.setText(date.toString());
		// 退房时间
		final String checkOutString = ToolsFunctionForThisProgect.getAfterDayBySpecifiedDay(date.toString());
		checkoutDateTextView = (TextView) findViewById(R.id.checkout_date_TextView);
		checkoutDateTextView.setText(checkOutString);
		// 房源数量
		roomTotalTextView = (TextView) findViewById(R.id.room_amount_TextView);
		// --------- checkDatabar -----------
		
		// --------- filterbar -----------
		// 筛选Bar : 距离筛选/价格筛选/入住时间
		filterLinearLayout = (LinearLayout) findViewById(R.id.filter_layout);
		// "距离筛选"
		distanceFilterLayout = findViewById(R.id.distance_layout);
		distanceFilterLayout.setOnClickListener(filterBarClickListener);
		distanceFilterTextView = (TextView) findViewById(R.id.distance_TextView);
		// 价格筛选
		priceFilterLayout = findViewById(R.id.price_layout);
		priceFilterLayout.setOnClickListener(filterBarClickListener);
		priceFilterLayoutTextView = (TextView) findViewById(R.id.price_TextView);
		// 入住时间
		checkInDateFilterLayout = findViewById(R.id.check_in_date_layout);
		checkInDateFilterLayout.setOnClickListener(filterBarClickListener);
		checkInDateFilterLayoutTextView = (TextView) findViewById(R.id.check_in_date_TextView);
		// --------- filterbar -----------
		
		// --------- roomlist -----------
		// 房源列表
		roomInfoListView = (ListView) findViewById(R.id.room_info_ListView);
		roomInfoListView.setOnItemClickListener(roomListViewItemClickListener);
		roomInfoListView.setOnScrollListener(onScrollListener);
		adapterForRoomInfoListView = new RoomInfoListAdapter(this, dataSourceForRoomInfoListView);
		roomInfoListView.setAdapter(adapterForRoomInfoListView);
		// --------- roomlist -----------
		
		// --------- toolsbar -----------
		// 筛选按钮
		final Button filterButton = (Button) findViewById(R.id.room_search_bottom_screening_Button);
		filterButton.setOnClickListener(toolsBarClickListener);
		// 排序按钮
		orderFilterButton = (Button) findViewById(R.id.room_search_bottom_oder_Button);
		orderFilterButton.setOnClickListener(toolsBarClickListener);
		// 地图按钮
		final Button mapButton = (Button) findViewById(R.id.room_search_bottom_map_Button);
		mapButton.setOnClickListener(toolsBarClickListener);
		// --------- toolsbar -----------
		
		// UI校正, 因为这个Activity用于两种界面
		if (isNearbyActivity) {
			// 从 "附近"模块进入 "房源列表"
			
			adjustUIForNearby();
			registerBroadcastReceiverForUserLocation();
			userNearbyLocation = new SimpleLocationForBaiduLBS(locationDelegate, addrInfoDelegate, true);
			
		} else {
			// 从 "其他"模块进入 "房源列表"
			
			adjustUIForNormal();
			queryRoomsAndShowProgressDialogAndClearOldData(roomSearchCriteriaBundle);
		}
	}
	
	/**
	 * 校正 "附近" 界面的UI
	 */
	private void adjustUIForNearby() {
		titleBar.setVisibility(View.GONE);
		checkDataRelativeLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 校正 "非附近" 界面的UI
	 */
	private void adjustUIForNormal() {
		filterLinearLayout.setVisibility(View.GONE);
	}
	
	private boolean parseIntent(final Intent intent) {
		do {
			if (intent == null) {
				DebugLog.e(TAG, "入参 intent 为空 ! ");
				break;
			}
			
			if (intent.hasExtra(IntentExtraTagEnum.IS_NEARBY_ACTIVITY.name())) {
				isNearbyActivity = true;
			} else {
				roomSearchCriteriaBundle = intent.getBundleExtra(IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name());
				if (roomSearchCriteriaBundle == null) {
					DebugLog.e(TAG, "房源搜索条件Bundle为空 ! ");
					break;
				}
			}
			// 一切正常
			return true;
		} while (false);
		
		return false;
	}
	
	/**
	 * 判断 "工具栏" 和 "过滤栏" 是否可用
	 * 
	 * @return
	 */
	private boolean isToolsBarAndFilterBarAvailable() {
		do {
			if (roomSearchCriteriaBundle == null) {
				DebugLog.e(TAG, "isToolsBarAndFilterBarAvailable: 房源搜索条件为空! ");
				break;
			}
			
			if (isNearbyActivity) {
				if (!BaiduLbsSingleton.getInstance().gpsIsEnable()) {
					// 在附近界面时, 如果GPS没有打开, 是不允许使用 功能按钮的
					break;
				}
			}
			
			if (netRequestIndexForRoomInfo != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				// 上一个网络请求还未回来
				break;
			}
			return true;
		} while (false);
		
		return false;
	}
	
	private View.OnClickListener toolsBarClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!isToolsBarAndFilterBarAvailable()) {
				return;
			}
			
			switch (v.getId())
			{
				// "筛选"
				case R.id.room_search_bottom_screening_Button: {
					final Intent intent = new Intent(C_RoomListMainActivity.this, RoomFilterActivity.class);
					intent.putExtra(RoomFilterActivity.IntentExtraTagEnum.IS_NEARBY_ACTIVITY.name(), isNearbyActivity);
					intent.putExtra(RoomFilterActivity.IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name(), new Bundle(C_RoomListMainActivity.this.roomSearchCriteriaBundle));
					startActivityForResult(intent, IntentRequestCodeEnum.TO_ROOM_FILTER_ACTIVITY.ordinal());
				}
				break;
				
				// "排序"
				case R.id.room_search_bottom_oder_Button: {
					final List<String> list = new ArrayList<String>(GlobalConstant.dataSourceForSortTypeList.keySet());
					if (!isNearbyActivity) {
						// 当前页面是 "附近" 时, 才会显示 "距离由近到远" 这个选项.
						list.remove(list.size() - 1);
					}
					activateRadioPopWindow("排序", list, (View) orderFilterButton);
				}
				break;
				
				// "地图"
				case R.id.room_search_bottom_map_Button: {
					if (roomInfoBeanList.size() <= 0) {
						// 没有有效数据的时候, 不要进入地图Activity
						break;
					}
					final Intent intent = new Intent(C_RoomListMainActivity.this, RoomMapActivityByBaiduLBS.class);
					String cityName = "";
					RoomMapActivityByBaiduLBS.UiTypeEnum uiTypeEnum = null;
					if (isNearbyActivity) {
						// 从 "附近"模块进入 "房源列表"
						if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
							cityName = SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city;
						}
						
						uiTypeEnum = RoomMapActivityByBaiduLBS.UiTypeEnum.GROUP_ROOM_FOR_NEARBY;
					} else {
						// 从 "其他"模块进入 "房源列表"
						cityName = roomSearchCriteriaBundle.getString(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name());
						
						uiTypeEnum = RoomMapActivityByBaiduLBS.UiTypeEnum.GROUP_ROOM_FOR_CITY;
					}
					if (TextUtils.isEmpty(cityName)) {
						cityName = "定位中...";
					}
					intent.putExtra(RoomMapActivityByBaiduLBS.IntentExtraDataKeyEnum.CITY_NAME.name(), cityName);
					intent.putExtra(RoomMapActivityByBaiduLBS.IntentExtraDataKeyEnum.UI_TYPE.name(), uiTypeEnum);
					intent.putParcelableArrayListExtra(RoomMapActivityByBaiduLBS.IntentExtraDataKeyEnum.DATA.name(), (ArrayList<? extends Parcelable>) roomInfoBeanList);
					startActivity(intent);
				}
				break;
				
				default:
				break;
			}
			
		}
	};
	
	/**
	 * "筛选条"
	 */
	private View.OnClickListener filterBarClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!isToolsBarAndFilterBarAvailable()) {
				return;
			}
			
			switch (v.getId())
			{
				// "距离筛选"
				case R.id.distance_layout: {
					final List<String> list = new ArrayList<String>(GlobalConstant.dataSourceForDistanceList.keySet());
					activateRadioPopWindow("距离筛选", list, (View) distanceFilterLayout);
				}
				break;
				
				// "价格筛选"
				case R.id.price_layout: {
					final List<String> list = new ArrayList<String>(GlobalConstant.dataSourceForPriceDifferenceList.keySet());
					activateRadioPopWindow("价格筛选", list, (View) priceFilterLayout);
				}
				break;
				
				// "入住时间筛选"
				case R.id.check_in_date_layout: {
					activatePopWindowForCanlendar();
				}
				break;
				
				default:
				break;
			}
			
		}
	};
	
	/**
	 * 创建 用户附近房源 的查询条件
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	private Bundle createNearbyRoomsSearchCriteria(final double longitude, final double latitude) {
		
		if (!isNearbyActivity) {
			assert false : "只能在附近界面调用createNearbyRoomsSearchCriteria()";
			return null;
		}
		
		final Bundle bundle = new Bundle();
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name(), RoomSearchDatabaseFieldsConstant.DefaultValueForRequestBeanFieldEnum.nearby.getDefaultValueString());
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.distance.name(), RoomSearchDatabaseFieldsConstant.DefaultValueForRequestBeanFieldEnum.distance.getDefaultValueString());
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue());
		// TODO : 入住时间是否需要设置, 这里需要确认
		final java.util.Date utilDate = new java.util.Date();
		final java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name(), sqlDate.toString());
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name(), Double.toString(longitude));
		bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name(), Double.toString(latitude));
		
		return bundle;
	}
	
	/**
	 * 查询房源信息, 并且清空旧数据和显示 ProgressDialog
	 * 
	 * @param bundle
	 */
	private void queryRoomsAndShowProgressDialogAndClearOldData(final Bundle bundle) {
		
		clearListViewData();
		
		hintForNoDataTextView.setText("正在载入...");
		hintForNoDataTextView.setVisibility(View.VISIBLE);
		
		if (requestRoomInfoList(bundle) != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(C_RoomListMainActivity.this, progressDialogOnCancelListener);
		}
	}
	
	/**
	 * 查询用户附近的房源信息
	 */
	private void queryNearbyRoomsForUser() {
		
		if (!isNearbyActivity) {
			DebugLog.e(TAG, "只能在附近界面调用queryNearbyRoomsForUser()");
			return;
		}
		
		if (!BaiduLbsSingleton.getInstance().gpsIsEnable()) {
			// 先列表清空数据
			clearListViewData();
			// 然后清空查询条件
			roomSearchCriteriaBundle = null;
			
			Toast.makeText(this, "GPS关闭, 请打开GPS功能", 0).show();
			hintForNoDataTextView.setText("GPS没有打开");
			hintForNoDataTextView.setVisibility(View.VISIBLE);
			return;
		}
		
		if (dataSourceForRoomInfoListView.size() <= 0 && roomSearchCriteriaBundle == null) {// 还没有进行房源查询
		
			if (SimpleLocationForBaiduLBS.getLastLocation() != null) {
				
				// 已经定位到用户当前的位置坐标, 可以直接通过坐标查询用户附近的房源信息
				requestRoomInfoListForUserNearby();
				
			} else {
				
				// 还没有定位到用户当前附近的坐标, 先要定位用户的坐标, 只有拥有用户的坐标, 才能进行用户附近的房源查询
				userNearbyLocation.startLocationRequest();
				
				hintForNoDataTextView.setText("定位中, 请稍等...");
				hintForNoDataTextView.setVisibility(View.VISIBLE);
				
				SimpleProgressDialog.show(C_RoomListMainActivity.this, progressDialogOnCancelListener);
			}
		}
	}
	
	/**
	 * 请求房源列表信息
	 * 
	 * @param data 查询条件
	 * @return
	 */
	public int requestRoomInfoList(final Bundle data) {
		
		//
		if (data == null || data.isEmpty()) {
			DebugLog.e(TAG, "房源搜索条件为空!");
			return DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
		DebugLog.i(TAG, "searchRoom:roomSearchCriteriaBundle=" + data.toString());
		if (!isValidRoomSearchCriteria(data)) {
			DebugLog.e(TAG, "房源搜索条件无效!");
			return DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
		
		final RoomSearchNetRequestBean roomSearchNetRequestBean = new RoomSearchNetRequestBean();
		// 城市id
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name())) {
			roomSearchNetRequestBean.setCityId(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name()));
		}
		// 城市名称
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name())) {
			final String cityNamesString = data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name());
			titleBar.setTitleByString(cityNamesString);// 设置标题title
			roomSearchNetRequestBean.setCityName(cityNamesString);
		}
		// 房源排序方式
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.order.name())) {
			roomSearchNetRequestBean.setOrder(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name()));
		} else {
			// "排序方式" 字段是必须要包含的, 下面是默认情况
			if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name()) || data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name())) {
				roomSearchNetRequestBean.setOrder(GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue());// 距离由近到远
			} else {
				roomSearchNetRequestBean.setOrder(GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue());// 爱日租推荐
			}
		}
		// 距离筛选( 500 , 1000, 3000)
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name()) || data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name())) {
			// 业务逻辑说明 : 只有在附近, 后者有地标条件存在时, 才需要设置 距离
			if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.distance.name())) {
				roomSearchNetRequestBean.setDistance(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.distance.name()));
			} else {
				// 默认是3公里
				roomSearchNetRequestBean.setDistance(RoomSearchDatabaseFieldsConstant.DefaultValueForRequestBeanFieldEnum.distance.getDefaultValueString());
			}
		}
		// 地标名 (从 2.4 房间推荐 接口 可以获取, 另外可以从搜索界面中获取用户手动输入的地标.
		// 业务说明 : 带地标条件的搜索时, order默认是jla(由近及远), distance默认是3000
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name())) {
			final String streetNameString = data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name());
			titleBar.setTitleByString(streetNameString);
			roomSearchNetRequestBean.setStreetName(streetNameString);
		}
		// 入住时间
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name())) {
			final String checkinDateString = data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name());
			roomSearchNetRequestBean.setCheckinDate(checkinDateString);
			
			//
			checkinDateTextView.setText(checkinDateString);
		}
		// 退房时间
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.checkoutDate.name())) {
			final String checkoutDateString = data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.checkoutDate.name());
			roomSearchNetRequestBean.setCheckoutDate(checkoutDateString);
			
			//
			checkoutDateTextView.setText(checkoutDateString);
		}
		// 入住人数
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.occupancyCount.name())) {
			roomSearchNetRequestBean.setOccupancyCount(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.occupancyCount.name()));
		}
		// 房间编号
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.roomNumber.name())) {
			roomSearchNetRequestBean.setRoomNumber(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.roomNumber.name()));
		}
		// 价格区间 (0-100, 100-200, 200-300, 300 :300以上传300)
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name())) {
			roomSearchNetRequestBean.setPriceDifference(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name()));
		}
		
		// 区名称
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name())) {
			roomSearchNetRequestBean.setDistrictName(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name()));
		}
		// 区ID
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name())) {
			roomSearchNetRequestBean.setDistrictId(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name()));
		}
		// 房屋类型(可在 2.8 初始化字典 接口获取)
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name())) {
			roomSearchNetRequestBean.setRoomType(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name()));
		}
		
		// 出租方式(可在 2.8 初始化字典接口获取)
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name())) {
			roomSearchNetRequestBean.setRentType(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name()));
		}
		// 设施设备(可在 2.8 初始化字典接口获取)
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.tamenities.name())) {
			roomSearchNetRequestBean.setTamenities(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.tamenities.name()));
		}
		
		// 设置是否是搜索 "附近" 的房源
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name())) {
			roomSearchNetRequestBean.setNearby(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name()));
		}
		// 设置 经纬度
		if (data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name()) && data.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name())) {
			roomSearchNetRequestBean.setLocationLat(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name()));
			roomSearchNetRequestBean.setLocationLng(data.getString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name()));
		}
		
		// 设置 房源查询起始索引
		roomSearchNetRequestBean.setOffset(Integer.toString(this.roomSearchStartIndex));
		roomSearchNetRequestBean.setMax(Integer.toString(this.roomSearchMaxNumber));
		
		if (netRequestIndexForRoomInfo != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			DomainProtocolNetHelperSingleton.getInstance().cancelNetRequestByRequestIndex(netRequestIndexForRoomInfo);
		}
		netRequestIndexForRoomInfo = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(C_RoomListMainActivity.this, roomSearchNetRequestBean, NetRequestTagEnum.ROOM_SEARCH,
				domainNetRespondCallback);
		
		return netRequestIndexForRoomInfo;
	}
	
	/**
	 * 检测房源搜索条件是否有效
	 * 
	 * @param bundle 房源搜索条件
	 * @return
	 */
	private boolean isValidRoomSearchCriteria(final Bundle bundle) {
		
		do {
			if (bundle == null) {
				DebugLog.e(TAG, "isValidRoomSearchCriteria:入参为空");
				break;
			}
			
			if (isNearbyActivity) {
				if (!bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name())) {
					DebugLog.e(TAG, "丢失 nearby 字段");
					bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name(), RoomSearchDatabaseFieldsConstant.DefaultValueForRequestBeanFieldEnum.nearby.getDefaultValueString());
				}
				
				if (!bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name()) || !bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name())) {
					DebugLog.e(TAG, "丢失 locationLat || locationLng 字段");
					
					final Location userLocation = SimpleLocationForBaiduLBS.getLastLocation();
					if (userLocation != null) {
						final Double latitude = Double.valueOf(userLocation.getLatitude());
						final Double longitude = Double.valueOf(userLocation.getLongitude());
						bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name(), latitude.toString());
						bundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name(), longitude.toString());
					} else {
						break;
					}
				}
				
			} else {
				
				if (bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name())) {
					DebugLog.e(TAG, "nearby 是多余的! ");
					bundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name());
				}
				
				if (!bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name()) && !bundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name())) {
					DebugLog.e(TAG, "丢失字段  cityId 和 cityName");
					break;
				}
			}
			
			DebugLog.i(TAG, "搜索条件有效! ");
			return true;
		} while (false);
		
		DebugLog.e(TAG, "搜索条件无效! ");
		return false;
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI_FOR_ROOM_INFO_LIST
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(C_RoomListMainActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				if (roomSearchStartIndex <= 0) {
					hintForNoDataTextView.setText("暂无数据");
					hintForNoDataTextView.setVisibility(View.VISIBLE);
				}
				
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(C_RoomListMainActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI_FOR_ROOM_INFO_LIST.ordinal()) {
				
				hintForNoDataTextView.setVisibility(View.GONE);
				roomTotalTextView.setText(Integer.toString(roomCountInt));
				
				// 一定要在 UI主线程 中更新ListView适配器的数据源, 否则会崩溃
				dataSourceForRoomInfoListView.addAll(newDataForRoomInfoList);
				newDataForRoomInfoList = null;
				
				// 通知DataAdapter 更新数据, 用于翻屏的时候.
				adapterForRoomInfoListView.notifyDataSetChanged();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ROOM_SEARCH == requestEvent) {
			netRequestIndexForRoomInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
	}
	
	private IDomainNetRespondCallback domainNetRespondCallback = new IDomainNetRespondCallback() {
		@Override
		public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean) {
			DebugLog.i(TAG, "domainNetRespondHandleInNonUIThread --- start ! ");
			clearNetRequestIndexByRequestEvent(requestEvent);
			
			if (errorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal();
				msg.getData().putString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name(), errorBean.getErrorMessage());
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.ROOM_SEARCH) {// 2.5房间搜索
			
				final RoomSearchNetRespondBean roomSearchNetRespondBean = (RoomSearchNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, roomSearchNetRespondBean.toString());
				// 房源数量
				roomCountInt = Integer.parseInt(roomSearchNetRespondBean.getRoomCount());
				// 新请求回来的房源信息
				final List<RoomInfo> newRoomInfoList = roomSearchNetRespondBean.getRoomList();
				// 记录新的业务Bean
				roomInfoBeanList.addAll(newRoomInfoList);
				// 只有当网络请求反回有效数据时, 才要累加 page 计数器
				roomSearchStartIndex = roomInfoBeanList.size();
				
				// 将新的房源信息列表, 解析成 ListView 的适配器数据源
				newDataForRoomInfoList = paserNewRoomInfoListToListViewAdapterSource(newRoomInfoList);
				
				if (roomInfoBeanList.size() >= roomCountInt) {
					// 已经是最后一屏数据
					isLastRow = true;
				}
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI_FOR_ROOM_INFO_LIST.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	/**
	 * 判断是否需要在 ListView 的 Item 中显示 "房源距离" 字段
	 * 
	 * @param roomSearchCriteriaBundle 房间搜索条件
	 * @return
	 */
	private boolean isNeedShowDistanceFieldInRoomInfoListViewItem(final Bundle roomSearchCriteriaBundle) {
		if (roomSearchCriteriaBundle == null) {
			// 房源搜索条件为空, 异常情况
			return false;
		}
		
		do {
			if (roomSearchCriteriaBundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name())) {
				break;
			}
			if (roomSearchCriteriaBundle.containsKey(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name())) {
				break;
			}
			return false;
		} while (false);
		
		return true;
	}
	
	// 解析返回的数据
	private List<Map<String, String>> paserNewRoomInfoListToListViewAdapterSource(final List<RoomInfo> roomInfoList) {
		
		final List<Map<String, String>> dataSource = new ArrayList<Map<String, String>>();
		if (roomInfoList != null) {
			for (RoomInfo roomInfo : roomInfoList) {
				final Map<String, String> map = new HashMap<String, String>();
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_IMAGE.name(), roomInfo.getImage());// 房间图片
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_IS_VERIFY.name(), roomInfo.getVerify());// 当前房间是否是 100%验证的
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_TITLE.name(), roomInfo.getRoomTitle());// 房间标题
				// 判断是否将距离加入列表 根据地标
				if (isNeedShowDistanceFieldInRoomInfoListViewItem(roomSearchCriteriaBundle)) {
					map.put(RoomInfoListAdapter.DataSourceKeyEnum.DISTANCE_TO_TARGET.name(), roomInfo.getDistance());// 当前房间与目标的距离
				}
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_RENTAL_WAY_NAME.name(), roomInfo.getRentalWayName());// 当前房间的租住方式
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_CHECK_AMOUNT.name(), roomInfo.getOccupancyCount());// 当前房间限住人数
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_REVIEW_COUNT.name(), roomInfo.getReviewCount());// 当前房间评论数
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_SCHEDULED_TOTAL.name(), roomInfo.getScheduled());// 该房间曾被预定的次数
				map.put(RoomInfoListAdapter.DataSourceKeyEnum.ROOM_PRICE.name(), roomInfo.getPrice());// 房间单价
				
				dataSource.add(map);
			}
		}
		
		return dataSource;
	}
	
	private OnItemClickListener roomListViewItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			do {
				final RoomInfo roomInfo = roomInfoBeanList.get(position);
				if (roomInfo == null) {
					break;
				}
				final String roomNumberString = roomInfo.getRoomId();
				if (TextUtils.isEmpty(roomNumberString)) {
					break;
				}
				
				// 跳转 "房间详情" 界面
				final Intent intent = new Intent(C_RoomListMainActivity.this, RoomDetailOfBasicInformationActivity.class);
				intent.putExtra(RoomDetailOfBasicInformationActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberString);
				startActivity(intent);
			} while (false);
		}
	};
	
	// 这个方法是为列表添加滚动监听
	private OnScrollListener onScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState)
			{
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE: {
					// 判断滚动到底部
					do {
						final int listViewItemTotal = view.getCount();
						if (listViewItemTotal <= 0) {
							// listview 为空
							break;
						}
						if (netRequestIndexForRoomInfo != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
							// 本次网络数据还未请求回来, 此时重新显示 ProgressDialog
							if (!SimpleProgressDialog.isShowing()) {
								SimpleProgressDialog.show(C_RoomListMainActivity.this, progressDialogOnCancelListener);
							}
							break;
						}
						final int listViewLastVisiblePosition = view.getLastVisiblePosition();
						if (listViewLastVisiblePosition != (listViewItemTotal - 1)) {
							// 没滑到列表最后一行
							break;
						}
						
						DebugLog.i(TAG, "OnScrollListener->LastScroll");
						
						// listview 已经滚到了最后了一行
						if (isLastRow) {
							Toast.makeText(C_RoomListMainActivity.this, "已经是最后一屏数据了!", 0).show();
						} else {
							if (requestRoomInfoList(roomSearchCriteriaBundle) != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
								if (!SimpleProgressDialog.isShowing()) {
									SimpleProgressDialog.show(C_RoomListMainActivity.this, progressDialogOnCancelListener);
								}
							}
						}
						
					} while (false);
				}
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isNearbyActivity) {
			
			// 跳转到 推荐首页
			ToolsFunctionForThisProgect.showHomePageForFirstLevelActivity(this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		DebugLog.i(TAG, "onActivityResult");
		
		if (requestCode == IntentRequestCodeEnum.TO_ROOM_FILTER_ACTIVITY.ordinal()) {
			do {
				if (resultCode != RESULT_OK) {
					break;
				}
				if (data == null) {
					// 异常
					break;
				}
				final Bundle bundle = data.getBundleExtra(IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name());
				if (bundle == null) {
					// 异常
					break;
				}
				DebugLog.i(TAG, "new room search criteria bundle -->" + bundle.toString());
				if (!isRoomSearchCriteriaChanged(bundle)) {
					break;
				}
				
				// 重新查询房源信息
				roomSearchCriteriaBundle = bundle;
				queryRoomsAndShowProgressDialogAndClearOldData(roomSearchCriteriaBundle);
			} while (false);
		}
	}
	
	/**
	 * 清理掉 房源列表 的数据
	 */
	private void clearListViewData() {
		// 清理掉房源列表的旧数据
		roomInfoBeanList.clear();
		dataSourceForRoomInfoListView.clear();
		newDataForRoomInfoList = null;
		adapterForRoomInfoListView.notifyDataSetChanged();
		isLastRow = false;
		
		// 重置房源总数量
		roomCountInt = 0;
		final TextView roomTotalTextView = (TextView) findViewById(R.id.room_amount_TextView);
		if (roomTotalTextView != null) {
			roomTotalTextView.setText(Integer.toString(roomCountInt));
		}
		
		// 房源搜索的开始条目索引
		roomSearchStartIndex = 0;
	}
	
	/**
	 * 检查 "房间搜索条件" 是否有变化
	 * 
	 * @param newRoomSearchCriteriaBundle
	 * @return
	 */
	private boolean isRoomSearchCriteriaChanged(Bundle newRoomSearchCriteriaBundle) {
		
		if (newRoomSearchCriteriaBundle == null || roomSearchCriteriaBundle == null) {
			assert false : "newRoomSearchCriteriaBundle == null || roomSearchCriteriaBundle == null";
			return false;
		}
		
		do {
			
			String key = "";
			
			key = RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.checkoutDate.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.occupancyCount.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.roomNumber.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.offset.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.max.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.order.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.tamenities.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.distance.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			key = RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name();
			if (!TextUtils.equals(newRoomSearchCriteriaBundle.getString(key), roomSearchCriteriaBundle.getString(key))) {
				break;
			}
			
			// 搜索条没有变化
			return false;
		} while (false);
		
		// 搜索条件有变化
		return true;
	}
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private synchronized void requestRoomInfoListForUserNearby() {
		final Location location = GlobalDataCacheForMemorySingleton.getInstance().getLastLocation();
		if (location != null) {
			roomSearchCriteriaBundle = createNearbyRoomsSearchCriteria(location.getLongitude(), location.getLatitude());
			queryRoomsAndShowProgressDialogAndClearOldData(roomSearchCriteriaBundle);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private SimpleLocationForBaiduLBS.LocationDelegate locationDelegate = new SimpleLocationForBaiduLBS.LocationDelegate() {
		
		@Override
		public void locationCallback(Location location) {
			unregisterReceiverForUserLocation();
			requestRoomInfoListForUserNearby();
			
			// 关掉属于 LocationDelegate 的 ProgressDialog
			SimpleProgressDialog.dismiss(C_RoomListMainActivity.this);
		}
	};
	
	private SimpleLocationForBaiduLBS.AddrInfoDelegate addrInfoDelegate = new SimpleLocationForBaiduLBS.AddrInfoDelegate() {
		
		@Override
		public void addrInfoCallback(MKAddrInfo addrInfo) {
			// 发送 "获取用户当前地址信息成功" 的广播消息
			final Intent intent = new Intent();
			intent.setAction(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name());
			C_RoomListMainActivity.this.sendBroadcast(intent);
		}
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BroadcastReceiverForRoomListActivity broadcastReceiver;
	
	// 接收获取用户当前位置坐标的广播消息
	private class BroadcastReceiverForRoomListActivity extends BroadcastReceiver {
		
		public BroadcastReceiverForRoomListActivity() {
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			DebugLog.i(TAG, "BroadcastReceiverForRoomListActivity:onReceive");
			
			if (intent.getAction().equals(GlobalConstant.UserActionEnum.GET_USER_LOCATION_SUCCESS.name())) {
				userNearbyLocation.cancelLocationDelegate();
				
				requestRoomInfoListForUserNearby();
				
				// 关掉属于 LocationDelegate 的 ProgressDialog
				SimpleProgressDialog.dismiss(C_RoomListMainActivity.this);
			}
		}
	}
	
	private void registerBroadcastReceiverForUserLocation() {
		broadcastReceiver = new BroadcastReceiverForRoomListActivity();
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalConstant.UserActionEnum.GET_USER_LOCATION_SUCCESS.name());
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	private void unregisterReceiverForUserLocation() {
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 在Activity退出前一定要 dismiss所有的 PopupWindow, 否则会在App被强制关闭时, 会报下面的异常
	// Activity cn.airizu.activity.main_navigation.MainNavigationActivity
	// has leaked window android.widget.PopupWindow$PopupViewContainer@41053418 that was originally added here
	private void dismissAllPopupWindowForThisActivity() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		if (popupWindowForCanlendar != null) {
			popupWindowForCanlendar.dismiss();
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private PopupWindow popupWindow;
	private RadioPopList radioPopList;
	
	// 激活一个单选弹出对话框
	private void activateRadioPopWindow(String title, List<String> dataSource, View triggerView) {
		
		if (popupWindow == null) {
			
			// 加载popupWindow的布局文件
			radioPopList = new RadioPopList(this, radioPopListDelegate);
			
			// 创建一个 PopupWindow
			popupWindow = new PopupWindow(radioPopList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.setFocusable(true);
		}
		
		radioPopList.setTitle(title);
		radioPopList.setTag(triggerView);
		final RadioPopListAdapter radioPopListAdapter = new RadioPopListAdapter(this, dataSource);
		radioPopList.setAdapter(radioPopListAdapter);
		popupWindow.showAsDropDown(titleBar);
	}
	
	private CustomControlDelegate radioPopListDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			final RadioPopList.ActionEnum actionEnum = (RadioPopList.ActionEnum) actionTypeEnum;
			switch (actionEnum)
			{
				case TITLE_LEFT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				case TITLE_RIGHT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				case LIST_ITEM_CLICK: {
					do {
						final View view = (View) contorl;
						if (view == null) {
							break;
						}
						final TextView contentTextView = (TextView) view.findViewById(R.id.content_TextView);
						if (contentTextView == null) {
							break;
						}
						final String mapKey = contentTextView.getText().toString();
						if (TextUtils.isEmpty(mapKey)) {
							break;
						}
						final Object triggerView = radioPopList.getTag();
						if (triggerView == null) {
							break;
						}
						
						String domainKey = "";
						
						Map<String, String> adapterSource = null;
						TextView textViewForThisPopupWindow = null;
						if (triggerView.equals(orderFilterButton)) {
							domainKey = RoomSearchDatabaseFieldsConstant.RequestBean.order.name();
							adapterSource = GlobalConstant.dataSourceForSortTypeList;
						} else if (triggerView.equals(distanceFilterLayout)) {
							domainKey = RoomSearchDatabaseFieldsConstant.RequestBean.distance.name();
							adapterSource = GlobalConstant.dataSourceForDistanceList;
							textViewForThisPopupWindow = distanceFilterTextView;
						} else if (triggerView.equals(priceFilterLayout)) {
							domainKey = RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name();
							adapterSource = GlobalConstant.dataSourceForPriceDifferenceList;
							textViewForThisPopupWindow = priceFilterLayoutTextView;
						}
						String domainValue = adapterSource.get(mapKey);
						if (TextUtils.isEmpty(domainValue)) {
							break;
						}
						if (textViewForThisPopupWindow != null) {
							textViewForThisPopupWindow.setText(mapKey);
						}
						roomSearchCriteriaBundle.putString(domainKey, domainValue);
						
						// 开始搜索目标房源
						queryRoomsAndShowProgressDialogAndClearOldData(roomSearchCriteriaBundle);
					} while (false);
					popupWindow.dismiss();
				}
				break;
				default:
				break;
			}
		}
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private PopupWindow popupWindowForCanlendar;
	
	private void activatePopWindowForCanlendar() {
		if (popupWindowForCanlendar == null) {
			
			// 加载popupWindow的布局文件
			final int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
			final int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
			final CustomCalendar calendarView = new CustomCalendar(this, width, height);
			calendarView.setTitle("入住时间");
			calendarView.setDelegate(calendarDelegate);
			
			// 声明一个弹出框
			popupWindowForCanlendar = new PopupWindow(calendarView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			// 下面这两句必须这个顺序--》点击外围关闭弹出
			popupWindowForCanlendar.setBackgroundDrawable(new BitmapDrawable());
			popupWindowForCanlendar.setOutsideTouchable(true);
			popupWindowForCanlendar.setAnimationStyle(R.style.PopupAnimation);
			popupWindowForCanlendar.setFocusable(true);
		}
		
		popupWindowForCanlendar.showAsDropDown(titleBar);
	}
	
	private CustomControlDelegate calendarDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			final CustomCalendar.ACTION actionEnum = (CustomCalendar.ACTION) actionTypeEnum;
			switch (actionEnum)
			{
				case ACTION_CANCEL_BUTTON_CLICK: {
					popupWindowForCanlendar.dismiss();
				}
				break;
				case ACTION_OK_BUTTON_CLICK: {
					popupWindowForCanlendar.dismiss();
				}
				break;
				case ACTION_DAY_CLICK: {
					final DayCellForCustomCalendar dayCell = (DayCellForCustomCalendar) contorl;
					final java.sql.Date date = new java.sql.Date(dayCell.getDate().getTimeInMillis());
					final String dateStringForSql = date.toString();
					checkInDateFilterLayoutTextView.setText(dateStringForSql);
					roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name(), dateStringForSql);
					
					// 开始搜索目标房源
					queryRoomsAndShowProgressDialogAndClearOldData(roomSearchCriteriaBundle);
					popupWindowForCanlendar.dismiss();
				}
				break;
				default:
				break;
			}
		}
	};
}
