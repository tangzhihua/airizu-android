package cn.airizu.activity.system_message_detail;

import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.protocol.account_system_message.SystemMessage;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SystemMessageDetailActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 系统消息bean
		SYSTEM_MESSAGE_BEAN
	};
	
	private SystemMessage systemMessageBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			updateProLoadedUIInfoForLostIntentExtraData();
			return;
		} else {
			loadRealUIAndUseSystemMessageBeanInitialize(systemMessageBean);
		}
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			systemMessageBean = (SystemMessage) intent.getSerializableExtra(IntentExtraTagEnum.SYSTEM_MESSAGE_BEAN.name());
			if (systemMessageBean == null) {
				break;
			}
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("系统消息");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void updateProLoadedUIInfoForLostIntentExtraData() {
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中失败, 请返回上一页.");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void loadRealUIAndUseSystemMessageBeanInitialize(SystemMessage systemMessage) {
		if (systemMessage == null) {
			return;
		}
		
		setContentView(R.layout.system_message_detail_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("系统消息");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 消息日期
		final TextView messageDateTextView = (TextView) findViewById(R.id.message_date_TextView);
		messageDateTextView.setText(ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(systemMessage.getDate()));
		// 消息内容
		final TextView messageTextTextView = (TextView) findViewById(R.id.message_text_TextView);
		messageTextTextView.setText(systemMessage.getMessage());
		
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
}
