package cn.airizu.global_data_cache;

import android.location.Location;
import cn.airizu.domain.protocol.account_help.HelpNetRespondBean;
import cn.airizu.domain.protocol.account_login.LogonNetRespondBean;
import cn.airizu.domain.protocol.account_version.VersionNetRespondBean;
import cn.airizu.domain.protocol.address_citys.CitysNetRespondBean;
import cn.airizu.domain.protocol.review_item.ReviewItemNetRespondBean;
import cn.airizu.domain.protocol.room_dictionary.DictionaryNetRespondBean;
import cn.airizu.domain.protocol.room_recommend.RecommendNetRespondBean;

import com.baidu.mapapi.MKAddrInfo;

/**
 * 需要全局缓存的数据
 * 
 * @author zhihua.tang
 */
public enum GlobalDataCacheForMemorySingleton {
	getInstance;
	
	public static synchronized GlobalDataCacheForMemorySingleton getInstance() {
		
		return getInstance;
	}
	
	// ////////////////////////////////////////////////////////////////////////////
	// 是否需要自动登录的标志
	private boolean isNeedAutologin;
	
	public boolean isNeedAutologin() {
		return isNeedAutologin;
	}
	
	public synchronized void setNeedAutologin(boolean isNeedAutologin) {
		this.isNeedAutologin = isNeedAutologin;
	}
	
	// 客户端应用版本号
	private String clientVersion;
	
	public String getClientVersion() {
		return clientVersion;
	}
	
	public synchronized void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	
	// 客户端 Android 版本号
	private String clientAVersion;
	
	public String getClientAVersion() {
		return clientAVersion;
	}
	
	public synchronized void setClientAVersion(String clientAVersion) {
		this.clientAVersion = clientAVersion;
	}
	
	// 屏幕大小
	private String screenSize;
	
	public String getScreenSize() {
		return screenSize;
	}
	
	public synchronized void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}
	
	// 用户登录成功后, 服务器返回的信息(判断有无此对象来判断当前用户是否已经登录)
	private LogonNetRespondBean logonNetRespondBean;
	
	public LogonNetRespondBean getLogonNetRespondBean() {
		return logonNetRespondBean;
	}
	
	public synchronized void setLogonNetRespondBean(LogonNetRespondBean logonNetRespondBean) {
		this.logonNetRespondBean = logonNetRespondBean;
	}
	
	// 当前用户的账户信息
	// private AccountIndexNetRespondBean accountIndexNetRespondBean;
	//
	// public AccountIndexNetRespondBean getAccountIndexNetRespondBean() {
	// return accountIndexNetRespondBean;
	// }
	//
	// public synchronized void setAccountIndexNetRespondBean(AccountIndexNetRespondBean accountIndexNetRespondBean) {
	// this.accountIndexNetRespondBean = accountIndexNetRespondBean;
	// }
	
	// 用户最后一次登录成功时的用户名
	private String usernameForLastSuccessfulLogon;
	
	public String getUsernameForLastSuccessfulLogon() {
		return usernameForLastSuccessfulLogon;
	}
	
	public synchronized void setUsernameForLastSuccessfulLogon(String usernameForLastSuccessfulLogon) {
		this.usernameForLastSuccessfulLogon = usernameForLastSuccessfulLogon;
	}
	
	// 用户最后一次登录成功时的密码
	private String passwordForLastSuccessfulLogon;
	
	public String getPasswordForLastSuccessfulLogon() {
		return passwordForLastSuccessfulLogon;
	}
	
	public synchronized void setPasswordForLastSuccessfulLogon(String passwordForLastSuccessfulLogon) {
		this.passwordForLastSuccessfulLogon = passwordForLastSuccessfulLogon;
	}
	
	// 城市信息列表(2.6 搜索城市)
	private CitysNetRespondBean cityInfoNetRespondBean;
	
	public CitysNetRespondBean getCityInfoNetRespondBean() {
		return cityInfoNetRespondBean;
	}
	
	public synchronized void setCityInfoNetRespondBean(CitysNetRespondBean cityInfoNetRespondBean) {
		this.cityInfoNetRespondBean = cityInfoNetRespondBean;
	}
	
	// 字典接口 : "房屋类型" "出租方式" "设施设备" (2.8 初始化字典)
	private DictionaryNetRespondBean dictionaryNetRespondBean;
	
	public DictionaryNetRespondBean getDictionaryNetRespondBean() {
		return dictionaryNetRespondBean;
	}
	
	public synchronized void setDictionaryNetRespondBean(DictionaryNetRespondBean dictionaryNetRespondBean) {
		this.dictionaryNetRespondBean = dictionaryNetRespondBean;
	}
	
	// 用户最后一次的 "附近" 坐标 (这里使用的是百度的LBS库)
	private Location lastLocation;
	
	public Location getLastLocation() {
		return lastLocation;
	}
	
	public synchronized void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}
	
	// 用户最后一次的 "附近" 位置信息 (这里使用的是百度的LBS库)
	private MKAddrInfo lastMKAddrInfo;
	
	public MKAddrInfo getLastMKAddrInfo() {
		return lastMKAddrInfo;
	}
	
	public synchronized void setLastMKAddrInfo(MKAddrInfo lastMKAddrInfo) {
		this.lastMKAddrInfo = lastMKAddrInfo;
	}
	
	// 推荐城市
	private RecommendNetRespondBean recommendNetRespondBean;
	
	public RecommendNetRespondBean getRecommendNetRespondBean() {
		return recommendNetRespondBean;
	}
	
	public synchronized void setRecommendNetRespondBean(RecommendNetRespondBean recommendNetRespondBean) {
		this.recommendNetRespondBean = recommendNetRespondBean;
	}
	
	// 帮助信息
	private HelpNetRespondBean helpNetRespondBean;
	
	public HelpNetRespondBean getHelpNetRespondBean() {
		return helpNetRespondBean;
	}
	
	public synchronized void setHelpNetRespondBean(HelpNetRespondBean helpNetRespondBean) {
		this.helpNetRespondBean = helpNetRespondBean;
	}
	
	// 评论项
	private ReviewItemNetRespondBean reviewItemNetRespondBean;
	
	public ReviewItemNetRespondBean getReviewItemNetRespondBean() {
		return reviewItemNetRespondBean;
	}
	
	public synchronized void setReviewItemNetRespondBean(ReviewItemNetRespondBean reviewItemNetRespondBean) {
		this.reviewItemNetRespondBean = reviewItemNetRespondBean;
	}
	
	// 版本信息
	private VersionNetRespondBean versionNetRespondBean;
	
	public VersionNetRespondBean getVersionNetRespondBean() {
		return versionNetRespondBean;
	}
	
	public synchronized void setVersionNetRespondBean(VersionNetRespondBean versionNetRespondBean) {
		this.versionNetRespondBean = versionNetRespondBean;
	}
	
	// 新版本检测状态
	private GlobalConstant.NewVersionDetectStateEnum newVersionDetectState = GlobalConstant.NewVersionDetectStateEnum.NOT_YET_DETECTED;
	
	public GlobalConstant.NewVersionDetectStateEnum getNewVersionDetectState() {
		return newVersionDetectState;
	}
	
	public synchronized void setNewVersionDetectState(GlobalConstant.NewVersionDetectStateEnum newVersionDetectState) {
		this.newVersionDetectState = newVersionDetectState;
	}
	
}
