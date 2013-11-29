package cn.airizu.toolutils.lbs.baidu;

import android.content.Context;
import android.location.LocationManager;
import cn.airizu.activity.MyApplication;
import cn.airizu.toolutils.DebugLog;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;

public final class BaiduLbsSingleton {
	private static final String TAG = "BaiduLBSSSingleton";
	
	private BaiduLbsSingleton() {
	}
	
	private static BaiduLbsSingleton instance = new BaiduLbsSingleton();
	
	public static synchronized BaiduLbsSingleton getInstance() {
		return instance;
	}
	
	// 授权Key
	// 申请地址：http://dev.baidu.com/wiki/static/imap/key/
	private String mStrKey = "198E24B94477359734BACB87B802396CE36EFE3A";// "请输入您的Key";
	
	// 百度MapAPI的管理类
	private BMapManager baiduMapManager;
	
	public BMapManager getBaiduMapManager() {
		return baiduMapManager;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	private static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			// 您的网络出错啦
			DebugLog.d(TAG, "onGetNetworkState error is " + iError);
		}
		
		@Override
		public void onGetPermissionState(int iError) {
			DebugLog.d(TAG, "onGetPermissionState error is " + iError);
			
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				DebugLog.d(TAG, "onGetNetworkState error is " + iError);
				// 授权Key错误：
				// "请在BMapApiDemoApp.java文件输入正确的授权Key！"
			}
		}
	}
	
	public BMapManager initMapManager() {
		
		do {
			if (baiduMapManager != null) {
				break;
			}
			baiduMapManager = new BMapManager(MyApplication.getApplication().getApplicationContext());
			
			// strKey - 申请的授权验证码 listener - 注册回调事件
			if (!baiduMapManager.init(this.mStrKey, new MyGeneralListener())) {
				break;
			}
			// 设置通知间隔
			// iMaxSecond - 最大通知间隔,单位:秒
			// iMinSecond - 最小通知间隔,单位:秒
			if (!baiduMapManager.getLocationManager().setNotifyInternal(10, 5)) {
				break;
			}
			
			return baiduMapManager;
		} while (false);
		
		baiduMapManager = null;
		return null;
	}
	
	public void releaseMapManager() {
		if (baiduMapManager != null) {
			baiduMapManager.destroy();
			baiduMapManager = null;
		}
	}
	
	public boolean gpsIsEnable() {
		LocationManager locationManager = (LocationManager) MyApplication.getApplication().getSystemService(Context.LOCATION_SERVICE);
		do {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				DebugLog.i(TAG, "GPS_PROVIDER is Enabled ! ");
				break;
			}
			DebugLog.e(TAG, "GPS_PROVIDER is Unavailable ! ");
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				DebugLog.e(TAG, "NETWORK_PROVIDER is Enabled ! ");
				break;
			}
			DebugLog.e(TAG, "NETWORK_PROVIDER is Unavailable ! ");
			return false;
		} while (false);
		return true;
	}
	
	public boolean openGPS() {
		if (baiduMapManager == null) {
			assert false : "baiduMapManager is null";
			return false;
		}
		
		MKLocationManager locationManager = baiduMapManager.getLocationManager();
		boolean gpsIsEnable = locationManager.enableProvider(MKLocationManager.MK_GPS_PROVIDER);
		boolean networkIsEnable = locationManager.enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
		
		return gpsIsEnable || networkIsEnable;
	}
}
