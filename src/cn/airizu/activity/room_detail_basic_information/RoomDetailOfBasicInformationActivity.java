package cn.airizu.activity.room_detail_basic_information;

import cn.airizu.activity.R;
import cn.airizu.activity.free_book_confirm_checkin_time.FreebookConfirmCheckinTimeActivity;
import cn.airizu.activity.room_detail_overview.RoomDetailOfOverviewActivity;
import cn.airizu.activity.room_detail_photo.RoomDetailPhotoActivity;
import cn.airizu.activity.room_detail_tenant_reviews.RoomDetailTenantReviewsActivity;
import cn.airizu.activity.room_map.RoomMapActivityByBaiduLBS;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RoomPhotoGalleryAdapter;
import cn.airizu.custom_control.page_change_light.PageChangeLight;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRequestBean;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 房间详情 - 基础信息
 * 
 * @author zhihua.tang
 */
public class RoomDetailOfBasicInformationActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	// 业务说明 : 可以从如下入口进入 订单详情-基础信息 界面
	// 1. 从房源列表页面, 选中一个房间时;
	// 2. 从 "直接搜索房间编号" 界面, 进行房间编号查询时;
	// 3. 从 "订单详情界面"
	public static enum IntentExtraTagEnum {
		// 房间编号
		ROOM_NUMBER
	};
	
	private String roomNumberString;
	private RoomDetailNetRespondBean roomDetailNetRespondBean;
	
	private enum NetRequestTagEnum {
		// 2.12 房间详情
		ROOM_DETAIL
	};
	
	private int netRequestIndexForRoomDetail = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	//
	private PageChangeLight pageChangeLight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		
		if (!parseIntent(getIntent())) {
			return;
		} else {
			updateProLoadedUIInfoByIntentExtraData();
		}
		
		requestRoomDetailByRoomNumber(roomNumberString);
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
		
		SimpleProgressDialog.resetByThisContext(this);
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		
		super.onStop();
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			roomNumberString = intent.getStringExtra(IntentExtraTagEnum.ROOM_NUMBER.name());
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("房间基本信息");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void updateProLoadedUIInfoByIntentExtraData() {
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		if (titleBar != null) {
			titleBar.setTitleByString("房间 : " + roomNumberString);
		}
	}
	
	private void loadRealUIAndUseRoomDetailNetRespondBeanInitialize(RoomDetailNetRespondBean roomDetailNetRespondBean) {
		if (roomDetailNetRespondBean == null) {
			return;
		}
		
		setContentView(R.layout.room_detail_basic_information_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		String titleBarTitleString = "房间 : " + roomDetailNetRespondBean.getNumber();
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 房间图片画廊上的房间单价
		final TextView roomPriceInRoomPhotoGalleryTextView = (TextView) findViewById(R.id.room_price_on_the_gallery_TextView);
		roomPriceInRoomPhotoGalleryTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(roomDetailNetRespondBean.getPrice()));
		//
		pageChangeLight = (PageChangeLight) findViewById(R.id.page_change_light_PageChangeLight);
		pageChangeLight.setIndicatorCount(roomDetailNetRespondBean.getImageM().size());
		
		// 房间图片 "画廊"
		final Gallery roomPhotoGallery = (Gallery) findViewById(R.id.room_photo_Gallery);
		roomPhotoGallery.setSpacing(60);
		roomPhotoGallery.setOnItemClickListener(roomPhotoGalleryOnItemClickListener);
		roomPhotoGallery.setAdapter(new RoomPhotoGalleryAdapter(this, roomDetailNetRespondBean.getImageS(), ScaleType.FIT_XY));
		roomPhotoGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pageChangeLight.setHightlightIndicator(position);
			}
		});
		
		// 房间交通地图
		final Button roomtTafficMapButton = (Button) findViewById(R.id.room_address_map_Button);
		roomtTafficMapButton.setOnClickListener(onClickListener);
		// 免费预订
		final Button freebookButton = (Button) findViewById(R.id.freebook_Button);
		freebookButton.setOnClickListener(onClickListener);
		// 房间详情
		final View roomDetailClickView = findViewById(R.id.room_detail_click_layout);
		roomDetailClickView.setOnClickListener(onClickListener);
		// 免费客服电话
		final View freeCustomerServiceTelephoneClickView = findViewById(R.id.free_customer_service_telephone_click_layout);
		freeCustomerServiceTelephoneClickView.setOnClickListener(onClickListener);
		
		// 房间标题
		final TextView roomTitleTextView = (TextView) findViewById(R.id.room_title_TextView);
		roomTitleTextView.setText(roomDetailNetRespondBean.getTitle());
		// 房间地址
		final TextView roomAddressTextView = (TextView) findViewById(R.id.room_address_TextView);
		roomAddressTextView.setText(roomDetailNetRespondBean.getAddress());
		// 房间编号
		final TextView roomNumberContentTextView = (TextView) findViewById(R.id.room_number_content_TextView);
		roomNumberContentTextView.setText(roomDetailNetRespondBean.getNumber());
		// 房间被预定次数
		final TextView roomScheduledContentTextView = (TextView) findViewById(R.id.room_scheduled_content_TextView);
		roomScheduledContentTextView.setText(roomDetailNetRespondBean.getScheduled() + "晚");
		// 房屋类型
		final TextView propertyTypeContentTextView = (TextView) findViewById(R.id.property_type_content_TextView);
		propertyTypeContentTextView.setText(roomDetailNetRespondBean.getPropertyType());
		// 租住方式
		final TextView privacyContentTextView = (TextView) findViewById(R.id.privacy_content_TextView);
		privacyContentTextView.setText(roomDetailNetRespondBean.getPrivacy());
		// 卫生间数
		final TextView bathRoomNumberContentTextView = (TextView) findViewById(R.id.bath_room_number_content_TextView);
		bathRoomNumberContentTextView.setText(roomDetailNetRespondBean.getBathRoomNum());
		// 卧室数
		final TextView bedRoomNumberContentTextView = (TextView) findViewById(R.id.bed_room_number_content_TextView);
		bedRoomNumberContentTextView.setText(roomDetailNetRespondBean.getBedRoom());
		// 可入住人数
		final TextView accommodatesContentTextView = (TextView) findViewById(R.id.accommodates_content_TextView);
		accommodatesContentTextView.setText(roomDetailNetRespondBean.getAccommodates());
		// 床型
		final TextView bedTypeContentTextView = (TextView) findViewById(R.id.bed_type_content_TextView);
		bedTypeContentTextView.setText(roomDetailNetRespondBean.getBedType());
		// 床数
		final TextView bedsTypeContentTextView = (TextView) findViewById(R.id.beds_content_TextView);
		bedsTypeContentTextView.setText(roomDetailNetRespondBean.getBeds());
		// 建筑面积
		final TextView constructionAreaContentTextView = (TextView) findViewById(R.id.construction_area_content_TextView);
		constructionAreaContentTextView.setText(roomDetailNetRespondBean.getSize());
		// 退房时间
		final TextView checkoutTimeContentTextView = (TextView) findViewById(R.id.checkout_time_content_TextView);
		checkoutTimeContentTextView.setText(roomDetailNetRespondBean.getCheckOutTime());
		// 最少天数
		final TextView minNightsContentTextView = (TextView) findViewById(R.id.min_nights_content_TextView);
		minNightsContentTextView.setText(roomDetailNetRespondBean.getMinNights());
		
		// 租客点评模块
		final View tenantReviewsLayout = findViewById(R.id.tenant_reviews_layout);
		final String reviewContentString = roomDetailNetRespondBean.getReviewContent();
		if (TextUtils.isEmpty(reviewContentString) || reviewContentString.equals("0")) {
			tenantReviewsLayout.setVisibility(View.GONE);
		} else {
			// 租客点评
			final View tenantReviewsClickView = findViewById(R.id.tenant_reviews_click_layout);
			tenantReviewsClickView.setOnClickListener(onClickListener);
			// 租客点评总分
			final TextView averageContentTextView = (TextView) findViewById(R.id.average_content_TextView);
			averageContentTextView.setText(roomDetailNetRespondBean.getReview() + "分");
			// 租客点评总条数
			final TextView numberOfArgumentsTextView = (TextView) findViewById(R.id.number_of_arguments_TextView);
			numberOfArgumentsTextView.setText(roomDetailNetRespondBean.getReviewCount() + "条评论");
			// 租客点评列表，这里只显示1条记录
			final TextView latestReviewsTextView = (TextView) findViewById(R.id.latest_reviews_TextView);
			latestReviewsTextView.setText(roomDetailNetRespondBean.getReviewContent());
		}
		
		// 房间特色模块
		final View roomFeaturesLayout = findViewById(R.id.room_features_layout);
		boolean isHasRoomFeatures = false;
		// 100%验证
		final View hundredPercentVerificationLayout = findViewById(R.id.hundred_percent_verification_layout);
		final TextView hundredPercentVerificationHintTextView = (TextView) findViewById(R.id.hundred_percent_verification_hint_TextView);
		if (!TextUtils.isEmpty(roomDetailNetRespondBean.getVerifyContent())) {
			isHasRoomFeatures = true;
			hundredPercentVerificationLayout.setVisibility(View.VISIBLE);
			hundredPercentVerificationHintTextView.setText(roomDetailNetRespondBean.getVerifyContent());
		}
		// 特价
		final View specialOfferLayout = findViewById(R.id.special_offer_layout);
		final TextView specialOfferHintTextView = (TextView) findViewById(R.id.special_offer_hint_TextView);
		if (!TextUtils.isEmpty(roomDetailNetRespondBean.getSpecials())) {
			isHasRoomFeatures = true;
			specialOfferLayout.setVisibility(View.VISIBLE);
			specialOfferHintTextView.setText(roomDetailNetRespondBean.getSpecials());
		}
		// 速定
		final View speedStableLayout = findViewById(R.id.speed_stable_layout);
		final TextView speedStableHintTextView = (TextView) findViewById(R.id.speed_stable_hint_TextView);
		if (!TextUtils.isEmpty(roomDetailNetRespondBean.getSpeedContent())) {
			isHasRoomFeatures = true;
			speedStableLayout.setVisibility(View.VISIBLE);
			speedStableHintTextView.setText(roomDetailNetRespondBean.getSpeedContent());
		}
		if (!isHasRoomFeatures) {
			roomFeaturesLayout.setVisibility(View.GONE);
		}
		
		// 广告
		final TextView introductionHintTextView = (TextView) findViewById(R.id.introduction_hint_TextView);
		introductionHintTextView.setText(roomDetailNetRespondBean.getIntroduction());
		
		// 房间单价
		final TextView priceTextView = (TextView) findViewById(R.id.price_TextView);
		priceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(roomDetailNetRespondBean.getPrice()));
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	private AdapterView.OnItemClickListener roomPhotoGalleryOnItemClickListener = new AdapterView.OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Intent intent = new Intent(RoomDetailOfBasicInformationActivity.this, RoomDetailPhotoActivity.class);
			intent.putExtra(RoomDetailPhotoActivity.IntentExtraTagEnum.CURRENTLY_BROWSING_PHOTO_INDEX.name(), position);
			intent.putExtra(RoomDetailPhotoActivity.IntentExtraTagEnum.ROOM_DETAIL_BEAN.name(), roomDetailNetRespondBean);
			startActivityForResult(intent, 0);
		}
	};
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (roomDetailNetRespondBean == null) {
				return;
			}
			
			switch (v.getId())
			{
				// 房间交通地图
				case R.id.room_address_map_Button: {
					final Intent intent = new Intent(RoomDetailOfBasicInformationActivity.this, RoomMapActivityByBaiduLBS.class);
					intent.putExtra(RoomMapActivityByBaiduLBS.IntentExtraDataKeyEnum.UI_TYPE.name(), RoomMapActivityByBaiduLBS.UiTypeEnum.SINGLE_ROOM);
					intent.putExtra(RoomMapActivityByBaiduLBS.IntentExtraDataKeyEnum.DATA.name(), roomDetailNetRespondBean);
					startActivity(intent);
				}
				break;
				
				// 免费预订
				case R.id.freebook_Button: {
					final Intent intent = new Intent(RoomDetailOfBasicInformationActivity.this, FreebookConfirmCheckinTimeActivity.class);
					intent.putExtra(FreebookConfirmCheckinTimeActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberString);
					startActivity(intent);
				}
				break;
				
				// 房间概述
				case R.id.room_detail_click_layout: {
					final Intent intent = new Intent(RoomDetailOfBasicInformationActivity.this, RoomDetailOfOverviewActivity.class);
					intent.putExtra(RoomDetailOfOverviewActivity.IntentExtraTagEnum.ROOM_DETAIL_BEAN.name(), roomDetailNetRespondBean);
					startActivity(intent);
				}
				break;
				
				// 租客点评
				case R.id.tenant_reviews_click_layout: {
					final Intent intent = new Intent(RoomDetailOfBasicInformationActivity.this, RoomDetailTenantReviewsActivity.class);
					intent.putExtra(RoomDetailTenantReviewsActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberString);
					intent.putExtra(RoomDetailTenantReviewsActivity.IntentExtraTagEnum.ROOM_PRICE.name(), roomDetailNetRespondBean.getPrice());
					startActivity(intent);
				}
				break;
				
				// 免费客服电话
				case R.id.free_customer_service_telephone_click_layout: {
					Toast.makeText(RoomDetailOfBasicInformationActivity.this, "拨打电话...", 0).show();
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	private void requestRoomDetailByRoomNumber(String roomNumber) {
		if (TextUtils.isEmpty(roomNumber)) {
			return;
		}
		
		final RoomDetailNetRequestBean roomDetailNetRequestBean = new RoomDetailNetRequestBean(roomNumber);
		netRequestIndexForRoomDetail = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, roomDetailNetRequestBean, NetRequestTagEnum.ROOM_DETAIL, domainNetRespondCallback);
		if (netRequestIndexForRoomDetail != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void loadNetErrorProcessUIAndInitialize(String netErrorMessageString) {
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		if (infoLabelForPreLoadedUiTextView != null || !TextUtils.isEmpty(netErrorMessageString)) {
			infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
			infoLabelForPreLoadedUiTextView.setText(netErrorMessageString);
		}
		final Button functionButtonForPreloadedUiButton = (Button) findViewById(R.id.function_button_for_preloaded_ui_Button);
		if (functionButtonForPreloadedUiButton != null) {
			functionButtonForPreloadedUiButton.setVisibility(View.VISIBLE);
			functionButtonForPreloadedUiButton.setText("刷新");
			functionButtonForPreloadedUiButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					requestRoomDetailByRoomNumber(roomNumberString);
				}
			});
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(RoomDetailOfBasicInformationActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessage);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				
				loadRealUIAndUseRoomDetailNetRespondBeanInitialize(roomDetailNetRespondBean);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ROOM_DETAIL == requestEvent) {
			netRequestIndexForRoomDetail = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.ROOM_DETAIL) {// 2.12 房间详情
			
				roomDetailNetRespondBean = (RoomDetailNetRespondBean) respondDomainBean;
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
}
