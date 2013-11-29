package cn.airizu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.airizu.activity.main_navigation.MainNavigationActivity;
import cn.airizu.domain.protocol.account_version.VersionNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

public class AirizuFirstActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private static enum IntentRequestCodeEnum {
		TO_DOWNLOAD_NEW_APK_ACTIVITY
	};
	
	private ProgressBar loadingProgressBar;
	private TextView loadingProgressTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_activity);
		
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading_ProgressBar);
		loadingProgressBar.setMax(PROGRESS_BAR_MAX_VALUE);
		loadingProgressTextView = (TextView) findViewById(R.id.loading_progress_TextView);
		loadingProgressTextView.setText("" + loadingProgress + "%");
		
		if (!loadingUiThread.isAlive()) {
			loadingUiThread.start();
		}
		if (!loadCacheDataThread.isAlive()) {
			loadCacheDataThread.start();
		}
	}
	
	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		DebugLog.i(TAG, "onRestart");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult --> start ! ");
		
		if (requestCode == IntentRequestCodeEnum.TO_DOWNLOAD_NEW_APK_ACTIVITY.ordinal()) {
			if (resultCode == Activity.RESULT_OK) {
				
				ToolsFunctionForThisProgect.stopServiceWithThisApp();
				
				// 要开始安装新的APK
				finish();
				
			} else {
				gotoMainActivity();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (loadingDataLoadEnd) {
				// 关键数据加载完成时, 可以允许用户退出, 一定要先终止UI线程
				loadingUiThread.interrupt();
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 跳转到MainActivity, 并且关闭当前Activity
	 */
	private synchronized void gotoNextActivity() {
		
		if (loadingUIShwoEnd && loadingDataLoadEnd) {
			
			if (GlobalDataCacheForMemorySingleton.getInstance.getNewVersionDetectState() == GlobalConstant.NewVersionDetectStateEnum.HAS_NEW_VERSION) {
				
				// 提示用户有软件新版本可以下载
				this.showDialog(DialogIdEnum.APP_UPGRADE.ordinal());
			} else {
				gotoMainActivity();
			}
		}
	}
	
	private synchronized void gotoMainActivity() {
		Intent intent = new Intent(AirizuFirstActivity.this, MainNavigationActivity.class);
		startActivity(intent);
		finish();
	}
	
	private synchronized void gotoNewApkDownloadActivity() {
		Intent intent = new Intent(AirizuFirstActivity.this, DownloadNewApkActivity.class);
		startActivityForResult(intent, IntentRequestCodeEnum.TO_DOWNLOAD_NEW_APK_ACTIVITY.ordinal());
	}
	
	private static enum HandlerMsgTypeEnum {
		// "加载进度条" UI进度更新事件
		LOADING_UI_PROGRESSING_UPDATE,
		// 加载缓存的数据完成事件
		LOADING_CACHE_DATA_FINISHED
	};
	
	private static enum HandlerExtraDataTypeEnum {
		// 已完成的进度
		PROGRESS,
		// 总进度
		TOTAL
	};
	
	private boolean loadingUIShwoEnd = false;
	private boolean loadingDataLoadEnd = false;
	private Handler myHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == HandlerMsgTypeEnum.LOADING_UI_PROGRESSING_UPDATE.ordinal()) {
				
				final int total = msg.getData().getInt(HandlerExtraDataTypeEnum.TOTAL.name());
				final int progress = msg.getData().getInt(HandlerExtraDataTypeEnum.PROGRESS.name());
				
				if (progress >= total) {
					DebugLog.i(TAG, "数据加载进度条显示完成!");
					loadingUIShwoEnd = true;
					gotoNextActivity();
				} else {
					loadingProgressBar.setMax(total);
					loadingProgressBar.setProgress(progress);
					loadingProgressTextView.setText("" + progress + "%");
				}
				
			} else if (msg.what == HandlerMsgTypeEnum.LOADING_CACHE_DATA_FINISHED.ordinal()) {
				
				DebugLog.i(TAG, "本地缓存的数据加载完成!");
				loadingDataLoadEnd = true;
				gotoNextActivity();
			}
		}
	};
	
	private static final int PROGRESS_BAR_MAX_VALUE = 100;
	private static int loadingProgress = 0;
	private Thread loadingUiThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			DebugLog.i(TAG, "loadingUIShwoStart");
			
			while (!Thread.currentThread().isInterrupted()) {
				if (loadingProgress > PROGRESS_BAR_MAX_VALUE) {
					loadingProgress = 0;
					break;
				}
				
				Message message = new Message();
				message.what = HandlerMsgTypeEnum.LOADING_UI_PROGRESSING_UPDATE.ordinal();
				message.getData().putInt(HandlerExtraDataTypeEnum.PROGRESS.name(), loadingProgress++);
				message.getData().putInt(HandlerExtraDataTypeEnum.TOTAL.name(), PROGRESS_BAR_MAX_VALUE);
				myHandler.sendMessage(message);
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
			DebugLog.i(TAG, "loadingUIShwoEnd");
		}
	});
	
	private Thread loadCacheDataThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			DebugLog.i(TAG, "loadingDataLoadStart");
			
			// 在SD卡上创建应用程序的文件缓存文件夹
			ToolsFunctionForThisProgect.createFiledataCacheFolderOnSDCard();
			
			// 这里加载的是最重要的信息
			// 推荐城市列表
			GlobalDataCacheForNeedSaveToFileSystem.readRecommendNetRespondBean();
			
			// 本地缓存的数据加载完成
			Message message = new Message();
			message.what = HandlerMsgTypeEnum.LOADING_CACHE_DATA_FINISHED.ordinal();
			myHandler.sendMessage(message);
			
			DebugLog.i(TAG, "loadingDataLoadEnd");
		}
	});
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 提示对话框 "有软件新版本需要下载!"
	private static enum DialogIdEnum {
		//
		APP_UPGRADE
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		DebugLog.i(TAG, "onCreateDialog");
		
		if (id == DialogIdEnum.APP_UPGRADE.ordinal()) {
			return buildAppUpgradeDialog();
		} else {
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		DebugLog.i(TAG, "onPrepareDialog");
		// dialog已经创建完成, 在显示之前会调用这里
		super.onPrepareDialog(DialogIdEnum.APP_UPGRADE.ordinal(), dialog);
	}
	
	private DialogInterface.OnKeyListener dialogOnKeyListener = new DialogInterface.OnKeyListener() {
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			DebugLog.i(TAG, "DialogInterface.OnKeyListener");
			
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				gotoMainActivity();
				//
				return true;
			}
			
			return false;
		}
	};
	private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DebugLog.i(TAG, "DialogInterface.OnClickListener");
			
			switch (which)
			{
				// "确定"
				case DialogInterface.BUTTON_POSITIVE: {
					dismissDialog(DialogIdEnum.APP_UPGRADE.ordinal());
					gotoNewApkDownloadActivity();
				}
				break;
				
				// "取消"
				case DialogInterface.BUTTON_NEGATIVE: {
					gotoMainActivity();
				}
				break;
				default:
				break;
			}
		}
	};
	
	private DialogInterface.OnCancelListener dialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			DebugLog.i(TAG, "DialogInterface.OnCancelListener");
			gotoMainActivity();
		}
	};
	
	private Dialog buildAppUpgradeDialog() {
		
		final VersionNetRespondBean versionNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getVersionNetRespondBean();
		
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("有软件新版本可以下载!");
		alertDialogBuilder.setMessage(versionNetRespondBean.getUpdateContent() + "软件大小 : " + versionNetRespondBean.getFileSize());
		alertDialogBuilder.setPositiveButton("确定", dialogOnClickListener);
		alertDialogBuilder.setNegativeButton("取消", dialogOnClickListener);
		alertDialogBuilder.setOnCancelListener(dialogOnCancelListener);
		alertDialogBuilder.setOnKeyListener(dialogOnKeyListener);
		
		return alertDialogBuilder.create();
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
}
