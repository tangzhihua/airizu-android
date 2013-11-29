package cn.airizu.activity;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.IBinder;
import android.text.TextUtils;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_login.LogonNetRequestBean;
import cn.airizu.domain.protocol.account_login.LogonNetRespondBean;
import cn.airizu.domain.protocol.account_version.VersionNetRequestBean;
import cn.airizu.domain.protocol.account_version.VersionNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;

import com.baidu.mapapi.MKAddrInfo;

/**
 * 启动App后, 就会激活当前服务, 当前服务的功能就是加载一些耗时的资源
 * 
 * @author zhihua.tang
 */
// 如果先采用startService()方法启动服务,然后调用bindService()方法绑定到服务，
// 再调用unbindService()方法解除绑定，最后调用bindService()方法再次绑定到服务，
// 触发的生命周期方法如下：
// onCreate()onStart()onBind()onUnbind()[重载后的方法需返回true]onRebind()
public class PreLoadedDataService extends Service {
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DebugLog.i(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}
	
	// onCreate()该方法在服务被创建时调用，该方法只会被调用一次，
	// 无论调用多少次startService()或bindService()方法，服务也只被创建一次。
	@Override
	public void onCreate() {
		super.onCreate();
		DebugLog.i(TAG, "onCreate");
		
		// 获取用户所在位置信息
		if (!loadingUserLocationInfoThread.isAlive()) {
			loadingUserLocationInfoThread.start();
		}
		
		// 加载缓存在本地文件中的数据
		if (!loadingDataThread.isAlive()) {
			loadingDataThread.start();
		}
		
		// 检查 "新版本信息"
		requestVersionInfo();
		
		// 用户自动登录
		userAutoLoginRequest();
	}
	
	// onStart() 只有采用Context.startService()方法启动服务时才会回调该方法。
	// 该方法在服务开始运行时被调用。多次调用startService()方法尽管不会多次创建服务，
	// 但onStart() 方法会被多次调用。
	@Override
	public void onStart(Intent intent, int startId) {
		DebugLog.i(TAG, "onStart");
		super.onStart(intent, startId);
	}
	
	// onDestroy()该方法在服务被终止时调用。
	@Override
	public void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		
		loadingUserLocationInfoThread.interrupt();
		loadingDataThread.interrupt();
		
		if (userLocationForBaiduLBS != null) {
			userLocationForBaiduLBS.cancelAddrInfoDelegate();
			userLocationForBaiduLBS.cancelLocationDelegate();
			userLocationForBaiduLBS.stopLocationRequest();
		}
		
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		DebugLog.i(TAG, "onLowMemory");
		super.onLowMemory();
	}
	
	@Override
	public void onRebind(Intent intent) {
		DebugLog.i(TAG, "onRebind");
		super.onRebind(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DebugLog.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	// onBind()只有采用Context.bindService()方法启动服务时才会回调该方法。
	// 该方法在调用者与服务绑定时被调用，当调用者与服务已经绑定，
	// 多次调用Context.bindService()方法并不会导致该方法被多次调用。
	@Override
	public IBinder onBind(Intent arg0) {
		DebugLog.i(TAG, "onBind");
		return null;
	}
	
	// onUnbind()只有采用Context.bindService()方法启动服务时才会回调该方法。
	// 该方法在调用者与服务解除绑定时被调用。
	@Override
	public boolean onUnbind(Intent intent) {
		DebugLog.i(TAG, "onUnbind");
		return super.onUnbind(intent);
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private SimpleLocationForBaiduLBS userLocationForBaiduLBS;
	private SimpleLocationForBaiduLBS.LocationDelegate locationDelegate = new SimpleLocationForBaiduLBS.LocationDelegate() {
		
		@Override
		public void locationCallback(Location location) {
			// 发送 "获取用户当前坐标成功" 的广播消息
			Intent intent = new Intent();
			intent.setAction(GlobalConstant.UserActionEnum.GET_USER_LOCATION_SUCCESS.name());
			PreLoadedDataService.this.sendBroadcast(intent);
			
			if (GlobalDataCacheForMemorySingleton.getInstance().getLastMKAddrInfo() != null) {
				userLocationForBaiduLBS.stopLocationRequest();
			}
		}
	};
	private SimpleLocationForBaiduLBS.AddrInfoDelegate addrInfoDelegate = new SimpleLocationForBaiduLBS.AddrInfoDelegate() {
		
		@Override
		public void addrInfoCallback(MKAddrInfo addrInfo) {
			// 发送  "获取用户当前地址成功" 的广播消息
			Intent intent = new Intent();
			intent.setAction(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name());
			PreLoadedDataService.this.sendBroadcast(intent);
		}
	};
	private Thread loadingUserLocationInfoThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			do {
				if (loadingUserLocationInfoThread.isInterrupted()) {
					break;
				}
				userLocationForBaiduLBS = new SimpleLocationForBaiduLBS(locationDelegate, addrInfoDelegate, true);
				userLocationForBaiduLBS.startLocationRequest();
			} while (false);
		}
	});
	private Thread loadingDataThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			do {
				if (loadingDataThread.isInterrupted()) {
					break;
				}
				GlobalDataCacheForNeedSaveToFileSystem.readCitysNetRespondBean();
				if (loadingDataThread.isInterrupted()) {
					break;
				}
				GlobalDataCacheForNeedSaveToFileSystem.readDictionaryNetRespondBean();
			} while (false);
		}
	});
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static enum NetRequestTagEnum {
		//
		VERSION_INFO,
		//
		USER_LOGIN
	};
	
	private void requestVersionInfo() {
		final String currentVersion = ToolsFunctionForThisProgect.getApplicationVersion();
		VersionNetRequestBean versionNetRequestBean = new VersionNetRequestBean(currentVersion);
		DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, versionNetRequestBean, NetRequestTagEnum.VERSION_INFO, domainNetRespondCallback);
	}
	
	private void userAutoLoginRequest() {
		GlobalDataCacheForNeedSaveToFileSystem.readUserLoginInfo();
		
		do {
			if (!GlobalDataCacheForMemorySingleton.getInstance.isNeedAutologin()) {
				break;
			}
			final String username = GlobalDataCacheForMemorySingleton.getInstance.getUsernameForLastSuccessfulLogon();
			final String password = GlobalDataCacheForMemorySingleton.getInstance.getPasswordForLastSuccessfulLogon();
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				break;
			}
			LogonNetRequestBean.Builder logonNetRequestBeanBuilder = new LogonNetRequestBean.Builder(username, password);
			DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, logonNetRequestBeanBuilder.builder(), NetRequestTagEnum.USER_LOGIN, domainNetRespondCallback);
		} while (false);
	}
	
	private IDomainNetRespondCallback domainNetRespondCallback = new IDomainNetRespondCallback() {
		@Override
		public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean) {
			DebugLog.i(TAG, "domainNetRespondHandleInNonUIThread --- start ! ");
			
			if (errorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.VERSION_INFO) {// 2.19 版本更新
				VersionNetRespondBean versionNetRespondBean = (VersionNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, versionNetRespondBean.toString());
				
				// 检测本地版本和服务器版本是否相同, 如果不相同, 就要下载服务器的新版本
				if (!versionNetRespondBean.getNewVersion().equals(ToolsFunctionForThisProgect.getApplicationVersion())) {
					DebugLog.e(TAG, "服务器端有新版本需要下载! ");
					
					// 缓存网络侧的新版本信息
					GlobalDataCacheForMemorySingleton.getInstance.setVersionNetRespondBean(versionNetRespondBean);
					
					GlobalDataCacheForMemorySingleton.getInstance.setNewVersionDetectState(GlobalConstant.NewVersionDetectStateEnum.HAS_NEW_VERSION);
				} else {
					GlobalDataCacheForMemorySingleton.getInstance.setNewVersionDetectState(GlobalConstant.NewVersionDetectStateEnum.LOCAL_APP_IS_THE_LATEST);
				}
				
			} else if (requestEvent == NetRequestTagEnum.USER_LOGIN) {//
			
				DebugLog.e(TAG, "自动登录成功!");
				
				LogonNetRespondBean logonNetRespondBean = (LogonNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, logonNetRespondBean.toString());
				
				// 如果 全局变量缓存区中已经有 "用户登录网络响应业务Bean", 证明用户再次登录了, 启动App时的自动登录不能覆盖用户自己登录的账户信息
				if (GlobalDataCacheForMemorySingleton.getInstance.getLogonNetRespondBean() == null) {
					// 保存用户成功登录后的信息
					final String username = GlobalDataCacheForMemorySingleton.getInstance.getUsernameForLastSuccessfulLogon();
					final String password = GlobalDataCacheForMemorySingleton.getInstance.getPasswordForLastSuccessfulLogon();
					ToolsFunctionForThisProgect.noteLogonSuccessfulInfo(logonNetRespondBean, username, password);
				}
			}
		}
	};
}
