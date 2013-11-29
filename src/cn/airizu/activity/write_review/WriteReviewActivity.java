package cn.airizu.activity.write_review;

import java.util.Map;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.ratingbar.CustomRatingBar;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.review_item.ReviewItemNetRequestBean;
import cn.airizu.domain.protocol.review_item.ReviewItemNetRespondBean;
import cn.airizu.domain.protocol.review_submit.ReviewSubmitNetRequestBean;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 写评论界面
 * 
 * @author zhihua.tang
 */
public class WriteReviewActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 订单编号
		ORDER_ID
	};
	
	private String orderIdString;
	private ReviewItemNetRespondBean reviewItemNetRespondBean;
	
	private enum NetRequestTagEnum {
		// 2.26 获取评论项
		GET_REVIEW_ITEM,
		// 2.27 对房间进行评论
		SUBMIT_REVIEW
	};
	
	private int netRequestIndexForGetReviewItem = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForSubmitReview = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private EditText reviewEditText;
	private CustomRatingBar scoreCustomRatingBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			return;
		}
		
		reviewItemNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getReviewItemNetRespondBean();
		if (reviewItemNetRespondBean == null) {
			requestReviewItems();
		} else {
			loadRealUIAndUseReviewItemNetRespondBeanInitialize(reviewItemNetRespondBean);
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
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			orderIdString = intent.getStringExtra(IntentExtraTagEnum.ORDER_ID.name());
			if (TextUtils.isEmpty(orderIdString)) {
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
		titleBar.setTitleByString("写评论");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void loadRealUIAndUseReviewItemNetRespondBeanInitialize(ReviewItemNetRespondBean roomDetailNetRespondBean) {
		if (roomDetailNetRespondBean == null) {
			return;
		}
		
		setContentView(R.layout.write_review_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("写评论");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		//
		scoreCustomRatingBar = (CustomRatingBar) findViewById(R.id.score_CustomRatingBar);
		scoreCustomRatingBar.setDataSourceAndInitialize(roomDetailNetRespondBean.getItemMap(), CustomRatingBar.StyleEnum.FOR_WRITE_REVIEW);
		
		//
		reviewEditText = (EditText) findViewById(R.id.review_EditText);
		
		//
		final Button submitReviewButton = (Button) findViewById(R.id.submit_review_Button);
		submitReviewButton.setOnClickListener(buttOnClickListener);
	}
	
	private View.OnClickListener buttOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String errorMessageString = "";
			
			do {
				final String reviewContent = reviewEditText.getText().toString();
				if (TextUtils.isEmpty(reviewContent)) {
					errorMessageString = "评论内容不能为空";
					break;
				}
				
				if (reviewContent.length() > 40) {
					errorMessageString = "评论内容不能超过40个字";
					break;
				}
				
				requestSubmitReview(orderIdString, reviewContent, scoreCustomRatingBar.getValue());
				return;
			} while (false);
			
			Toast.makeText(WriteReviewActivity.this, errorMessageString, 0).show();
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
	
	private void requestReviewItems() {
		final ReviewItemNetRequestBean reviewItemNetRequestBean = new ReviewItemNetRequestBean();
		netRequestIndexForGetReviewItem = DomainProtocolNetHelperSingleton.getInstance()
				.requestDomainProtocol(this, reviewItemNetRequestBean, NetRequestTagEnum.GET_REVIEW_ITEM, domainNetRespondCallback);
		if (netRequestIndexForGetReviewItem != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void requestSubmitReview(final String orderId, final String reviewContent, final Map<String, String> reviewItemScoreList) {
		if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(reviewContent) || reviewItemScoreList == null || reviewItemScoreList.size() <= 0) {
			return;
		}
		
		final ReviewSubmitNetRequestBean reviewItemNetRequestBean = new ReviewSubmitNetRequestBean(orderId, reviewContent, reviewItemScoreList);
		netRequestIndexForSubmitReview = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, reviewItemNetRequestBean, NetRequestTagEnum.SUBMIT_REVIEW, domainNetRespondCallback);
		if (netRequestIndexForSubmitReview != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void loadNetErrorProcessUIAndInitialize(String netErrorMessageString, NetRequestTagEnum netRequestTagEnum) {
		
		switch (netRequestTagEnum)
		{
			case GET_REVIEW_ITEM: {
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
							requestReviewItems();
						}
					});
				}
			}
			break;
			case SUBMIT_REVIEW: {
				Toast.makeText(WriteReviewActivity.this, netErrorMessageString, 0).show();
			}
			break;
			default:
			break;
		}
		
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		GET_REVIEW_ITEW_SUCCESS,
		//
		SUBMIT_REVIEW_SUCCESS
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(WriteReviewActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessage, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.GET_REVIEW_ITEW_SUCCESS.ordinal()) {
				
				loadRealUIAndUseReviewItemNetRespondBeanInitialize(reviewItemNetRespondBean);
			} else if (msg.what == HandlerMsgTypeEnum.SUBMIT_REVIEW_SUCCESS.ordinal()) {
				
				setResult(RESULT_OK);
				finish();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.GET_REVIEW_ITEM == requestEvent) {
			netRequestIndexForGetReviewItem = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (NetRequestTagEnum.SUBMIT_REVIEW == requestEvent) {
			netRequestIndexForSubmitReview = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.GET_REVIEW_ITEM) {// 2.25 获得房间评论
			
				reviewItemNetRespondBean = (ReviewItemNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, reviewItemNetRespondBean.toString());
				
				GlobalDataCacheForMemorySingleton.getInstance.setReviewItemNetRespondBean(reviewItemNetRespondBean);
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.GET_REVIEW_ITEW_SUCCESS.ordinal();
				handler.sendMessage(msg);
			} else if (requestEvent == NetRequestTagEnum.SUBMIT_REVIEW) {// 2.27 对房间进行评论
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SUBMIT_REVIEW_SUCCESS.ordinal();
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
