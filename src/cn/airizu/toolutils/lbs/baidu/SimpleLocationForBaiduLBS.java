package cn.airizu.toolutils.lbs.baidu;

import android.location.Location;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;

public final class SimpleLocationForBaiduLBS {
	private final String TAG = this.getClass().getSimpleName();
	
	public static Location getLastLocation() {
		return GlobalDataCacheForMemorySingleton.getInstance().getLastLocation();
	}
	
	public static MKAddrInfo getLastMKAddrInfo() {
		return GlobalDataCacheForMemorySingleton.getInstance().getLastMKAddrInfo();
	}
	
	public static interface LocationDelegate {
		public void locationCallback(Location location);
	};
	
	public static interface AddrInfoDelegate {
		public void addrInfoCallback(MKAddrInfo location);
	};
	
	private LocationDelegate locationDelegate;
	private AddrInfoDelegate addrInfoDelegate;
	
	private final boolean isAutoOnOffBaiduMapManager;
	
	/**
	 * @param locationDelegate
	 * @param addrInfoDelegate
	 * @param isAutoOnOffBaiduMapManager 如果设置 true, 那么外部就不需要主动设置BaiduMapManager 的start方法和stop方法, 否则需要主动设置
	 */
	public SimpleLocationForBaiduLBS(LocationDelegate locationDelegate, AddrInfoDelegate addrInfoDelegate, boolean isAutoOnOffBaiduMapManager) {
		this.locationDelegate = locationDelegate;
		this.addrInfoDelegate = addrInfoDelegate;
		this.isAutoOnOffBaiduMapManager = isAutoOnOffBaiduMapManager;
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			DebugLog.i(TAG, "onLocationChanged");
			if (location == null) {
				DebugLog.e(TAG, "onLocationChanged:location is null");
				return;
			}
			GlobalDataCacheForMemorySingleton.getInstance().setLastLocation(location);
			
			if (locationDelegate != null) {
				locationDelegate.locationCallback(location);
				locationDelegate = null;
			}
			BaiduLbsSingleton.getInstance().getBaiduMapManager().getLocationManager().removeUpdates(locationListener);
			
			// 请求用户当前的位置信息
			GeoPoint ptCenter = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
			baiduMKSearch.reverseGeocode(ptCenter);
		}
	};
	
	private MKSearch baiduMKSearch; // 搜索模块，也可去掉地图模块独立使用
	private MKSearchListener baiduMkSearchListener = new MKSearchListener() {
		public void onGetAddrResult(MKAddrInfo res, int error) {
			DebugLog.i(TAG, "onGetAddrResult");
			
			do {
				if (error != 0) {
					DebugLog.e(TAG, "onGetAddrResult:error=" + error);
					break;
				}
				
				if (res.addressComponents == null) {
					DebugLog.e(TAG, "onGetAddrResult:res.addressComponents is null");
					break;
				}
				GlobalDataCacheForMemorySingleton.getInstance().setLastMKAddrInfo(res);
				
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.strAddr=" + res.strAddr);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.strBusiness=" + res.strBusiness);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.type=" + res.type);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.addressComponents.province=" + res.addressComponents.province);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.addressComponents.city=" + res.addressComponents.city);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.addressComponents.district=" + res.addressComponents.district);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.addressComponents.street=" + res.addressComponents.street);
				DebugLog.i(TAG, "onGetAddrResult:MKAddrInfo.addressComponents.streetNumber=" + res.addressComponents.streetNumber);
				
				if (addrInfoDelegate != null) {
					addrInfoDelegate.addrInfoCallback(res);
					addrInfoDelegate = null;
				}
				
				//
				stopLocationRequest();
			} while (false);
		}
		
		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private boolean isBusy = false;
	
	public boolean startLocationRequest() {
		DebugLog.i(TAG, "startLocationRequest");
		
		if (isBusy) {
			DebugLog.e(TAG, "isBusy");
			return true;
		}
		
		do {
			if (BaiduLbsSingleton.getInstance().getBaiduMapManager() == null) {
				BaiduLbsSingleton.getInstance().initMapManager();
			}
			if (BaiduLbsSingleton.getInstance().getBaiduMapManager() == null) {
				break;
			}
			if (isAutoOnOffBaiduMapManager) {
				BaiduLbsSingleton.getInstance().getBaiduMapManager().start();
			}
			BaiduLbsSingleton.getInstance().getBaiduMapManager().getLocationManager().requestLocationUpdates(locationListener);
			
			if (baiduMKSearch == null) {
				baiduMKSearch = new MKSearch();
				if (!baiduMKSearch.init(BaiduLbsSingleton.getInstance().getBaiduMapManager(), baiduMkSearchListener)) {
					break;
				}
			}
			
			// 一切OK
			isBusy = true;
			return true;
		} while (false);
		
		// 出现了问题
		if (BaiduLbsSingleton.getInstance().getBaiduMapManager() != null) {
			BaiduLbsSingleton.getInstance().getBaiduMapManager().getLocationManager().removeUpdates(locationListener);
			if (isAutoOnOffBaiduMapManager) {
				BaiduLbsSingleton.getInstance().getBaiduMapManager().stop();
			}
		}
		return false;
	}
	
	public void stopLocationRequest() {
		DebugLog.i(TAG, "stopLocationRequest");
		isBusy = false;
		
		if (BaiduLbsSingleton.getInstance().getBaiduMapManager() != null) {
			BaiduLbsSingleton.getInstance().getBaiduMapManager().getLocationManager().removeUpdates(locationListener);
			if (isAutoOnOffBaiduMapManager) {
				BaiduLbsSingleton.getInstance().getBaiduMapManager().stop();
			}
		}
	}
	
	public void cancelLocationDelegate() {
		locationDelegate = null;
	}
	
	public void cancelAddrInfoDelegate() {
		addrInfoDelegate = null;
	}
}
