package cn.airizu.activity.logon;

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
import cn.airizu.activity.R;
import cn.airizu.activity.password_forget.PasswordForgetActivity;
import cn.airizu.activity.register.RegisterActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_login.LogonNetRequestBean;
import cn.airizu.domain.protocol.account_login.LogonNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

/**
 * "登录" 界面
 * 
 * @author zhihua.tang
 */
public class LogonActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	// 业务说明 : 可以从如下入口进入 "登录" 界面
	// 1. 如果用户设置了 "自动登录", 那么在第一次进入 "账户"界面时, 将自动跳转 "登录"界面, 并且自动登录
	// 2.
	public static enum IntentExtraTagEnum {
		// 自动登录
		AUTOMATIC_LOGIN
	};
	
	private static enum NetRequestTagEnum {
		USER_LOGON
	};
	
	private static enum IntentRequestCodeEnum {
		TO_REGISTER_ACTIVITY
	};
	
	private int netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private EditText userNameEditText;
	private EditText passwordEditText;
	private CheckBox autoLogonCheckBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.logon_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("登录");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 用户名
		userNameEditText = (EditText) findViewById(R.id.user_name_EditText);
		final String usernameForLastSuccessfulLogon = GlobalDataCacheForMemorySingleton.getInstance().getUsernameForLastSuccessfulLogon();
		if (!TextUtils.isEmpty(usernameForLastSuccessfulLogon)) {
			userNameEditText.setText(usernameForLastSuccessfulLogon);
		}
		// 密码
		passwordEditText = (EditText) findViewById(R.id.password_EditText);
		String passwordForLastSuccessfulLogon = GlobalDataCacheForMemorySingleton.getInstance().getPasswordForLastSuccessfulLogon();
		if (!TextUtils.isEmpty(passwordForLastSuccessfulLogon)) {
			passwordEditText.setText(passwordForLastSuccessfulLogon);
		}
		// "忘记密码"
		final TextView passwordForgetTextView = (TextView) findViewById(R.id.password_forget_TextView);
		passwordForgetTextView.setOnClickListener(onClickListener);
		// "登录" 按钮
		final Button logonButton = (Button) findViewById(R.id.logon_Button);
		logonButton.setOnClickListener(onClickListener);
		// "快速登录" 按钮
		final Button registerButton = (Button) findViewById(R.id.quick_registration_Button);
		registerButton.setOnClickListener(onClickListener);
		// "自动登录"
		autoLogonCheckBox = (CheckBox) findViewById(R.id.auto_logon_CheckBox);
		autoLogonCheckBox.setChecked(GlobalDataCacheForMemorySingleton.getInstance().isNeedAutologin());
		
		// 自动进行登录操作
		if (getIntent() != null) {
			if (getIntent().hasExtra(IntentExtraTagEnum.AUTOMATIC_LOGIN.name())) {
				requestUserLogonWithUsernameAndPassword(usernameForLastSuccessfulLogon, passwordForLastSuccessfulLogon);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
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
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		
		GlobalDataCacheForMemorySingleton.getInstance().setNeedAutologin(autoLogonCheckBox.isChecked());
		SimpleProgressDialog.resetByThisContext(this);
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult");
		
		do {
			if (resultCode != RESULT_OK) {
				break;
			}
			
			if (requestCode == IntentRequestCodeEnum.TO_REGISTER_ACTIVITY.ordinal()) {
				// 从 "注册界面" 返回到此的, 返回 "RESULT_OK"
				// 证明用户已经登录成功
				setResult(RESULT_OK);
				finish();
			}
			
		} while (false);
	}
	
	private String usernameTempBuf = "";
	private String passwordTempBuf = "";
	
	private void requestUserLogonWithUsernameAndPassword(String username, String password) {
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return;
		}
		// 发起业务接口 "2.2用户登录" 的访问
		final LogonNetRequestBean.Builder logonNetRequestBeanBuilder = new LogonNetRequestBean.Builder(username, password);
		logonNetRequestBeanBuilder.screenSize(GlobalDataCacheForMemorySingleton.getInstance().getScreenSize());
		logonNetRequestBeanBuilder.clientAVersion(GlobalDataCacheForMemorySingleton.getInstance().getClientAVersion());
		logonNetRequestBeanBuilder.clientVersion(GlobalDataCacheForMemorySingleton.getInstance().getClientVersion());
		netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(LogonActivity.this, logonNetRequestBeanBuilder.builder(),
				NetRequestTagEnum.USER_LOGON, domainNetRespondCallback);
		if (netRequestIndexForUserLogon != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			usernameTempBuf = username;
			passwordTempBuf = password;
			SimpleProgressDialog.show(LogonActivity.this, progressDialogOnCancelListener);
		}
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch (v.getId())
			{
				// "登录按钮"
				case R.id.logon_Button: {
					String errorMessageString = "";
					String username = "";
					String password = "";
					
					do {
						username = userNameEditText.getText().toString();
						if (TextUtils.isEmpty(username)) {
							errorMessageString = "用户名不能为空";
							break;
						}
						
						password = passwordEditText.getText().toString();
						if (TextUtils.isEmpty(password)) {
							errorMessageString = "密码不能为空";
							break;
						}
						
						requestUserLogonWithUsernameAndPassword(username, password);
						
						// 一切OK
						return;
					} while (false);
					
					// 用户输入的信息错误
					Toast.makeText(LogonActivity.this, errorMessageString, 0).show();
				}
				break;
				
				// "快速注册按钮"
				case R.id.quick_registration_Button: {
					final Intent intent = new Intent(LogonActivity.this, RegisterActivity.class);
					startActivityForResult(intent, IntentRequestCodeEnum.TO_REGISTER_ACTIVITY.ordinal());
				}
				break;
				
				// "忘记密码"
				case R.id.password_forget_TextView: {
					final Intent intent = new Intent(LogonActivity.this, PasswordForgetActivity.class);
					startActivity(intent);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		USER_LOGIN_SUCCESSFULLY
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			//
			SimpleProgressDialog.dismiss(LogonActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(LogonActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.USER_LOGIN_SUCCESSFULLY.ordinal()) {
				setResult(RESULT_OK);
				finish();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.USER_LOGON == requestEvent) {
			netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.USER_LOGON) {// 2.2 用户登录
				LogonNetRespondBean logonRespond = (LogonNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, logonRespond.toString());
				
				// 保存用户成功登录后的信息
				ToolsFunctionForThisProgect.noteLogonSuccessfulInfo(logonRespond, usernameTempBuf, passwordTempBuf);
				
				// 发送用户成功登录的广播消息
				final Intent intent = new Intent();
				intent.setAction(GlobalConstant.UserActionEnum.USER_LOGON_SUCCESS.name());
				LogonActivity.this.sendBroadcast(intent);
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.USER_LOGIN_SUCCESSFULLY.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO : 这里还需要考虑怎么处理最合适
			// 用户如果取消了, ProgressDialog 的话, 就取消刚才的登录操作.
			DomainProtocolNetHelperSingleton.getInstance().cancelNetRequestByRequestIndex(netRequestIndexForUserLogon);
			netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
	};
}
