package cn.airizu.toolutils;

import cn.airizu.activity.MyApplication;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 用于判断当前网络连接类型，接入点，代理服务器信息。 可以自动补齐代理(有用户只设置了apn,而没有设置代理.
 */
public class NetConnectionManageTools {
	
	private static final String TAG = NetConnectionManageTools.class.getSimpleName();
	
	/**
	 * constructor.
	 * 
	 * @param context context
	 */
	public NetConnectionManageTools() {
		
		checkActiveNetworkType();
	}
	
	public static enum AvailableNetTypeEnum {
		// 当前没有可用的网络
		NO_AVAILABLE,
		// 通过WIFI上网
		WIFI,
		// 通过手机上网
		MOBILE
	}
	
	/**
	 * 当前网络是否可用
	 */
	private boolean isNetAvailable = false;
	
	/**
	 * current apn.
	 */
	private String mApn = "";
	
	/**
	 * apn proxy.
	 */
	private String mProxy = "";
	
	/**
	 * proxy port.
	 */
	private String mPort = "";
	
	/**
	 * 当前可用的网络类型
	 */
	private AvailableNetTypeEnum availableNetTypeEnum = AvailableNetTypeEnum.NO_AVAILABLE;
	
	/**
	 * 当前网络是否使用wap网络 (wifi 或者 cmnet等不使用 wap网络)
	 */
	private boolean isWapNetwork;
	
	/** prefered apn url. */
	public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	
	// 检查当前系统 apn 设置 状态.
	// APN(Access Point Name) 即“接入点名称”，无论使用联通3G还是移动2G网络，都必须通过手机APN设置，是手机上网时必须配置的参数。
	// APN类型 : 中国联通的2G业务WAP浏览器中使用的APN为“UNIWAP”，3G业务WAP浏览器使用的APN为“3GWAP”；
	// 中国联通的2G上公网使用的APN为“UNINET”，3G业务上网卡及上公网使用的APN为“3GNET“。
	// 中国移动上内网的APN为“CMWAP“，上网卡及上公网使用的APN为“CMNET“。
	private void checkApn() {
		
		// getContentResolver() : Return a ContentResolver instance for your application's package.
		final Cursor cursor = MyApplication.getApplication().getContentResolver().query(PREFERRED_APN_URI, new String[]
		{ "_id", "apn", "proxy", "port" }, null, null, null);
		
		do {
			if (cursor == null) {
				break;
			}
			
			// Move the cursor to the first row.
			// This method will return false if the cursor is empty.
			if (!cursor.moveToFirst()) {
				break;
			}
			
			final int anpIndex = cursor.getColumnIndex("apn");
			final int proxyIndex = cursor.getColumnIndex("proxy");
			final int portIndex = cursor.getColumnIndex("port");
			
			mApn = cursor.getString(anpIndex);
			mProxy = cursor.getString(proxyIndex);
			mPort = cursor.getString(portIndex);
			
			//
			// availableNetTypeEnum = mApn;
			
			DebugLog.d(TAG, "default apn=" + mApn + " ,proxy=" + mProxy + ", port=" + mPort);
			
			if (!TextUtils.isEmpty(mProxy)) {// 如果设置了代理
			
				if ("10.0.0.172".equals(mProxy.trim())) {
					// 当前网络连接类型为cmwap || uniwap
					isWapNetwork = true;
					mPort = "80";
				} else if ("10.0.0.200".equals(mProxy.trim())) {
					isWapNetwork = true;
					mPort = "80";
				} else {
					// 否则为net
					isWapNetwork = false;
				}
				
			} else if (mApn != null) {// 如果用户只设置了apn，没有设置代理，我们自动补齐
			
				String strApn = mApn.toUpperCase();
				if (strApn.equals("CMWAP") || strApn.equals("UNIWAP") || strApn.equals("3GWAP")) {
					isWapNetwork = true;
					mProxy = "10.0.0.172";
					mPort = "80";
				} else if (strApn.equals("CTWAP")) {
					isWapNetwork = true;
					mProxy = "10.0.0.200";
					mPort = "80";
				}
				
			} else {// 其它网络都看作是net
			
				isWapNetwork = false;
			}
			
			cursor.close();
			
			DebugLog.d(TAG, "adjust apn=" + mApn + " ,proxy=" + mProxy + ", port=" + mPort);
			
		} while (false);
		
	}
	
	/**
	 * 检查当前网络类型。
	 * 
	 * @param context context
	 */
	private void checkActiveNetworkType() {
		
		final ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// NetworkInfo: type: WIFI[], state: CONNECTED/CONNECTED, reason: (unspecified), extra: (none), roaming: false, failover: false, isAvailable: true
		final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		
		do {
			if (null == activeNetInfo) {
				// 当前设备没有激活的网络可用(比如在飞行模式下, 这个 activeNetInfo 就为null
				break;
			}
			
			if (!activeNetInfo.isAvailable()) {
				// 当前设备没有激活的网络可用
				break;
			}
			isNetAvailable = true;
			DebugLog.d(TAG, "当前可用的网络类型 --> " + activeNetInfo.toString());
			
			// wifi 或者 cmnet等不使用 wap网络(也就是说, 在andorid手机中设置apn的地方, 可以设置当前手机使用CMNET访问网络)
			if ("WIFI".equals(activeNetInfo.getTypeName().toUpperCase())) {
				availableNetTypeEnum = AvailableNetTypeEnum.WIFI;
				isWapNetwork = false;
			} else {
				availableNetTypeEnum = AvailableNetTypeEnum.MOBILE;
				checkApn();
			}
		} while (false);
	}
	
	/**
	 * 判断当前网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetAvailable() {
		return isNetAvailable;
	}
	
	/**
	 * 当前网络连接是否是wap网络。
	 * 
	 * @return cmwap 3gwap ctwap 返回true
	 */
	public boolean isWapNetwork() {
		return isWapNetwork;
	}
	
	/**
	 * 获取当前网络连接apn.
	 * 
	 * @return apn
	 */
	public String getApn() {
		return mApn;
	}
	
	/**
	 * 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172.
	 * 
	 * @return 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172
	 */
	public String getProxy() {
		return mProxy;
	}
	
	/**
	 * 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80.
	 * 
	 * @return 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80
	 */
	public String getProxyPort() {
		return mPort;
	}
	
	/**
	 * 获得当前网络类型
	 * 
	 * @return WIFI, MOBILE
	 */
	public AvailableNetTypeEnum getAvailableNetType() {
		return availableNetTypeEnum;
	}
	
	@Override
	public String toString() {
		return "NetConnectionManageTools [isNetAvailable=" + isNetAvailable + ", mApn=" + mApn + ", mProxy=" + mProxy + ", mPort=" + mPort + ", availableNetTypeEnum=" + availableNetTypeEnum
				+ ", isWapNetwork=" + isWapNetwork + "]";
	}
	
}
