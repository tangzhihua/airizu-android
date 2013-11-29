package cn.airizu.toolutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import cn.airizu.activity.MyApplication;
import cn.airizu.activity.PreLoadedDataService;
import cn.airizu.activity.R;
import cn.airizu.domain.protocol.account_login.LogonNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;

/**
 * 这里只放置, 在当前项目中会被用到的方法
 * 
 * @author zhihua.tang
 */
public final class ToolsFunctionForThisProgect {
	private static String TAG = "ToolsFunctionForThisProgect";
	
	private ToolsFunctionForThisProgect() {
		
	}
	
	/**
	 * 记录用户登录成功后的重要信息
	 * 
	 * @param logonNetRespondBean
	 * @param usernameForLastSuccessfulLogon
	 * @param passwordForLastSuccessfulLogon
	 */
	public static synchronized void noteLogonSuccessfulInfo(LogonNetRespondBean logonNetRespondBean, String usernameForLastSuccessfulLogon, String passwordForLastSuccessfulLogon) {
		
		if (logonNetRespondBean == null) {
			throw new IllegalArgumentException("logonNetRespondBean is null !");
		}
		
		if (TextUtils.isEmpty(usernameForLastSuccessfulLogon) || TextUtils.isEmpty(passwordForLastSuccessfulLogon)) {
			throw new IllegalArgumentException("username or password is empty ! ");
		}
		
		DebugLog.i(TAG, "logonRespond ---> " + logonNetRespondBean.toString());
		DebugLog.i(TAG, "username ---> " + usernameForLastSuccessfulLogon);
		DebugLog.i(TAG, "password ---> " + passwordForLastSuccessfulLogon);
		
		// 设置Cookie
		SimpleCookieSingleton.getInstance().add("JSESSIONID", logonNetRespondBean.getJSESSIONID());
		
		// 保用用户登录成功的信息
		GlobalDataCacheForMemorySingleton.getInstance().setLogonNetRespondBean(logonNetRespondBean);
		
		// 保留用户最后一次登录成功时的 用户名
		GlobalDataCacheForMemorySingleton.getInstance().setUsernameForLastSuccessfulLogon(usernameForLastSuccessfulLogon);
		
		// 保留用户最后一次登录成功时的 密码
		GlobalDataCacheForMemorySingleton.getInstance().setPasswordForLastSuccessfulLogon(passwordForLastSuccessfulLogon);
	}
	
	/**
	 * 清空登录相关信息
	 */
	public static synchronized void clearLogonInfo() {
		SimpleCookieSingleton.getInstance().remove("JSESSIONID");
		
		GlobalDataCacheForMemorySingleton.getInstance().setLogonNetRespondBean(null);
		// GlobalDataCacheForMemorySingleton.getInstance().setAccountIndexNetRespondBean(null);
	}
	
	public static synchronized void onKeyDownForFinishApp(final Activity activity, final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(activity).setTitle("提示").setMessage("是否确定退出").setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
				}
			}).show();
		}
	}
	
	/**
	 * 当前App文件在SDCard上, 数据缓存文件夹名称
	 */
	public static final String FiledataCacheFolderNameInSDCard = "airizu";
	
	/**
	 * 返回当前App缓存文件数据的完整路径, 如果有SD卡, 就返回SD卡上的路径, 如果没有, 就返回设备上的files目录
	 * 
	 * @return
	 */
	public static synchronized String getFiledataCachePath() {
		String pathString = "";
		// 判断SDCARD是否存在于手机上，并且可以进行读写访问
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			pathString = Environment.getExternalStorageDirectory().getPath() + "/" + FiledataCacheFolderNameInSDCard;
		} else {
			pathString = MyApplication.getApplication().getApplicationContext().getFilesDir().getPath();
		}
		
		return pathString;
	}
	
	/**
	 * 保存文件数据到缓存文件夹中(SD卡优先, 如果没有SD卡, 就保存到设备存储中)
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static synchronized FileOutputStream openFileOutputByFiledataCachePath(String fileName) throws Exception {
		FileOutputStream fileOutputStream = null;
		// 判断SDCARD是否存在于手机上，并且可以进行读写访问
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String filePathForSDCard = Environment.getExternalStorageDirectory() + "/" + FiledataCacheFolderNameInSDCard;
			File file = new File(filePathForSDCard, fileName);
			fileOutputStream = new FileOutputStream(file);
		} else {
			fileOutputStream = MyApplication.getApplication().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
		}
		
		return fileOutputStream;
	}
	
	/**
	 * 从缓存目录中读取文件数据 (这里已经考虑了SD卡设备存储的双支持)
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static synchronized FileInputStream openFileInputByFiledataCachePath(String fileName) throws Exception {
		FileInputStream fileInputStream = null;
		
		do {
			if (!fileIsExist(fileName)) {
				break;
			}
			String fileFullPath = MyApplication.getApplication().getApplicationContext().getFilesDir().getPath() + "/" + fileName;
			if (FileTools.fileIsExist(fileFullPath)) {
				fileInputStream = MyApplication.getApplication().getApplicationContext().openFileInput(fileName);
				break;
			}
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				fileFullPath = Environment.getExternalStorageDirectory() + "/" + FiledataCacheFolderNameInSDCard + "/" + fileName;
				if (FileTools.fileIsExist(fileFullPath)) {
					File file = new File(fileFullPath);
					fileInputStream = new FileInputStream(file);
					break;
				}
			}
		} while (false);
		
		return fileInputStream;
	}
	
	public static synchronized void createFiledataCacheFolderOnSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File fileFoler = new File(Environment.getExternalStorageDirectory() + "/" + FiledataCacheFolderNameInSDCard);
			if (!fileFoler.exists()) {
				fileFoler.mkdir();
			}
		}
	}
	
	/**
	 * 这里已经考虑了SD卡设备存储的双支持
	 * 
	 * @param fileName
	 * @return
	 */
	public static synchronized boolean fileIsExist(String fileName) {
		
		do {
			String fileFullPath = MyApplication.getApplication().getApplicationContext().getFilesDir().getPath() + "/" + fileName;
			if (FileTools.fileIsExist(fileFullPath)) {
				break;
			}
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				fileFullPath = Environment.getExternalStorageDirectory() + "/" + FiledataCacheFolderNameInSDCard + "/" + fileName;
				if (FileTools.fileIsExist(fileFullPath)) {
					break;
				}
			}
			
			return false;
		} while (false);
		
		return true;
	}
	
	public static synchronized String getDateStringWithYearMonthDayFormat(String dateString) {
		if (TextUtils.isEmpty(dateString)) {
			return "";
		}
		
		String dateStringWithYearMonthDayFormat = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.util.Date dateForUtil = format.parse(dateString);
			java.sql.Date dateForSql = new java.sql.Date(dateForUtil.getTime());
			dateStringWithYearMonthDayFormat = dateForSql.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateStringWithYearMonthDayFormat;
	}
	
	public static enum DateCompareEnum {
		// 左边日期大于右边
		LEFT_IS_GREATER_THAN_RIGHT,
		// 右边日期大于左边
		RIGHT_IS_GREATER_THAN_LEFT,
		// 日期相同
		SAME_DATE
	}
	
	public static synchronized DateCompareEnum dateCompare(String leftDateString, String rightDateString) {
		if (TextUtils.isEmpty(leftDateString) || TextUtils.isEmpty(rightDateString)) {
			return DateCompareEnum.SAME_DATE;
		}
		if (leftDateString.equals(rightDateString)) {
			return DateCompareEnum.SAME_DATE;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date leftDate = null;
		Date rightDate = null;
		try {
			leftDate = format.parse(leftDateString);
			rightDate = format.parse(rightDateString);
		} catch (Exception e) {
			return DateCompareEnum.SAME_DATE;
		}
		Calendar leftCalendar = Calendar.getInstance();
		Calendar rightCalendar = Calendar.getInstance();
		leftCalendar.setTime(leftDate);
		rightCalendar.setTime(rightDate);
		
		int i = leftCalendar.compareTo(rightCalendar);
		if (i == 0) {
			return DateCompareEnum.SAME_DATE;
		} else if (i < 0) {
			return DateCompareEnum.RIGHT_IS_GREATER_THAN_LEFT;
		} else {
			return DateCompareEnum.LEFT_IS_GREATER_THAN_RIGHT;
		}
	}
	
	public static synchronized String getFormattedPriceString(float priceFloat) {
		int priceInt = Double.valueOf(priceFloat).intValue();
		return new String(GlobalConstant.MONEY_MARK_STRING + priceInt);
	}
	
	public static synchronized String getFormattedPriceString(String priceString) {
		int priceInt = 0;
		try {
			priceInt = Float.valueOf(priceString).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new String(GlobalConstant.MONEY_MARK_STRING + priceInt);
	}
	
	public static synchronized String getTodayDateString() {
		java.util.Date utilDate = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return sqlDate.toString();
	}
	
	public static String getAfterDayBySpecifiedDay(String specifiedDay) {
		if (TextUtils.isEmpty(specifiedDay)) {
			DebugLog.e(TAG, "specifiedDay is empty ! ");
			return "";
		}
		
		try {
			Calendar calendar = Calendar.getInstance();
			java.util.Date utilDate = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
			calendar.setTime(utilDate);
			int day = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, day + 1);
			
			String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
			return dayAfter;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static synchronized String getApplicationVersion() {
		String version = "";
		
		InputStream inputStream = null;
		
		try {
			do {
				inputStream = MyApplication.getApplication().getResources().openRawResource(R.raw.build_revision);
				if (inputStream == null) {
					break;
				}
				final byte[] data = StreamTools.readInputStream(inputStream);
				if (data == null || data.length <= 0) {
					break;
				}
				version = new String(data, "utf-8");
			} while (false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				inputStream = null;
			}
		}
		
		return version;
	}
	
	public static synchronized void stopServiceWithThisApp() {
		Intent intent = new Intent(MyApplication.getApplication(), PreLoadedDataService.class);
		MyApplication.getApplication().stopService(intent);
	}
	
	/**
	 * 为一级界面Activity准备的, 直接跳转到 "推荐首页", 非一级界面不要使用
	 * 
	 * @param context
	 */
	public static synchronized void showHomePageForFirstLevelActivity(final Context context) {
		final Intent intent = new Intent();
		intent.setAction(GlobalConstant.UserActionEnum.GOTO_HOME_PAGE.name());
		context.sendBroadcast(intent);
	}
}
