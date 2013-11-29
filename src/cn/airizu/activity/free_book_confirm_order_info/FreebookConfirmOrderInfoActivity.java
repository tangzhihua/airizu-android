package cn.airizu.activity.free_book_confirm_order_info;

import cn.airizu.activity.R;
import cn.airizu.activity.use_promotion_activity.UsePromotionActivity;
import cn.airizu.activity.user_order_detail.UserOrderDetailActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.order_detail.OrderDetailNetRespondBean;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRequestBean;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRespondBean;
import cn.airizu.domain.protocol.order_submit.OrderSubmitNetRequestBean;
import cn.airizu.domain.protocol.order_submit.OrderSubmitNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FreebookConfirmOrderInfoActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	private final int INTENT_REQUEST_CODE_FOR_USE_PROMOTIONS = 0;
	
	// 订单总额
	private TextView orderTotalPriceContentTextView;
	// 预付定金
	private TextView downPaymentContentTextView;
	// 线下支付
	private TextView linePaymentContentTextView;
	
	// 跳转到 优惠 界面的 click layout
	private View usePromotionLayout;
	
	// 租客姓名
	private EditText tenantNameEditText;
	// 租客手机号码
	private EditText phoneNumberEditText;
	
	// 是否同意订单信息和爱日租服务条款
	private CheckBox confirmOrderInfoCheckBox;
	
	public static enum IntentExtraTagMenu {
		//
		FREEBOOK_NET_REQUEST_BEAN,
		//
		FREEBOOK_NET_RESPOND_BEAN
	};
	
	private FreeBookNetRequestBean freeBookNetRequestBean;
	private FreeBookNetRespondBean freeBookNetRespondBeanForLatest;
	// 未使用优惠时的预订结果
	private FreeBookNetRespondBean freeBookNetRespondBeanForUnusedPromotions;
	
	private static enum NetRequestTagEnum {
		// 2.21 提交订单
		ORDER_SUBMIT,
		// 2.20 订单预订
		ORDER_FREEBOOK
	};
	
	private int netRequestIndexForOrderSubmit = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.free_book_confirm_order_info_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("免费预订");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			return;
		}
		
		// 入住时间
		final TextView checkinDateContentTextView = (TextView) findViewById(R.id.checkin_date_content_TextView);
		checkinDateContentTextView.setText(freeBookNetRequestBean.getCheckInDate());
		// 退房时间
		final TextView checkoutDateContentTextView = (TextView) findViewById(R.id.checkout_date_content_TextView);
		checkoutDateContentTextView.setText(freeBookNetRequestBean.getCheckOutDate());
		// 入住人数
		final TextView occupancyCountContentTextView = (TextView) findViewById(R.id.occupancy_count_content_TextView);
		occupancyCountContentTextView.setText(freeBookNetRequestBean.getGuestNum() + "人");
		
		// 订单总额
		orderTotalPriceContentTextView = (TextView) findViewById(R.id.order_total_price_content_TextView);
		// 预付定金
		downPaymentContentTextView = (TextView) findViewById(R.id.down_payment_content_TextView);
		// 线下支付
		linePaymentContentTextView = (TextView) findViewById(R.id.line_payment_content_TextView);
		
		usePromotionLayout = findViewById(R.id.already_use_the_promotions_body_layout);
		
		// 跳转到 优惠 界面的按钮(不具备点击监听)
		final Button usePromotionButton = (Button) findViewById(R.id.promotion_Button);
		usePromotionButton.setOnClickListener(buttonClickListener);
		
		tenantNameEditText = (EditText) findViewById(R.id.tenant_name_EditText);
		phoneNumberEditText = (EditText) findViewById(R.id.phone_number_EditText);
		
		confirmOrderInfoCheckBox = (CheckBox) findViewById(R.id.confirm_order_info_CheckBox);
		
		// "提交我的订单" 按钮
		final Button submitButton = (Button) findViewById(R.id.submit_Button);
		submitButton.setOnClickListener(buttonClickListener);
		
		if (freeBookNetRespondBeanForLatest == null) {
			requestFreebookOrderInfoByFreeBookNetRequestBean(freeBookNetRequestBean);
		} else {
			initOrderPriceInfoFromFreeBookNetRespondBean(freeBookNetRespondBeanForLatest);
		}
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
		netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		netRequestIndexForOrderSubmit = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult");
		
		if (resultCode != RESULT_OK) {
			return;
		}
		
		if (requestCode == INTENT_REQUEST_CODE_FOR_USE_PROMOTIONS) {
			do {
				if (data == null) {
					break;
				}
				
				final String pointNumberString = data.getStringExtra(UsePromotionActivity.IntentResultExtraTagEnum.POINT_NUMBER.name());
				final int voucherMethod = data.getIntExtra(UsePromotionActivity.IntentResultExtraTagEnum.VOUCHER_METHOD.name(), 0);
				final String voucherCodeString = data.getStringExtra(UsePromotionActivity.IntentResultExtraTagEnum.VOUCHER_CODE.name());
				boolean isHavePreferential = false;
				if (!TextUtils.isEmpty(pointNumberString)) {
					isHavePreferential = true;
				} else if (!TextUtils.isEmpty(voucherCodeString)) {
					isHavePreferential = true;
				}
				
				if (isHavePreferential) {// 用户使用了优惠
					usePromotionLayout.setVisibility(View.VISIBLE);
					final FreeBookNetRequestBean.Builder freeBookNetRequestBeanBuilder = new FreeBookNetRequestBean.Builder(freeBookNetRequestBean);
					if (!TextUtils.isEmpty(pointNumberString)) {
						freeBookNetRequestBeanBuilder.pointNum(pointNumberString);
					}
					if (!TextUtils.isEmpty(voucherCodeString)) {
						freeBookNetRequestBeanBuilder.iVoucherCode(voucherCodeString);
					}
					freeBookNetRequestBeanBuilder.voucherMethod(voucherMethod);
					freeBookNetRequestBean = freeBookNetRequestBeanBuilder.builder();
					requestFreebookOrderInfoByFreeBookNetRequestBean(freeBookNetRequestBean);
				} else {
					usePromotionLayout.setVisibility(View.GONE);
					initOrderPriceInfoFromFreeBookNetRespondBean(freeBookNetRespondBeanForUnusedPromotions);
				}
			} while (false);
		}
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				DebugLog.e(TAG, "intent is null ! ");
				break;
			}
			
			freeBookNetRequestBean = (FreeBookNetRequestBean) intent.getSerializableExtra(IntentExtraTagMenu.FREEBOOK_NET_REQUEST_BEAN.name());
			if (freeBookNetRequestBean == null) {
				DebugLog.e(TAG, "freeBookNetRequestBean is null ! ");
				break;
			}
			
			freeBookNetRespondBeanForLatest = (FreeBookNetRespondBean) intent.getSerializableExtra(IntentExtraTagMenu.FREEBOOK_NET_RESPOND_BEAN.name());
			if (freeBookNetRespondBeanForLatest == null) {
				DebugLog.e(TAG, "freeBookNetRespondBeanForLatest is null ! ");
				break;
			}
			
			freeBookNetRespondBeanForUnusedPromotions = freeBookNetRespondBeanForLatest;
			return true;
		} while (false);
		
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
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "使用优惠" 按钮
				case R.id.promotion_Button: {
					if (freeBookNetRespondBeanForUnusedPromotions != null) {
						final Intent intent = new Intent(FreebookConfirmOrderInfoActivity.this, UsePromotionActivity.class);
						intent.putExtra(UsePromotionActivity.IntentExtraTagMenu.FREEBOOK_NET_REQUEST_BEAN.name(), freeBookNetRequestBean);
						intent.putExtra(UsePromotionActivity.IntentExtraTagMenu.FREEBOOK_NET_RESPOND_BEAN.name(), freeBookNetRespondBeanForUnusedPromotions);
						startActivityForResult(intent, INTENT_REQUEST_CODE_FOR_USE_PROMOTIONS);
					}
				}
				break;
				
				// "提交我的订单" 按钮
				case R.id.submit_Button: {
					requestSubmitOrder();
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	private void requestSubmitOrder() {
		String errorMessageString = "";
		do {
			if (!confirmOrderInfoCheckBox.isChecked()) {
				errorMessageString = "请您确认订单信息, 并同意爱日租服务条款.";
				break;
			}
			final String checkinName = tenantNameEditText.getText().toString();
			if (TextUtils.isEmpty(checkinName)) {
				errorMessageString = "请输入租客姓名";
				break;
			}
			final String checkinPhone = phoneNumberEditText.getText().toString();
			if (TextUtils.isEmpty(checkinPhone)) {
				errorMessageString = "请输入租客手机号码";
				break;
			}
			
			final OrderSubmitNetRequestBean.Builder orderSubmitNetRequestBeanBuilder = new OrderSubmitNetRequestBean.Builder(freeBookNetRequestBean, checkinName, checkinPhone);
			orderSubmitNetRequestBeanBuilder.voucherCode(freeBookNetRequestBean.getVoucherCode());
			orderSubmitNetRequestBeanBuilder.pointNum(freeBookNetRequestBean.getPointNum());
			final OrderSubmitNetRequestBean orderSubmitNetRequestBean = orderSubmitNetRequestBeanBuilder.builder();
			
			netRequestIndexForOrderSubmit = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, orderSubmitNetRequestBean, NetRequestTagEnum.ORDER_SUBMIT, domainNetRespondCallback);
			if (netRequestIndexForOrderSubmit != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(this, progressDialogOnCancelListener);
			}
			return;
		} while (false);
		
		Toast.makeText(this, errorMessageString, 0).show();
	}
	
	private void initOrderPriceInfoFromFreeBookNetRespondBean(FreeBookNetRespondBean freeBookNetRespondBean) {
		if (freeBookNetRespondBean == null) {
			return;
		}
		
		// 订单总额
		final String orderTotalPriceString = GlobalConstant.MONEY_MARK_STRING + Double.valueOf(freeBookNetRespondBean.getTotalPrice()).intValue();
		orderTotalPriceContentTextView.setText(orderTotalPriceString);
		// 预付定金
		final String advancedDepositString = GlobalConstant.MONEY_MARK_STRING + Double.valueOf(freeBookNetRespondBean.getAdvancedDeposit()).intValue();
		downPaymentContentTextView.setText(advancedDepositString);
		// 线下支付
		final String underLinePaidString = GlobalConstant.MONEY_MARK_STRING + Double.valueOf(freeBookNetRespondBean.getUnderLinePaid()).intValue();
		linePaymentContentTextView.setText(underLinePaidString);
	}
	
	private void requestFreebookOrderInfoByFreeBookNetRequestBean(FreeBookNetRequestBean freeBookNetRequestBean) {
		if (freeBookNetRequestBean == null) {
			return;
		}
		
		netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, freeBookNetRequestBean, NetRequestTagEnum.ORDER_FREEBOOK, domainNetRespondCallback);
		if (netRequestIndexForFreebook != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI,
		//
		TO_ORDER_DETAIL_ACTIVIY
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE,
		//
		ORDER_DETAIL_RESPOND_BAEN
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			//
			SimpleProgressDialog.dismiss(FreebookConfirmOrderInfoActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(FreebookConfirmOrderInfoActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				initOrderPriceInfoFromFreeBookNetRespondBean(freeBookNetRespondBeanForLatest);
			} else if (msg.what == HandlerMsgTypeEnum.TO_ORDER_DETAIL_ACTIVIY.ordinal()) {
				final OrderDetailNetRespondBean orderDetailNetRespondBean = (OrderDetailNetRespondBean) msg.getData().getSerializable(HandlerExtraDataTypeEnum.ORDER_DETAIL_RESPOND_BAEN.name());
				if (orderDetailNetRespondBean != null) {
					final Intent intent = new Intent(FreebookConfirmOrderInfoActivity.this, UserOrderDetailActivity.class);
					intent.putExtra(UserOrderDetailActivity.IntentExtraTagMenu.ORDER_DETAIL_FROM_SUBMIT_ORDER.name(), orderDetailNetRespondBean);
					startActivity(intent);
				} else {
					// 异常
					DebugLog.e(TAG, "Important data for handler msg data is lost");
				}
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ORDER_SUBMIT == requestEvent) {
			netRequestIndexForOrderSubmit = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {
			netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (NetRequestTagEnum.ORDER_SUBMIT == requestEvent) {// 2.21 提交订单
				final OrderSubmitNetRespondBean orderSubmitNetRespondBean = (OrderSubmitNetRespondBean) respondDomainBean;
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.TO_ORDER_DETAIL_ACTIVIY.ordinal();
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.ORDER_DETAIL_RESPOND_BAEN.name(), orderSubmitNetRespondBean.getOrderDetailNetRespondBean());
				handler.sendMessage(msg);
				
			} else if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {// 2.20 订单预订
			
				freeBookNetRespondBeanForLatest = (FreeBookNetRespondBean) respondDomainBean;
				if (freeBookNetRespondBeanForUnusedPromotions == null) {
					freeBookNetRespondBeanForUnusedPromotions = freeBookNetRespondBeanForLatest;
				}
				DebugLog.i(TAG, freeBookNetRespondBeanForLatest.toString());
				
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
