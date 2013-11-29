package cn.airizu.activity.password_forget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_forget_password.ForgetPasswordNetRequestBean;
import cn.airizu.toolutils.DebugLog;

public class PasswordForgetActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private static enum NetRequestTagEnum {
		PASSWORD_FORGET
	};
	
	int netRequestIndexForPasswordForget = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.password_forget_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("密码找回");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		final Button passwordForgetButton = (Button) findViewById(R.id.password_foret_Button);
		passwordForgetButton.setOnClickListener(buttOnClickListener);
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
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisNetRespondDelegate(netRespondCallback);
	}
	
	private View.OnClickListener buttOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String errorMessageString = "";
			do {
				final EditText phoneNumberEditText = (EditText) findViewById(R.id.phone_number_EditText);
				final String phoneNumber = phoneNumberEditText.getText().toString();
				if (TextUtils.isEmpty(phoneNumber)) {
					errorMessageString = "手机号不能为空";
					break;
				}
				
				requestForgetPassword(phoneNumber);
				
				// 一切OK
				return;
			} while (false);
			
			Toast.makeText(PasswordForgetActivity.this, errorMessageString, 0).show();
		}
	};
	
	private void requestForgetPassword(final String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return;
		}
		
		// 发起业务接口 "" 的访问
		final ForgetPasswordNetRequestBean netRequestDomainBean = new ForgetPasswordNetRequestBean(phoneNumber);
		netRequestIndexForPasswordForget = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(PasswordForgetActivity.this, netRequestDomainBean, NetRequestTagEnum.PASSWORD_FORGET,
				netRespondCallback);
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		// 找回密码成功
		FORGOT_PASSWORD_SUCCESS
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(PasswordForgetActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.FORGOT_PASSWORD_SUCCESS.ordinal()) {
				finish();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.PASSWORD_FORGET == requestEvent) {
			netRequestIndexForPasswordForget = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
	}
	
	private IDomainNetRespondCallback netRespondCallback = new IDomainNetRespondCallback() {
		
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
			
			if (requestEvent == NetRequestTagEnum.PASSWORD_FORGET) {// 2.3 忘记密码
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.FORGOT_PASSWORD_SUCCESS.ordinal();
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
	
}
