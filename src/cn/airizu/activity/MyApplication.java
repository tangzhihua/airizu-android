package cn.airizu.activity;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.net_file.NetFileHelper;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.lbs.baidu.BaiduLbsSingleton;

public class MyApplication extends Application {
	private final String TAG = this.getClass().getSimpleName();
	
	// AirizuApplication 类对外的接口
	private static MyApplication self;
	
	public static Application getApplication() {
		return self;
	}
	
	@Override
	public void onCreate() {
		DebugLog.i(TAG, "onCreate");
		super.onCreate();
		
		// 必须在第一个行的位置
		self = this;
		
		// 单例类提前实例化
		NetFileHelper.getInstance();
		DomainProtocolNetHelperSingleton.getInstance();
		
		//
		BaiduLbsSingleton.getInstance().initMapManager();
		
		// 启动一个服务, 用于预加载相关数据
		Intent intent = new Intent(this, PreLoadedDataService.class);
		// 采用 startService() 启动的服务, 必须主动调用 stopService() 来主动停止
		startService(intent);
	}
	
	@Override
	public void onTerminate() {
		DebugLog.d(TAG, "onTerminate");
		// 父类方法, 必须调用
		super.onTerminate();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DebugLog.i(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		
	}
	
	@Override
	public void onLowMemory() {
		DebugLog.i(TAG, "onLowMemory");
		super.onLowMemory();
		
	}
}