package cn.airizu.activity.user_order_detail;

import cn.airizu.activity.R;
import cn.airizu.activity.room_detail_basic_information.RoomDetailOfBasicInformationActivity;
import cn.airizu.activity.user_order_main.UserOrderMainActivity;
import cn.airizu.activity.write_review.WriteReviewActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.order_cancel.OrderCancelNetRequestBean;
import cn.airizu.domain.protocol.order_detail.OrderDetailNetRequestBean;
import cn.airizu.domain.protocol.order_detail.OrderDetailNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleImageLoader;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 订单详情(包括订单详情的5个界面)
 * 
 * @author zhihua.tang
 */
public class UserOrderDetailActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	// 业务说明: 会有2个入口进入 "订单详情界面"
	// 1. 提交一个新订单, 并且下单成功时, 会进入此界面, 此时传递过来的是 ORDER_DETAIL_FROM_SUBMIT_ORDER, 此时不需要再联网获取订单详情了.
	// 2. 从 "我的订单" 页面, 选择一个已经存在的订单后, 进入此界面, 此时传递过来的是 ORDER_ID, 此时需要联网获取订单详情.
	public static enum IntentExtraTagMenu {
		// 订单ID
		ORDER_ID,
		// 提交订单后返回的订单详情数据
		ORDER_DETAIL_FROM_SUBMIT_ORDER
	};
	
	private static enum NetRequestTagEnum {
		// 2.23 查看订单详情
		ORDER_DETAIL,
		// 2.24 取消订单
		ORDER_CANCEL
	};
	
	private static enum IntentRequestCodeEnum {
		// 使用 startActivityForResult 启动 "订单评论界面"
		TO_WRITE_REVIEW_ACTIVITY
	};
	
	private String orderIdString = "";
	private OrderDetailNetRespondBean orderDetailNetRespondBean;
	private boolean isNewOrder = false;
	private int netRequestIndexForOrderDetail = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForOrderCancel = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		// 订单详情的默认界面(真正的界面, 会在获取订单状态后, 再进行加载)
		setContentView(R.layout.user_order_detail_default_ui);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("订单详情");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			return;
		}
		
		// 更新title的标题
		titleBar.setTitleByString("订单 : " + orderIdString);
		
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
		netRequestIndexForOrderDetail = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		netRequestIndexForOrderCancel = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DebugLog.i(TAG, "onKeyDown");
		gotoUserOrderMainActivity();
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult --> start ! ");
		
		if (resultCode != RESULT_OK) {
			return;
		}
		
		if (requestCode == IntentRequestCodeEnum.TO_WRITE_REVIEW_ACTIVITY.ordinal()) {
			gotoUserOrderMainActivity();
		}
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				DebugLog.e(TAG, "intent is null ! ");
				break;
			}
			
			orderIdString = intent.getStringExtra(IntentExtraTagMenu.ORDER_ID.name());
			orderDetailNetRespondBean = (OrderDetailNetRespondBean) intent.getSerializableExtra(IntentExtraTagMenu.ORDER_DETAIL_FROM_SUBMIT_ORDER.name());
			
			if (!TextUtils.isEmpty(orderIdString)) {
				DebugLog.i(TAG, "This is a old order , need request order detail ! ");
				
				requestOrderDetailByOrderID(orderIdString);
				
			} else if (orderDetailNetRespondBean != null) {
				DebugLog.i(TAG, "This is a new order , does not request order detail ");
				
				isNewOrder = true;
				// 设置订单ID
				orderIdString = orderDetailNetRespondBean.getOrderId();
				initUIByOrderDetailNetRespondBean(orderDetailNetRespondBean);
			} else {
				DebugLog.e(TAG, "Important data is lost ! ");
				break;
			}
			return true;
		} while (false);
		
		return false;
	}
	
	private void initOrderDetailMainBodyUIAndActivityTitleView(OrderDetailNetRespondBean orderDetailNetRespondBean) {
		if (orderDetailNetRespondBean == null) {
			return;
		}
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setTitleByString("订单 : " + orderIdString);
		
		// 当前订单状态对应的文字说明
		final TextView orderStateTextView = (TextView) findViewById(R.id.order_state_text_TextView);
		orderStateTextView.setText(orderDetailNetRespondBean.getStatusContent());
		// 房间缩略图
		final ImageView roomPhotoImageView = (ImageView) findViewById(R.id.room_photo_ImageView);
		SimpleImageLoader.loadImageFromLocalCacheAndNetworkDownload(roomPhotoImageView, orderDetailNetRespondBean.getImage(), null);
		// 房间标题
		final TextView roomTitleTextTextView = (TextView) findViewById(R.id.room_title_text_TextView);
		roomTitleTextTextView.setText(orderDetailNetRespondBean.getTitle());
		// 房间地址
		final TextView roomAddressTextTextView = (TextView) findViewById(R.id.room_address_text_TextView);
		roomAddressTextTextView.setText(orderDetailNetRespondBean.getAddress());
		
		// 订单编号
		final TextView orderNumberTextTextView = (TextView) findViewById(R.id.order_number_text_TextView);
		orderNumberTextTextView.setText(orderDetailNetRespondBean.getOrderId());
		// 入住时间
		final TextView checkinDateTextTextView = (TextView) findViewById(R.id.checkin_date_text_TextView);
		checkinDateTextTextView.setText(ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(orderDetailNetRespondBean.getChenckInDate()));
		// 退房时间
		final TextView checkoutDateTextTextView = (TextView) findViewById(R.id.checkout_date_text_TextView);
		checkoutDateTextTextView.setText(ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(orderDetailNetRespondBean.getChenckOutDate()));
		// 入住人数
		final TextView occupancyCountTextTextView = (TextView) findViewById(R.id.occupancy_count_text_TextView);
		occupancyCountTextTextView.setText(orderDetailNetRespondBean.getGuestNum() + "人");
		
		// 预付定金
		final TextView downPaymentTextTextView = (TextView) findViewById(R.id.down_payment_text_TextView);
		downPaymentTextTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(orderDetailNetRespondBean.getPricePerNight()));
		// 线下支付
		final TextView linePaymentTextTextView = (TextView) findViewById(R.id.line_payment_text_TextView);
		linePaymentTextTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(orderDetailNetRespondBean.getLinePay()));
		// 订单总额
		final TextView orderTotalPriceTextTextView = (TextView) findViewById(R.id.order_total_price_text_TextView);
		orderTotalPriceTextTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(orderDetailNetRespondBean.getSubPrice()));
	}
	
	private void loadWaitConfirmUILayout(boolean isNewOrder, OrderDetailNetRespondBean orderDetailNetRespondBean) {
		setContentView(R.layout.user_order_of_to_be_confirmed_activity);
		
		initOrderDetailMainBodyUIAndActivityTitleView(orderDetailNetRespondBean);
		
		final TextView titleTextView = (TextView) findViewById(R.id.title_for_wait_confirm_text_TextView);
		titleTextView.setTextColor(getResources().getColor(R.color.TextHighlighted));
		
		final Button button = (Button) findViewById(R.id.function_Button);
		String buttonText = "";
		if (isNewOrder) {
			buttonText = "管理订单";
		} else {
			buttonText = "取消订单";
		}
		button.setText(buttonText);
		button.setOnClickListener(onClickListenerForWaitConfirm);
		
		// 跳转 "订单详情" 的点击layout
		final View orderDetailView = findViewById(R.id.room_detail_layout);
		orderDetailView.setOnClickListener(onClickListenerForWaitConfirm);
	}
	
	private void loadWaitPayUILayout(boolean isNewOrder, OrderDetailNetRespondBean orderDetailNetRespondBean) {
		setContentView(R.layout.user_order_of_to_be_paid_activity);
		
		initOrderDetailMainBodyUIAndActivityTitleView(orderDetailNetRespondBean);
		
		final TextView titleTextView = (TextView) findViewById(R.id.title_for_wait_pay_text_TextView);
		titleTextView.setTextColor(getResources().getColor(R.color.TextHighlighted));
		
		// "去支付" 按钮
		final Button toPayButton = (Button) findViewById(R.id.to_pay_Button);
		toPayButton.setOnClickListener(onClickListenerForWaitPay);
		
		// "功能按钮"
		final Button functionButton = (Button) findViewById(R.id.function_Button);
		String buttonText = "";
		if (isNewOrder) {
			buttonText = "管理订单";
		} else {
			buttonText = "取消订单";
		}
		functionButton.setText(buttonText);
		functionButton.setOnClickListener(onClickListenerForWaitPay);
		
		// "订单详情"
		final View orderDetailView = findViewById(R.id.room_detail_layout);
		orderDetailView.setOnClickListener(onClickListenerForWaitPay);
	}
	
	private void loadWaitLiveUILayout(OrderDetailNetRespondBean orderDetailNetRespondBean) {
		setContentView(R.layout.user_order_of_to_be_live_activity);
		
		initOrderDetailMainBodyUIAndActivityTitleView(orderDetailNetRespondBean);
		
		final TextView titleTextView = (TextView) findViewById(R.id.title_for_wait_live_text_TextView);
		titleTextView.setTextColor(getResources().getColor(R.color.TextHighlighted));
		
		// "功能按钮"
		final Button functionButton = (Button) findViewById(R.id.function_Button);
		functionButton.setOnClickListener(onClickListenerForWaitLive);
		
		// "订单详情"
		final View orderDetailView = findViewById(R.id.room_detail_layout);
		orderDetailView.setOnClickListener(onClickListenerForWaitLive);
	}
	
	private void loadWaitCommentUILayout(OrderDetailNetRespondBean orderDetailNetRespondBean) {
		setContentView(R.layout.user_order_of_to_be_review_activity);
		
		initOrderDetailMainBodyUIAndActivityTitleView(orderDetailNetRespondBean);
		
		final TextView titleTextView = (TextView) findViewById(R.id.title_for_wait_review_text_TextView);
		titleTextView.setTextColor(getResources().getColor(R.color.TextHighlighted));
		
		// "去评价按钮"
		final Button toReviewButton = (Button) findViewById(R.id.to_review_Button);
		toReviewButton.setOnClickListener(onClickListenerForWaitComment);
		
		// "订单详情"
		final View orderDetailView = findViewById(R.id.room_detail_layout);
		orderDetailView.setOnClickListener(onClickListenerForWaitComment);
	}
	
	private void loadHasEndedUILayout(OrderDetailNetRespondBean orderDetailNetRespondBean) {
		setContentView(R.layout.user_order_of_ended_activity);
		
		initOrderDetailMainBodyUIAndActivityTitleView(orderDetailNetRespondBean);
		
		// "订单详情"
		final View orderDetailView = findViewById(R.id.room_detail_layout);
		orderDetailView.setOnClickListener(onClickListenerForHasEnded);
	}
	
	private void gotoWriteReviewActivity() {
		final Intent intent = new Intent(UserOrderDetailActivity.this, WriteReviewActivity.class);
		intent.putExtra(WriteReviewActivity.IntentExtraTagEnum.ORDER_ID.name(), orderIdString);
		startActivityForResult(intent, IntentRequestCodeEnum.TO_WRITE_REVIEW_ACTIVITY.ordinal());
	}
	
	private void gotoUserOrderMainActivity() {
		if (isNewOrder) {
			final Intent intent = new Intent(UserOrderDetailActivity.this, UserOrderMainActivity.class);
			intent.putExtra(UserOrderMainActivity.IntentExtraTagMenu.ORDER_STATE.name(), GlobalConstant.OrderStateEnum.WAIT_CONFIRM);
			startActivity(intent);
			finish();
		} else {
			setResult(RESULT_OK);
			finish();
		}
	}
	
	private void gotoRoomDetailActivity() {
		do {
			if (orderDetailNetRespondBean == null) {
				break;
			}
			
			String roomNumberString = orderDetailNetRespondBean.getNumber();
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			
			final Intent intent = new Intent(UserOrderDetailActivity.this, RoomDetailOfBasicInformationActivity.class);
			intent.putExtra(RoomDetailOfBasicInformationActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberString);
			startActivity(intent);
		} while (false);
	}
	
	/**
	 * "待确认" 状态下的控件监听者
	 */
	private View.OnClickListener onClickListenerForWaitConfirm = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "跳转订单详情" 按钮
				case R.id.room_detail_layout: {
					gotoRoomDetailActivity();
				}
				break;
				
				// 业务说明 : 如果是新订单, 此按钮是 "管理订单" 功能, 如果不是新订单, 此按钮是 "取消订单" 功能.
				case R.id.function_Button: {
					if (isNewOrder) {
						gotoUserOrderMainActivity();
					} else {
						// 不允许用户, 重复取消一个订单
						if (netRequestIndexForOrderCancel != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
							SimpleProgressDialog.show(UserOrderDetailActivity.this, progressDialogOnCancelListener);
						} else {
							requestOrderCancelByOrderID(orderIdString);
						}
					}
				}
				break;
				default:
				break;
			}
		}
	};
	
	/**
	 * "待支付" 状态下的控件监听者
	 */
	private View.OnClickListener onClickListenerForWaitPay = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "去付款" 按钮
				case R.id.to_pay_Button: {
					
				}
				break;
				
				// "跳转订单详情" 按钮
				case R.id.room_detail_layout: {
					gotoRoomDetailActivity();
				}
				break;
				
				// 业务说明 : 如果是新订单, 此按钮是 "管理订单" 功能, 如果不是新订单, 此按钮是 "取消订单" 功能.
				case R.id.function_Button: {
					if (isNewOrder) {
						gotoUserOrderMainActivity();
					} else {
						requestOrderCancelByOrderID(orderIdString);
					}
				}
				break;
				default:
				break;
			}
		}
	};
	
	/**
	 * "待入住" 状态下的控件监听者
	 */
	private View.OnClickListener onClickListenerForWaitLive = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "跳转订单详情" 按钮
				case R.id.room_detail_layout: {
					gotoRoomDetailActivity();
				}
				break;
				
				// "取消订单"
				case R.id.function_Button: {
					requestOrderCancelByOrderID(orderIdString);
				}
				break;
				default:
				break;
			}
		}
	};
	
	/**
	 * "待评价" 状态下的控件监听者
	 */
	private View.OnClickListener onClickListenerForWaitComment = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "去评论" 按钮
				case R.id.to_review_Button: {
					gotoWriteReviewActivity();
				}
				break;
				
				// "跳转订单详情" 按钮
				case R.id.room_detail_layout: {
					gotoRoomDetailActivity();
				}
				break;
				default:
				break;
			}
		}
	};
	
	/**
	 * "已完成" 状态下的控件监听者
	 */
	private View.OnClickListener onClickListenerForHasEnded = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "跳转订单详情" 按钮
				case R.id.room_detail_layout: {
					gotoRoomDetailActivity();
				}
				break;
				default:
				break;
			}
		}
	};
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				gotoUserOrderMainActivity();
			}
		}
	};
	
	private void requestOrderDetailByOrderID(String orderId) {
		if (TextUtils.isEmpty(orderId)) {
			return;
		}
		
		final OrderDetailNetRequestBean orderDetailNetRequestBean = new OrderDetailNetRequestBean(orderId);
		netRequestIndexForOrderDetail = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, orderDetailNetRequestBean, NetRequestTagEnum.ORDER_DETAIL, domainNetRespondCallback);
		if (netRequestIndexForOrderDetail != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void requestOrderCancelByOrderID(String orderId) {
		if (TextUtils.isEmpty(orderId)) {
			return;
		}
		
		final OrderCancelNetRequestBean orderCancelNetRequestBean = new OrderCancelNetRequestBean(orderId);
		netRequestIndexForOrderCancel = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, orderCancelNetRequestBean, NetRequestTagEnum.ORDER_CANCEL, domainNetRespondCallback);
		if (netRequestIndexForOrderCancel != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void initUIByOrderDetailNetRespondBean(OrderDetailNetRespondBean orderDetailNetRespondBean) {
		if (orderDetailNetRespondBean == null) {
			return;
		}
		
		final int conversionStateInt = orderDetailNetRespondBean.getConversionState();
		if (GlobalConstant.OrderStateEnum.WAIT_CONFIRM.getValue() == conversionStateInt) {
			loadWaitConfirmUILayout(isNewOrder, orderDetailNetRespondBean);
		} else if (GlobalConstant.OrderStateEnum.WAIT_PAY.getValue() == conversionStateInt) {
			loadWaitPayUILayout(isNewOrder, orderDetailNetRespondBean);
		} else if (GlobalConstant.OrderStateEnum.WAIT_LIVE.getValue() == conversionStateInt) {
			loadWaitLiveUILayout(orderDetailNetRespondBean);
		} else if (GlobalConstant.OrderStateEnum.WAIT_COMMENT.getValue() == conversionStateInt) {
			loadWaitCommentUILayout(orderDetailNetRespondBean);
		} else if (GlobalConstant.OrderStateEnum.HAS_ENDED.getValue() == conversionStateInt) {
			loadHasEndedUILayout(orderDetailNetRespondBean);
		} else {
			DebugLog.e(TAG, "Invalid conversionState");
			
			// TODO :临时处理方式
			TextView errorMessagePromptsTextView = (TextView) findViewById(R.id.error_message_prompts_TextView);
			if (errorMessagePromptsTextView != null) {
				errorMessagePromptsTextView.setText("访问出错, 请返回上一层界面.");
			}
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		GET_ORDER_DETAIL_SUCCESS,
		//
		CANCEL_ORDER_SUCCESS
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			SimpleProgressDialog.dismiss(UserOrderDetailActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(UserOrderDetailActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.GET_ORDER_DETAIL_SUCCESS.ordinal()) {
				initUIByOrderDetailNetRespondBean(orderDetailNetRespondBean);
			} else if (msg.what == HandlerMsgTypeEnum.CANCEL_ORDER_SUCCESS.ordinal()) {
				gotoUserOrderMainActivity();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (requestEvent == NetRequestTagEnum.ORDER_DETAIL) {
			netRequestIndexForOrderDetail = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (requestEvent == NetRequestTagEnum.ORDER_CANCEL) {
			netRequestIndexForOrderCancel = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.ORDER_DETAIL) {// 2.23 查看订单详情
				orderDetailNetRespondBean = (OrderDetailNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, orderDetailNetRespondBean.toString());
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.GET_ORDER_DETAIL_SUCCESS.ordinal();
				handler.sendMessage(msg);
			} else if (requestEvent == NetRequestTagEnum.ORDER_CANCEL) {// 2.24 取消订单
				Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.CANCEL_ORDER_SUCCESS.ordinal();
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
