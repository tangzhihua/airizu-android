package cn.airizu.activity.main_navigation.tab_item;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.about.AboutActivity;
import cn.airizu.activity.help.HelpActivity;
import cn.airizu.activity.logon.LogonActivity;
import cn.airizu.activity.register.RegisterActivity;
import cn.airizu.activity.system_message_index.SystemMessageIndexActivity;
import cn.airizu.activity.user_information.UserInformationActivity;
import cn.airizu.activity.user_order_main.UserOrderMainActivity;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_index.AccountIndexNetRequestBean;
import cn.airizu.domain.protocol.account_index.AccountIndexNetRespondBean;
import cn.airizu.domain.protocol.account_logout.LogoutNetRequestBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleImageLoader;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import cn.airizu.toolutils.lbs.baidu.BaiduLbsSingleton;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;

import com.baidu.mapapi.MKAddrInfo;

/**
 * "账号" 界面
 * 
 * @author zhihua.tang
 */
public class D_AccountMainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private static enum IntentRequestCodeEnum {
		// 使用 startActivityForResult 启动 "登录界面"
		TO_LOGON_ACTIVITY,
		// 使用 startActivityForResult 启动 "注册界面"
		TO_REGISTER_ACTIVITY,
		// 使用 startActivityForResult 启动 "我的订单"
		TO_MY_ORDER_ACTIVITY,
		// 使用 startActivityForResult 启动 "个人信息"
		TO_USER_INFORMATION_ACTIVITY
	};
	
	private static enum NetRequestTagEnum {
		// 2.15 获取账号首页信息
		GET_ACCOUNT_INFO,
		// 2.17 登出
		USER_LOGOUT
	};
	
	private int netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	// 用户地址
	private String userAddress;
	private SimpleLocationForBaiduLBS location;
	
	//
	private AccountIndexNetRespondBean accountIndexNetRespondBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		userAddress = getResources().getString(R.string.positioning);// "定位中..."
		location = new SimpleLocationForBaiduLBS(null, addrInfoDelegate, true);
		if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
			// 如果 getLastMKAddrInfo() 方法返回空的话, 证明用户当前位置还未定位到, 那么就重新定位用户当前位置
			userAddress = SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.district;
		}
		
		// 用户未设置自动登录, 就显示 "未登录时的UI"
		loadContentViewForNotLogged();
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		DebugLog.i(TAG, "onNewIntent");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		DebugLog.i(TAG, "onStart");
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		
		SimpleProgressDialog.resetByThisContext(this);
		location.stopLocationRequest();
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
		
		if (BaiduLbsSingleton.getInstance().gpsIsEnable()) {
			if (userAddress.equals("定位中...") || userAddress.equals("GPS未打开")) {
				userAddress = "定位中...";
				location.startLocationRequest();
			}
		} else {
			userAddress = "GPS未打开";
		}
		// 设置 "当前位置"
		setUserAddress(userAddress);
		
		if (GlobalDataCacheForMemorySingleton.getInstance().getLogonNetRespondBean() != null) {
			// 如果用户已经登录了, 那么回到此界面时, 都要重新拉取 "用户账户首页信息"
			if (netRequestIndexForGetAccountInfo == DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				requestUserAccountIndexInfo(true);
			} else {
				SimpleProgressDialog.show(this, progressDialogOnCancelListener);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// 跳转到 推荐首页
		ToolsFunctionForThisProgect.showHomePageForFirstLevelActivity(this);
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult --> start ! ");
		
		do {
			if (resultCode != RESULT_OK) {
				break;
			}
			
			// if (requestCode == IntentRequestCodeEnum.TO_LOGON_ACTIVITY.ordinal()) {
			// getUserAccountIndexInfo(false);
			// } else if (requestCode == IntentRequestCodeEnum.TO_REGISTER_ACTIVITY.ordinal()) {
			// getUserAccountIndexInfo(true);
			// } else if (requestCode == IntentRequestCodeEnum.TO_MY_ORDER_ACTIVITY.ordinal()) {
			// getUserAccountIndexInfo(true);
			// } else if (requestCode == IntentRequestCodeEnum.TO_USER_INFORMATION_ACTIVITY.ordinal()) {
			// getUserAccountIndexInfo(true);
			// }
		} while (false);
	}
	
	private SimpleLocationForBaiduLBS.AddrInfoDelegate addrInfoDelegate = new SimpleLocationForBaiduLBS.AddrInfoDelegate() {
		
		@Override
		public void addrInfoCallback(MKAddrInfo addrInfo) {
			userAddress = addrInfo.addressComponents.city + addrInfo.addressComponents.district;
			
			// 设置 "当前位置"
			setUserAddress(userAddress);
		}
	};
	
	/**
	 * 用户未登录时, 加载的UI界面
	 */
	private void loadContentViewForNotLogged() {
		
		setContentView(R.layout.account_main_not_logged_activity);
		
		// "登录" 按钮
		final Button logonButton = (Button) findViewById(R.id.login_Button);
		logonButton.setOnClickListener(functionButtonsClickListener);
		
		// "注册" 按钮
		final Button registerButton = (Button) findViewById(R.id.register_Button);
		registerButton.setOnClickListener(functionButtonsClickListener);
		
		// "分享我的位置" 按钮
		final View shareUserLocation = findViewById(R.id.share_user_location_layout);
		shareUserLocation.setOnClickListener(functionButtonsClickListener);
		
		// "帮助" 按钮
		final View help = findViewById(R.id.help_layout);
		help.setOnClickListener(functionButtonsClickListener);
		
		// "版本更新" 按钮
		final View versionUpdate = findViewById(R.id.version_update_layout);
		versionUpdate.setOnClickListener(functionButtonsClickListener);
		
		// "关于" 按钮
		final View about = findViewById(R.id.about_layout);
		about.setOnClickListener(functionButtonsClickListener);
	}
	
	/**
	 * 用户已经登录时, 加载的UI界面
	 */
	private void loadContentViewForLogged(AccountIndexNetRespondBean accountIndexNetRespondBean) {
		
		if (accountIndexNetRespondBean == null) {
			assert false : "accountIndexNetRespond is null";
			return;
		}
		
		// 设置登录后的布局资源
		setContentView(R.layout.account_main_logged_activity);
		
		// 设置用户头像
		final ImageView userPhotoImageView = (ImageView) findViewById(R.id.user_photo_ImageView);
		SimpleImageLoader.loadImageFromNetworkDownload(userPhotoImageView, accountIndexNetRespondBean.getUserImageURL(), null);
		
		// 设置 "用户名"
		final TextView userNameTextView = (TextView) findViewById(R.id.user_name_TextView);
		userNameTextView.setText(accountIndexNetRespondBean.getUserName());
		userNameTextView.invalidate();
		
		// 设置 "积分"
		final TextView totalPointTextView = (TextView) findViewById(R.id.total_point_TextView);
		totalPointTextView.setText(Integer.valueOf(accountIndexNetRespondBean.getTotalPoint()).toString() + "分");
		totalPointTextView.invalidate();
		
		// 设置 "当前位置"
		setUserAddress(userAddress);
		
		// 设置 "等待房东确认" 订单的数量
		final TextView waitConfirmCountValueTextView = (TextView) findViewById(R.id.wait_confirm_count_value_TextView);
		waitConfirmCountValueTextView.setText(Integer.toString(accountIndexNetRespondBean.getWaitConfirmCount()));
		waitConfirmCountValueTextView.invalidate();
		final View waitConfirmView = findViewById(R.id.wait_confirm_count_RelativeLayout);
		waitConfirmView.setOnClickListener(userOrderClickListener);
		
		// 设置 "等待支付" 订单的数量
		final TextView waitPayCountValueTextView = (TextView) findViewById(R.id.wait_pay_count_value_TextView);
		waitPayCountValueTextView.setText(Integer.toString(accountIndexNetRespondBean.getWaitPayCount()));
		waitPayCountValueTextView.invalidate();
		final View waitPayView = findViewById(R.id.wait_pay_count_RelativeLayout);
		waitPayView.setOnClickListener(userOrderClickListener);
		
		// 设置 "等待入住" 订单的数量
		final TextView waitLiveCountValueTextView = (TextView) findViewById(R.id.wait_live_count_value_TextView);
		waitLiveCountValueTextView.setText(Integer.toString(accountIndexNetRespondBean.getWaitLiveCount()));
		waitLiveCountValueTextView.invalidate();
		final View waitLiveView = findViewById(R.id.wait_live_count_RelativeLayout);
		waitLiveView.setOnClickListener(userOrderClickListener);
		
		// 设置 "等待评价" 订单的数量
		final TextView waitReviewCountValueTextView = (TextView) findViewById(R.id.wait_review_count_value_TextView);
		waitReviewCountValueTextView.setText(Integer.toString(accountIndexNetRespondBean.getWaitReviewCount()));
		waitReviewCountValueTextView.invalidate();
		final View waitReviewView = findViewById(R.id.wait_review_count_RelativeLayout);
		waitReviewView.setOnClickListener(userOrderClickListener);
		
		// "分享我的位置" 按钮
		final View shareUserLocation = findViewById(R.id.share_user_location_layout);
		shareUserLocation.setOnClickListener(functionButtonsClickListener);
		
		// "个人信息" 按钮
		final View userInfo = findViewById(R.id.user_info_layout);
		userInfo.setOnClickListener(functionButtonsClickListener);
		
		// "系统通知" 按钮
		final View systemNotification = findViewById(R.id.system_notification_layout);
		systemNotification.setOnClickListener(functionButtonsClickListener);
		
		// "帮助" 按钮
		final View help = findViewById(R.id.help_layout);
		help.setOnClickListener(functionButtonsClickListener);
		
		// "版本更新" 按钮
		final View versionUpdate = findViewById(R.id.version_update_layout);
		versionUpdate.setOnClickListener(functionButtonsClickListener);
		
		// "关于" 按钮
		final View about = findViewById(R.id.about_layout);
		about.setOnClickListener(functionButtonsClickListener);
		
		// "退出登录" 按钮
		final Button logOutButton = (Button) findViewById(R.id.log_out_Button);
		logOutButton.setOnClickListener(functionButtonsClickListener);
	}
	
	/**
	 * 用户订单按钮群
	 */
	private View.OnClickListener userOrderClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			GlobalConstant.OrderStateEnum orderStateEnum = null;
			switch (v.getId())
			{
				// "等待房东确认"
				case R.id.wait_confirm_count_RelativeLayout: {
					orderStateEnum = GlobalConstant.OrderStateEnum.WAIT_CONFIRM;
				}
				break;
				
				// "等待支付"
				case R.id.wait_pay_count_RelativeLayout: {
					orderStateEnum = GlobalConstant.OrderStateEnum.WAIT_PAY;
				}
				break;
				
				// "等待入住"
				case R.id.wait_live_count_RelativeLayout: {
					orderStateEnum = GlobalConstant.OrderStateEnum.WAIT_LIVE;
				}
				break;
				
				// "等待评价"
				case R.id.wait_review_count_RelativeLayout: {
					orderStateEnum = GlobalConstant.OrderStateEnum.WAIT_COMMENT;
				}
				break;
			}
			
			if (orderStateEnum != null) {
				final Intent intent = new Intent(D_AccountMainActivity.this, UserOrderMainActivity.class);
				intent.putExtra(UserOrderMainActivity.IntentExtraTagMenu.ORDER_STATE.name(), orderStateEnum);
				startActivityForResult(intent, IntentRequestCodeEnum.TO_MY_ORDER_ACTIVITY.ordinal());
			}
		}
	};
	
	private View.OnClickListener functionButtonsClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// "登录按钮"
				case R.id.login_Button: {
					final Intent intent = new Intent(D_AccountMainActivity.this, LogonActivity.class);
					startActivityForResult(intent, IntentRequestCodeEnum.TO_LOGON_ACTIVITY.ordinal());
				}
				break;
				
				// "注册按钮"
				case R.id.register_Button: {
					final Intent intent = new Intent(D_AccountMainActivity.this, RegisterActivity.class);
					startActivityForResult(intent, IntentRequestCodeEnum.TO_REGISTER_ACTIVITY.ordinal());
				}
				break;
				
				// "分享我的位置"
				case R.id.share_user_location_layout: {
					Toast.makeText(D_AccountMainActivity.this, "分享我的位置", 0).show();
				}
				break;
				
				// "帮助"
				case R.id.help_layout: {
					final Intent intent = new Intent(D_AccountMainActivity.this, HelpActivity.class);
					startActivity(intent);
				}
				break;
				
				// "版本更新"
				case R.id.version_update_layout: {
					Toast.makeText(D_AccountMainActivity.this, "版本更新", 0).show();
				}
				break;
				
				// "关于"
				case R.id.about_layout: {
					final Intent intent = new Intent(D_AccountMainActivity.this, AboutActivity.class);
					startActivity(intent);
				}
				break;
				
				// "退出登录"
				case R.id.log_out_Button: {
					requestUserAccountLogout();
				}
				break;
				
				// "个人信息"
				case R.id.user_info_layout: {
					final Intent intent = new Intent(D_AccountMainActivity.this, UserInformationActivity.class);
					startActivity(intent);
				}
				break;
				
				// "系统通知"
				case R.id.system_notification_layout: {
					final Intent intent = new Intent(D_AccountMainActivity.this, SystemMessageIndexActivity.class);
					startActivity(intent);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	// 开始请求, 2.15 获取账号首页信息
	private void requestUserAccountIndexInfo(boolean isShowProgressDialog) {
		final AccountIndexNetRequestBean netRequestDomainBean = new AccountIndexNetRequestBean();
		netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, netRequestDomainBean, NetRequestTagEnum.GET_ACCOUNT_INFO, domainNetRespondCallback);
		if (isShowProgressDialog && netRequestIndexForGetAccountInfo != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	// 开始请求, 用户退出登录
	private void requestUserAccountLogout() {
		ToolsFunctionForThisProgect.clearLogonInfo();
		loadContentViewForNotLogged();
		final LogoutNetRequestBean netRequestDomainBean = new LogoutNetRequestBean();
		DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(D_AccountMainActivity.this, netRequestDomainBean, NetRequestTagEnum.USER_LOGOUT, domainNetRespondCallback);
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
			
			//
			SimpleProgressDialog.dismiss(D_AccountMainActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(D_AccountMainActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				// 加载用户已经登录对应的UI
				loadContentViewForLogged(accountIndexNetRespondBean);
				accountIndexNetRespondBean = null;
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.GET_ACCOUNT_INFO == requestEvent) {
			netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.GET_ACCOUNT_INFO) {// 2.15 获取账号首页信息
				accountIndexNetRespondBean = (AccountIndexNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, accountIndexNetRespondBean.toString());
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				handler.sendMessage(msg);
			} else if (requestEvent == NetRequestTagEnum.USER_LOGOUT) {// 2.17 登出
				DebugLog.e(TAG, "用户退出登录");
			}
		}
	};
	
	private void setUserAddress(String userAddress) {
		// 设置 "当前位置"
		final TextView userLocationTextView = (TextView) findViewById(R.id.user_location_TextView);
		if (userLocationTextView != null) {
			// 这里的判断是必须的, 因为 "当前位置" TextView只有在 "已登录状态" 页面布局中才存在
			userLocationTextView.setText(userAddress);
			userLocationTextView.invalidate();
		}
	}
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
}
