package cn.airizu.activity.register;

import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_login.LogonNetRequestBean;
import cn.airizu.domain.protocol.account_login.LogonNetRespondBean;
import cn.airizu.domain.protocol.account_register.RegisterNetRespondBean;
import cn.airizu.domain.protocol.account_register.RegisterNetResquestBean;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 注册界面
 * 
 * @author zhihua.tang
 */
public class RegisterActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	private int netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForRegister = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private static enum NetRequestTagEnum {
		// 2.1 用户注册
		USER_REGISTER,
		// 2.2 用户登录
		USER_LOGON
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("注册");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		final Button registerButton = (Button) findViewById(R.id.register_Button);
		registerButton.setOnClickListener(registerButtonClickListener);
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
	
	// 用户手机号码作为登录名
	private String phoneNumberString = "";
	private String passwordString = "";
	
	private View.OnClickListener registerButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String errorMessageString = "";
			do {
				// "用户名"
				final EditText userNameEditText = (EditText) findViewById(R.id.user_name_EditText);
				String userNameString = userNameEditText.getText().toString();
				if (TextUtils.isEmpty(userNameString)) {
					errorMessageString = "用户名不能为空";
					break;
				}
				
				// "手机号"
				final EditText phoneNumberEditText = (EditText) findViewById(R.id.phone_number_EditText);
				phoneNumberString = phoneNumberEditText.getText().toString();
				if (TextUtils.isEmpty(phoneNumberString)) {
					errorMessageString = "手机号码不能为空";
					break;
				}
				if (phoneNumberString.length() != 11) {
					errorMessageString = "手机号码位数不正确";
					break;
				}
				
				// "邮箱"
				final EditText emailEditText = (EditText) findViewById(R.id.email_EditText);
				String emailString = emailEditText.getText().toString();
				if (TextUtils.isEmpty(emailString)) {
					errorMessageString = "邮箱地址不能为空";
					break;
				}
				
				// "密码"
				final EditText passwordEditText = (EditText) findViewById(R.id.password_EditText);
				passwordString = passwordEditText.getText().toString();
				if (TextUtils.isEmpty(passwordString)) {
					errorMessageString = "密码不能为空";
					break;
				}
				
				requestRegister(userNameString, phoneNumberString, emailString, passwordString);
				
				// 一切OK
				return;
			} while (false);
			
			// 用户输入的信息不符合规定
			Toast.makeText(RegisterActivity.this, errorMessageString, 0).show();
		}
	};
	
	private void requestRegister(final String userName, final String phoneNumber, final String email, final String password) {
		// 发起业务接口 "2.1用户登注册" 的访问
		final RegisterNetResquestBean netRequestDomainBean = new RegisterNetResquestBean(userName, phoneNumber, email, password);
		netRequestIndexForRegister = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(RegisterActivity.this, netRequestDomainBean, NetRequestTagEnum.USER_REGISTER,
				domainNetRespondCallback);
		if (netRequestIndexForRegister != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(RegisterActivity.this, progressDialogOnCancelListener);
		}
	}
	
	private void requestUserLogonWithUsernameAndPassword(String username, String password) {
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return;
		}
		
		// 发起业务接口 "2.2用户登录" 的访问
		final LogonNetRequestBean.Builder logonNetRequestBeanBuilder = new LogonNetRequestBean.Builder(username, password);
		logonNetRequestBeanBuilder.screenSize(GlobalDataCacheForMemorySingleton.getInstance().getScreenSize());
		logonNetRequestBeanBuilder.clientAVersion(GlobalDataCacheForMemorySingleton.getInstance().getClientAVersion());
		logonNetRequestBeanBuilder.clientVersion(GlobalDataCacheForMemorySingleton.getInstance().getClientVersion());
		netRequestIndexForUserLogon = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(RegisterActivity.this, logonNetRequestBeanBuilder.builder(), NetRequestTagEnum.USER_LOGON,
				domainNetRespondCallback);
		if (netRequestIndexForUserLogon != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(RegisterActivity.this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		// 用户注册成功
		USER_REGISTER_SUCCESS,
		// 用户登录成功
		USER_LOGIN_SUCCESS
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			SimpleProgressDialog.dismiss(RegisterActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(RegisterActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.USER_REGISTER_SUCCESS.ordinal()) {
				// 用户注册成功后, 自动帮用户进行登录操作
				final String username = phoneNumberString;// 使用用户手机号作为登录名
				final String password = passwordString;
				requestUserLogonWithUsernameAndPassword(username, password);
			} else if (msg.what == HandlerMsgTypeEnum.USER_LOGIN_SUCCESS.ordinal()) {
				RegisterActivity.this.showDialog(REGISTER_SUCCESS_DIALOG_ID);
			}
		}
	};
	
	// 用户注册成功后的提示对话框ID
	private static int REGISTER_SUCCESS_DIALOG_ID = 1;
	// 用户第一次成功登录后, 服务器给用户的提示信息
	private String userFisrtLogonSuccessfulInfoFromServer = "注册成功!";
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.USER_REGISTER == requestEvent) {
			netRequestIndexForRegister = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (NetRequestTagEnum.USER_LOGON == requestEvent) {
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
			
			if (NetRequestTagEnum.USER_REGISTER == requestEvent) {// 2.1 用户注册
			
				RegisterNetRespondBean registerRespondBean = (RegisterNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, registerRespondBean.toString());
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.USER_REGISTER_SUCCESS.ordinal();
				handler.sendMessage(msg);
			} else if (NetRequestTagEnum.USER_LOGON == requestEvent) {// 2.2 用户登录
			
				LogonNetRespondBean logonNetRespondBean = (LogonNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, logonNetRespondBean.toString());
				
				final String usernameForLastSuccessfulLogon = phoneNumberString;
				String passwordForLastSuccessfulLogon = passwordString;
				ToolsFunctionForThisProgect.noteLogonSuccessfulInfo(logonNetRespondBean, usernameForLastSuccessfulLogon, passwordForLastSuccessfulLogon);
				
				//
				userFisrtLogonSuccessfulInfoFromServer = logonNetRespondBean.getMessage();
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.USER_LOGIN_SUCCESS.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		DebugLog.i(TAG, "onCreateDialog");
		
		if (id == REGISTER_SUCCESS_DIALOG_ID) {
			return buildDialog(this);
		} else {
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		DebugLog.i(TAG, "onPrepareDialog");
		
		// dialog已经创建完成, 在显示之前会调用这里
		super.onPrepareDialog(REGISTER_SUCCESS_DIALOG_ID, dialog);
	}
	
	private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DebugLog.i(TAG, "DialogInterface.OnClickListener");
			
			// 告知登录界面, 用户已经成功登录
			setResult(RESULT_OK);
			finish();
		}
	};
	
	private DialogInterface.OnCancelListener dialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			DebugLog.i(TAG, "DialogInterface.OnCancelListener");
			
			// 告知登录界面, 用户已经成功登录
			setResult(RESULT_OK);
			finish();
		}
	};
	
	private Dialog buildDialog(Context context) {
		
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("注册成功");
		alertDialogBuilder.setMessage(userFisrtLogonSuccessfulInfoFromServer);
		alertDialogBuilder.setPositiveButton("确定", dialogOnClickListener);
		alertDialogBuilder.setOnCancelListener(dialogOnCancelListener);
		
		return alertDialogBuilder.create();
	}
	
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
			// TODO Auto-generated method stub
			
		}
	};
}
