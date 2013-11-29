package cn.airizu.global_data_cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.text.TextUtils;
import cn.airizu.activity.MyApplication;
import cn.airizu.domain.protocol.address_citys.CitysNetRespondBean;
import cn.airizu.domain.protocol.room_dictionary.DictionaryNetRespondBean;
import cn.airizu.domain.protocol.room_recommend.RecommendNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.FileTools;

public final class GlobalDataCacheForNeedSaveToFileSystem {
	private final static String TAG = GlobalDataCacheForNeedSaveToFileSystem.class.getSimpleName();
	
	private GlobalDataCacheForNeedSaveToFileSystem() {
		
	}
	
	public static synchronized void readRecommendNetRespondBean() {
		//
		DebugLog.i(TAG, "start loading --> recommendNetRespondBean");
		final RecommendNetRespondBean recommendNetRespondBean = (RecommendNetRespondBean) deserializeObjectFromFile(CacheDataNameForSaveToFile.RECOMMEND_CITY.name());
		if (recommendNetRespondBean != null) {
			DebugLog.i(TAG, "recommendNetRespondBean-->" + recommendNetRespondBean.toString());
			if (GlobalDataCacheForMemorySingleton.getInstance().getRecommendNetRespondBean() == null) {
				GlobalDataCacheForMemorySingleton.getInstance().setRecommendNetRespondBean(recommendNetRespondBean);
			}
		} else {
			DebugLog.i(TAG, "recommendNetRespondBean is null");
		}
	}
	
	public static synchronized void readCitysNetRespondBean() {
		//
		DebugLog.i(TAG, "start loading --> citysNetRespondBean");
		final CitysNetRespondBean citysNetRespondBean = (CitysNetRespondBean) deserializeObjectFromFile(CacheDataNameForSaveToFile.CITY_LIST.name());
		if (citysNetRespondBean != null) {
			DebugLog.i(TAG, "citysNetRespondBean --> " + citysNetRespondBean.toString());
			if (GlobalDataCacheForMemorySingleton.getInstance().getCityInfoNetRespondBean() == null) {
				GlobalDataCacheForMemorySingleton.getInstance().setCityInfoNetRespondBean(citysNetRespondBean);
			}
		} else {
			DebugLog.i(TAG, "citysNetRespondBean is null");
		}
	}
	
	public static synchronized void readDictionaryNetRespondBean() {
		//
		DebugLog.i(TAG, "start loading --> dictionaryNetRespondBean");
		final DictionaryNetRespondBean dictionaryNetRespondBean = (DictionaryNetRespondBean) deserializeObjectFromFile(CacheDataNameForSaveToFile.DICTIONARY.name());
		if (dictionaryNetRespondBean != null) {
			DebugLog.i(TAG, "dictionaryNetRespondBean --> " + dictionaryNetRespondBean.toString());
			if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
				GlobalDataCacheForMemorySingleton.getInstance().setDictionaryNetRespondBean(dictionaryNetRespondBean);
			}
		} else {
			DebugLog.i(TAG, "dictionaryNetRespondBean is null");
		}
	}
	
	public static synchronized void readUserLoginInfo() {
		//
		DebugLog.i(TAG, "start loading --> isNeedAutologin");
		final Boolean isNeedAutoLogin = (Boolean) deserializeObjectFromFile(CacheDataNameForSaveToFile.AUTO_LOGIN_MARK.name());
		if (isNeedAutoLogin != null) {
			DebugLog.i(TAG, "isNeedAutologin=" + isNeedAutoLogin.toString());
			// TODO:这里稍后处理
			GlobalDataCacheForMemorySingleton.getInstance().setNeedAutologin(isNeedAutoLogin.booleanValue());
		} else {
			DebugLog.i(TAG, "isNeedAutologin is null");
		}
		//
		DebugLog.i(TAG, "start loading --> usernameForLastSuccessfulLogon");
		final String usernameForLastSuccessfulLogon = (String) deserializeObjectFromFile(CacheDataNameForSaveToFile.USERNAME_FOR_LAST_SUCCESSFUL_LOGON.name());
		if (!TextUtils.isEmpty(usernameForLastSuccessfulLogon)) {
			DebugLog.i(TAG, "usernameForLastSuccessfulLogon=" + usernameForLastSuccessfulLogon);
			if (GlobalDataCacheForMemorySingleton.getInstance().getUsernameForLastSuccessfulLogon() == null) {
				GlobalDataCacheForMemorySingleton.getInstance().setUsernameForLastSuccessfulLogon(usernameForLastSuccessfulLogon);
			}
		} else {
			DebugLog.i(TAG, "usernameForLastSuccessfulLogon is null");
		}
		//
		DebugLog.i(TAG, "start loading --> passwordForLastSuccessfulLogon");
		final String passwordForLastSuccessfulLogon = (String) deserializeObjectFromFile(CacheDataNameForSaveToFile.PASSWORD_FOR_LAST_SUCCESSFUL_LOGON.name());
		if (!TextUtils.isEmpty(passwordForLastSuccessfulLogon)) {
			DebugLog.i(TAG, "passwordForLastSuccessfulLogon=" + passwordForLastSuccessfulLogon);
			if (GlobalDataCacheForMemorySingleton.getInstance().getPasswordForLastSuccessfulLogon() == null) {
				GlobalDataCacheForMemorySingleton.getInstance().setPasswordForLastSuccessfulLogon(passwordForLastSuccessfulLogon);
			}
		} else {
			DebugLog.i(TAG, "passwordForLastSuccessfulLogon is null");
		}
	}
	
	public static synchronized void saveRecommendNetRespondBean() {
		//
		final RecommendNetRespondBean recommendNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance().getRecommendNetRespondBean();
		if (recommendNetRespondBean != null) {
			serializeObjectToFile(CacheDataNameForSaveToFile.RECOMMEND_CITY.name(), recommendNetRespondBean);
		}
	}
	
	public static synchronized void saveCitysNetRespondBean() {
		//
		final CitysNetRespondBean citysNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance().getCityInfoNetRespondBean();
		if (citysNetRespondBean != null) {
			serializeObjectToFile(CacheDataNameForSaveToFile.CITY_LIST.name(), citysNetRespondBean);
		}
	}
	
	public static synchronized void saveDictionaryNetRespondBean() {
		//
		final DictionaryNetRespondBean dictionaryNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean();
		if (dictionaryNetRespondBean != null) {
			serializeObjectToFile(CacheDataNameForSaveToFile.DICTIONARY.name(), dictionaryNetRespondBean);
		}
	}
	
	public static synchronized void saveUserLoginInfo() {
		//
		final Boolean isNeedAutologin = Boolean.valueOf(GlobalDataCacheForMemorySingleton.getInstance().isNeedAutologin());
		serializeObjectToFile(CacheDataNameForSaveToFile.AUTO_LOGIN_MARK.name(), isNeedAutologin);
		//
		final String usernameForLastSuccessfulLogon = GlobalDataCacheForMemorySingleton.getInstance().getUsernameForLastSuccessfulLogon();
		if (!TextUtils.isEmpty(usernameForLastSuccessfulLogon)) {
			serializeObjectToFile(CacheDataNameForSaveToFile.USERNAME_FOR_LAST_SUCCESSFUL_LOGON.name(), usernameForLastSuccessfulLogon);
		}
		//
		final String passwordForLastSuccessfulLogon = GlobalDataCacheForMemorySingleton.getInstance().getPasswordForLastSuccessfulLogon();
		if (!TextUtils.isEmpty(passwordForLastSuccessfulLogon)) {
			serializeObjectToFile(CacheDataNameForSaveToFile.PASSWORD_FOR_LAST_SUCCESSFUL_LOGON.name(), passwordForLastSuccessfulLogon);
		}
	}
	
	public static synchronized void saveAllCacheData() {
		// 保存 "推荐城市"
		saveRecommendNetRespondBean();
		// 保存 "城市列表"
		saveCitysNetRespondBean();
		// 保存 "数据字典"
		saveDictionaryNetRespondBean();
		// 保存 "用户信息"
		saveUserLoginInfo();
	}
	
	private enum CacheDataNameForSaveToFile {
		// 推荐城市
		RECOMMEND_CITY,
		// 城市列表
		CITY_LIST,
		// 字典
		DICTIONARY,
		// 自动登录的标志
		AUTO_LOGIN_MARK,
		// 用户最后一次成功登录时的用户名
		USERNAME_FOR_LAST_SUCCESSFUL_LOGON,
		// 用户最后一次成功登录时的密码
		PASSWORD_FOR_LAST_SUCCESSFUL_LOGON,
	};
	
	/**
	 * 将一个对象, 序列化到文件中
	 * 
	 * @param context
	 * @param fileName
	 * @param object
	 */
	private static void serializeObjectToFile(final String fileName, final Object object) {
		try {
			final FileOutputStream fs = MyApplication.getApplication().openFileOutput(fileName, Context.MODE_PRIVATE);
			final ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(object);
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 从一个文件中, 反序列化一个对象
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	private static Object deserializeObjectFromFile(final String fileName) {
		Object object = null;
		try {
			if (FileTools.fileIsExist(MyApplication.getApplication().getFilesDir() + "/" + fileName)) {
				final FileInputStream fs = MyApplication.getApplication().openFileInput(fileName);
				final ObjectInputStream os = new ObjectInputStream(fs);
				object = os.readObject();
				os.close();
			}
		} catch (Exception ex) {
			object = null;
			ex.printStackTrace();
		}
		
		return object;
	}
}
