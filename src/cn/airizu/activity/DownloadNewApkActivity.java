package cn.airizu.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.protocol.account_version.VersionNetRespondBean;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.INetFileRespondCallback;
import cn.airizu.net_file.NetFileHelper;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

public class DownloadNewApkActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	private ProgressBar loadingProgressBar;
	private TextView loadingProgressTextView;
	
	private static final int PROGRESS_BAR_MAX_VALUE = 100;
	
	private static enum NetRequestTagEnum {
		DOWNLOAD_NEW_APK
	};
	
	private int netFileDownloadIndex = NetFileHelper.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_new_apk_activity);
		
		final VersionNetRespondBean versionNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getVersionNetRespondBean();
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("新版本" + versionNetRespondBean.getNewVersion());
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading_ProgressBar);
		loadingProgressBar.setMax(PROGRESS_BAR_MAX_VALUE);
		loadingProgressTextView = (TextView) findViewById(R.id.loading_progress_TextView);
		loadingProgressTextView.setText("0%");
		
		netFileDownloadIndex = NetFileHelper.getInstance().downloadFile(NetRequestTagEnum.DOWNLOAD_NEW_APK, versionNetRespondBean.getDownloadURL(),
				ToolsFunctionForThisProgect.getFiledataCachePath(), "airizu.apk", downloadProgressCallback, downloadFileRespondCallback);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopFileDownloadAndBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void stopFileDownloadAndBack() {
		if (netFileDownloadIndex != NetFileHelper.IDLE_NETWORK_REQUEST_ID) {
			NetFileHelper.getInstance().cancelNetFileRequestByRequestIndex(netFileDownloadIndex);
			netFileDownloadIndex = NetFileHelper.IDLE_NETWORK_REQUEST_ID;
		}
		setResult(RESULT_CANCELED);
		finish();
	}
	
	final INetFileProgressCallback downloadProgressCallback = new INetFileProgressCallback() {
		
		@Override
		public void netFileProgressCallbackInNonUIThread(long totalSize, long completedSize) {
			Message message = Message.obtain(myHandler);
			message.what = HandlerMsgTypeEnum.LOADING_UI_PROGRESSING_UPDATE.ordinal();
			message.getData().putLong(HandlerExtraDataTypeEnum.PROGRESS.name(), completedSize);
			message.getData().putLong(HandlerExtraDataTypeEnum.TOTAL.name(), totalSize);
			myHandler.sendMessage(message);
		}
	};
	
	private String newApkFullFilePathName;
	final INetFileRespondCallback downloadFileRespondCallback = new INetFileRespondCallback() {
		
		@Override
		public void netFileRespondCallback(final Enum<?> requestEvent, final String fullFilePathName, final NetErrorTypeEnum errorType, final String errorMessage) {
			
			if (errorType != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				Toast.makeText(DownloadNewApkActivity.this, errorMessage, 0).show();
				return;
			}
			
			DebugLog.e(TAG, "文件-> " + fullFilePathName + " <--下载完成");
			newApkFullFilePathName = fullFilePathName;
			showDialog(DialogIdEnum.APP_UPGRADE.ordinal());
		}
	};
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				stopFileDownloadAndBack();
			}
		}
	};
	
	private static enum HandlerMsgTypeEnum {
		// "加载进度条" UI进度更新事件
		LOADING_UI_PROGRESSING_UPDATE
	};
	
	private static enum HandlerExtraDataTypeEnum {
		// 已完成的进度
		PROGRESS,
		// 总进度
		TOTAL
	};
	
	private Handler myHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == HandlerMsgTypeEnum.LOADING_UI_PROGRESSING_UPDATE.ordinal()) {
				
				final long total = msg.getData().getLong(HandlerExtraDataTypeEnum.TOTAL.name());
				final long progress = msg.getData().getLong(HandlerExtraDataTypeEnum.PROGRESS.name());
				
				DebugLog.e(TAG, "total=" + total + ", progress=" + progress);
				int i = (int) (progress * 100 / total);
				loadingProgressBar.setMax(100);
				loadingProgressBar.setProgress(i);
				loadingProgressTextView.setText("" + i + "%");
				
				if (progress >= total) {
					DebugLog.e(TAG, "新版本下载完成!");
				}
			}
		}
	};
	
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
				stopFileDownloadAndBack();
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
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(new File(newApkFullFilePathName)), "application/vnd.android.package-archive");
					startActivity(intent);
					
					setResult(RESULT_OK);
					finish();
				}
				break;
				
				// "取消"
				case DialogInterface.BUTTON_NEGATIVE: {
					stopFileDownloadAndBack();
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
			
		}
	};
	
	private Dialog buildAppUpgradeDialog() {
		
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("下载完成");
		alertDialogBuilder.setMessage("软件已经下载完毕, 是否要进行更新?");
		alertDialogBuilder.setPositiveButton("确定", dialogOnClickListener);
		alertDialogBuilder.setNegativeButton("取消", dialogOnClickListener);
		alertDialogBuilder.setOnCancelListener(dialogOnCancelListener);
		alertDialogBuilder.setOnKeyListener(dialogOnKeyListener);
		
		return alertDialogBuilder.create();
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
}
