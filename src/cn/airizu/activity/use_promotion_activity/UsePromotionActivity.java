package cn.airizu.activity.use_promotion_activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRequestBean;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;

public class UsePromotionActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	// 订单总额
	private TextView orderTotalPriceContentTextView;
	// 预付定金
	private TextView downPaymentContentTextView;
	// 线下支付
	private TextView linePaymentContentTextView;
	
	// 积分冲抵模块
	private View creditsToOffsetBodyLayout;
	private SeekBar creditsSeekBar;
	private TextView seekbarMinValueTextView;
	private TextView seekbarMaxValueTextView;
	private TextView creditsToOffsetRuleContentTextView;
	
	// 折扣券输入框
	private EditText discountCouponsContentEditText;
	
	// 确定按钮
	private Button submitButton;
	
	private FreeBookNetRequestBean freeBookNetRequestBean;
	private FreeBookNetRespondBean freeBookNetRespondBean;
	
	public static enum IntentExtraTagMenu {
		//
		FREEBOOK_NET_REQUEST_BEAN,
		//
		FREEBOOK_NET_RESPOND_BEAN
	};
	
	private static enum NetRequestTagEnum {
		// 2.20 订单预订
		ORDER_FREEBOOK
	};
	
	public static enum IntentResultExtraTagEnum {
		// 优惠劵号码
		VOUCHER_CODE,
		// 优惠卷类型
		VOUCHER_METHOD,
		// 用户本次使用的积分
		POINT_NUMBER
	};
	
	// 订单总额
	private int totalPriceInt = 0;
	// 预付订金
	private int advancedDepositInt = 0;
	// 用户积分
	private int availablePointInt = 0;
	// 当前积分可以兑换的金钱额度
	private int moneyWithAvailablePointInt = 0;
	//
	private int progressForSeekbar = 0;
	
	private int netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.use_promotion_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("使用优惠");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 必须放在这里
		if (!parseIntent(getIntent())) {
			return;
		}
		
		// 订单总额
		orderTotalPriceContentTextView = (TextView) findViewById(R.id.order_total_price_content_TextView);
		// 预付定金
		downPaymentContentTextView = (TextView) findViewById(R.id.down_payment_content_TextView);
		// 线下支付
		linePaymentContentTextView = (TextView) findViewById(R.id.line_payment_content_TextView);
		
		// 积分冲抵模块
		creditsToOffsetBodyLayout = findViewById(R.id.credits_to_offset_body_layout);
		//
		creditsSeekBar = (SeekBar) findViewById(R.id.credits_SeekBar);
		creditsSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		creditsSeekBar.setOnTouchListener(seekBarTouchListener);
		final Bitmap thumBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
		creditsSeekBar.setPadding(thumBitmap.getWidth() / 2, 0, thumBitmap.getWidth() / 2, 0);
		creditsSeekBar.setThumb(new SeekbarThumb(thumBitmap));
		//
		seekbarMinValueTextView = (TextView) findViewById(R.id.seekbar_min_value_TextView);
		seekbarMaxValueTextView = (TextView) findViewById(R.id.seekbar_max_value_TextView);
		creditsToOffsetRuleContentTextView = (TextView) findViewById(R.id.credits_to_offset_rule_content_TextView);
		
		// 折扣券输入框
		discountCouponsContentEditText = (EditText) findViewById(R.id.discount_coupons_content_EditText);
		discountCouponsContentEditText.setOnEditorActionListener(editorActionListener);
		// TODO : 使用优惠券不能预订超过3天的订单, 但是这个业务是否会改变, 还是不要放到客户端判断死了, 要不将来一旦有改, 这里会出问题
		
		// 确定按钮
		submitButton = (Button) findViewById(R.id.submit_Button);
		submitButton.setOnClickListener(buttonClickListener);
		
		initViewDataUseFreeBookNetRespondBean(freeBookNetRespondBean);
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
				DebugLog.e(TAG, "intent is null ! ");
				break;
			}
			
			freeBookNetRequestBean = (FreeBookNetRequestBean) intent.getSerializableExtra(IntentExtraTagMenu.FREEBOOK_NET_REQUEST_BEAN.name());
			if (freeBookNetRequestBean == null) {
				DebugLog.e(TAG, "freeBookNetRequestBean is null ! ");
				break;
			}
			freeBookNetRespondBean = (FreeBookNetRespondBean) intent.getSerializableExtra(IntentExtraTagMenu.FREEBOOK_NET_RESPOND_BEAN.name());
			if (freeBookNetRespondBean == null) {
				DebugLog.e(TAG, "freeBookNetRespondBean is null ! ");
				break;
			}
			
			return true;
		} while (false);
		
		return false;
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		}
	};
	
	private View.OnTouchListener seekBarTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Auto-generated method stub
			return false;
		}
	};
	private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		
		// 拖动进度条后停止跟踪触摸
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// 停止跟踪触摸时候发生的动作
			if (seekBar.getProgress() < 8) {
				seekBar.setProgress(0);
			}
			
		}
		
		// 拖动进度条前开始跟踪触摸
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// 开始跟踪触摸时候发生的动作
			
		}
		
		// 拖动进度条后，进度发生改变时的回调事件
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			
			progressForSeekbar = progress;
			
			// 定金总额
			final Integer totalPriceInteger = Integer.valueOf(totalPriceInt - progress);
			orderTotalPriceContentTextView.setText(GlobalConstant.MONEY_MARK_STRING + totalPriceInteger.toString());
			// 预付定金
			final Integer advancedDepositInteger = Integer.valueOf(advancedDepositInt - progress);
			downPaymentContentTextView.setText(GlobalConstant.MONEY_MARK_STRING + advancedDepositInteger.toString());
		}
	};
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.submit_Button: {
					final Intent intent = new Intent();
					
					// 优惠劵号码
					final String couponsNumberString = discountCouponsContentEditText.getText().toString();
					if (!TextUtils.isEmpty(couponsNumberString) && freeBookNetRespondBean != null) {// freeBookNetRespondBean 不为空时, 才是有效优惠券
						intent.putExtra(IntentResultExtraTagEnum.VOUCHER_CODE.name(), couponsNumberString);
						
						// 优惠卷类型
						// 0：不使用优惠
						// 1：VIP优惠
						// 2：普通优惠卷
						// 3：现金卷
						intent.putExtra(IntentResultExtraTagEnum.VOUCHER_METHOD.name(), GlobalConstant.VoucherMethodEnum.ORDINARY_COUPONS.ordinal());
					} else {
						intent.putExtra(IntentResultExtraTagEnum.VOUCHER_METHOD.name(), GlobalConstant.VoucherMethodEnum.DO_NOT_USE_PROMOTIONS.ordinal());
					}
					
					// 用户本次使用的冲抵积分
					int pointNumInt = creditsSeekBar.getProgress();
					if (pointNumInt > 0) {
						intent.putExtra(IntentResultExtraTagEnum.POINT_NUMBER.name(), Integer.toString(pointNumInt));
					}
					
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				break;
				
				default:
				break;
			}
			
		}
	};
	
	/**
	 * 初始化View控件数据
	 * 
	 * @param freeBookNetRespondBean
	 */
	private void initViewDataUseFreeBookNetRespondBean(FreeBookNetRespondBean freeBookNetRespondBean) {
		if (freeBookNetRespondBean == null) {
			return;
		}
		
		// 订单总额
		totalPriceInt = Double.valueOf(freeBookNetRespondBean.getTotalPrice()).intValue();
		orderTotalPriceContentTextView.setText(GlobalConstant.MONEY_MARK_STRING + totalPriceInt);
		// 预付订金
		advancedDepositInt = Double.valueOf(freeBookNetRespondBean.getAdvancedDeposit()).intValue();
		downPaymentContentTextView.setText(GlobalConstant.MONEY_MARK_STRING + advancedDepositInt);
		// 线下支付
		linePaymentContentTextView.setText(GlobalConstant.MONEY_MARK_STRING + Double.valueOf(freeBookNetRespondBean.getUnderLinePaid()).intValue());
		// 用户积分
		availablePointInt = freeBookNetRespondBean.getAvailablePoint();
		
		// 只有用户积分大于800时, 才显示积分冲抵模块
		if (availablePointInt >= 800) {
			creditsToOffsetBodyLayout.setVisibility(View.VISIBLE);
			
			// 将用户可用的积分折换成可用的金钱额度
			moneyWithAvailablePointInt = availablePointInt / 100;
			
			int seekBarMaxValue = 0;
			// 如果用户可冲抵的积分总额度 >= 预付定金额度, 那么用户可冲抵的真实积分额度就是 预付定金额度减一
			if (moneyWithAvailablePointInt >= advancedDepositInt) {
				seekBarMaxValue = advancedDepositInt - 1;
				
				// 否则, 就是用户可冲抵的积分总额度 < 预付定金额度, 那么用户可冲抵的真实额度就等于用户积分总额度
			} else {
				seekBarMaxValue = moneyWithAvailablePointInt;
			}
			progressForSeekbar = seekBarMaxValue;
			creditsSeekBar.setMax(seekBarMaxValue);
			creditsSeekBar.setProgress(creditsSeekBar.getMax());
			
			//
			creditsToOffsetRuleContentTextView.setText("你的账户目前有" + availablePointInt + "积分, 本次预订最多可以冲抵" + moneyWithAvailablePointInt + "元房费.");
			
			seekbarMinValueTextView.setText(GlobalConstant.MONEY_MARK_STRING + "0");
			seekbarMaxValueTextView.setText(GlobalConstant.MONEY_MARK_STRING + creditsSeekBar.getMax());
		}
	}
	
	private final TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// "完成" / "回车"
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION) {
				final String discountCoupons = discountCouponsContentEditText.getText().toString();
				if (!TextUtils.isEmpty(discountCoupons)) {
					requestCheckDiscountCoupons(discountCoupons);
				}
			}
			return false;
		}
	};
	
	/**
	 * 联网检查 "折扣券", 如果传入空字符串, 就等于在不使用折扣券的情况下进行 "免费预订"
	 * 
	 * @param discountCoupons
	 */
	private void requestCheckDiscountCoupons(String discountCoupons) {
		do {
			final FreeBookNetRequestBean.Builder freeBookNetRequestBeanBuilder = new FreeBookNetRequestBean.Builder(freeBookNetRequestBean);
			if (!TextUtils.isEmpty(discountCoupons)) {
				freeBookNetRequestBeanBuilder.iVoucherCode(discountCoupons).voucherMethod(GlobalConstant.VoucherMethodEnum.ORDINARY_COUPONS.ordinal());
			} else {
				freeBookNetRequestBeanBuilder.voucherMethod(GlobalConstant.VoucherMethodEnum.DO_NOT_USE_PROMOTIONS.ordinal());
			}
			final FreeBookNetRequestBean freeBookNetRequestBean = freeBookNetRequestBeanBuilder.builder();
			netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, freeBookNetRequestBean, NetRequestTagEnum.ORDER_FREEBOOK, domainNetRespondCallback);
			if (netRequestIndexForFreebook != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(this, progressDialogOnCancelListener);
			}
		} while (false);
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		ORDER_FREEBOOK_SUCCESS
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(UsePromotionActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(UsePromotionActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.ORDER_FREEBOOK_SUCCESS.ordinal()) {
				initViewDataUseFreeBookNetRespondBean(freeBookNetRespondBean);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {
			netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
			
			// 如果优惠券无效, 就要清空 "免费预订网络响应业务Bean"
			freeBookNetRespondBean = null;
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
			
			if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {// 检测优惠券是否有效
				freeBookNetRespondBean = (FreeBookNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, freeBookNetRespondBean.toString());
				
				final Message msg = Message.obtain(handler);
				msg.what = HandlerMsgTypeEnum.ORDER_FREEBOOK_SUCCESS.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// Auto-generated method stub
			
		}
	};
	
	private class SeekbarThumb extends BitmapDrawable {
		private Paint paint = new Paint();
		
		public SeekbarThumb(Bitmap thumBitmap) {
			super(thumBitmap);
			// 字默认是左对齐，且基线在Y轴, 这样很好呀，不用去计算字体高度，不用担心不同语种字体上下偏移的问题。
			paint.setColor(Color.BLUE);
			paint.setTextSize(14.0f);
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);// X就是字符串的中间轴, Y是字符串的基线
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		}
		
		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			canvas.drawText(GlobalConstant.MONEY_MARK_STRING + progressForSeekbar, getBounds().centerX(), getBounds().centerY() - paint.getTextSize() / 2, paint);
		}
	}
}
