package cn.airizu.activity.help;

import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_help.HelpInfo;
import cn.airizu.domain.protocol.account_help.HelpNetRequestBean;
import cn.airizu.domain.protocol.account_help.HelpNetRespondBean;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import android.app.ActivityGroup;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class HelpActivity extends ActivityGroup {
	private final String TAG = this.getClass().getSimpleName();
	
	private int netRequestIndexForHelp = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private enum NetRequestTagEnum {
		//
		HELP
	};
	
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		final HelpNetRespondBean helpNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getHelpNetRespondBean();
		if (helpNetRespondBean != null) {
			loadRealUIAndUseNetRespondBeanInitialize(helpNetRespondBean);
		} else {
			loadPreLoadedUIAndInitialize();
			requestHelp();
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
		
		SimpleProgressDialog.resetByThisContext(this);
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
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		netRequestIndexForHelp = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("帮助");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private TabHost.TabContentFactory tabContentFactory = new TabHost.TabContentFactory() {
		
		@Override
		public View createTabContent(String tag) {
			
			final LayoutInflater inflaterInstance = LayoutInflater.from(HelpActivity.this);
			final View tabcontentView = inflaterInstance.inflate(R.layout.help_tabhost_tabcontent, null);
			final HelpNetRespondBean helpNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getHelpNetRespondBean();
			for (HelpInfo helpInfo : helpNetRespondBean.getHelpInfoList()) {
				if (tag.endsWith(helpInfo.getType())) {
					((TextView) tabcontentView.findViewById(R.id.help_title_TextView)).setText(helpInfo.getTitle());
					final WebView webView = (WebView) tabcontentView.findViewById(R.id.help_text_WebView);
					webView.loadUrl("http://www.airizu.com/howitworks.html");
					break;
				}
			}
			return tabcontentView;
		}
	};
	
	private void loadRealUIAndUseNetRespondBeanInitialize(HelpNetRespondBean helpNetRespondBean) {
		if (helpNetRespondBean == null) {
			return;
		}
		
		// 加载真正的UI界面
		setContentView(R.layout.help_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		String titleBarTitleString = "帮助";
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(getLocalActivityManager());
		
		final LayoutInflater inflaterInstance = LayoutInflater.from(HelpActivity.this);
		for (HelpInfo helpInfo : helpNetRespondBean.getHelpInfoList()) {
			final String tabTagString = helpInfo.getType();
			final String tabTitleString = helpInfo.getType();
			final View tabspecView = inflaterInstance.inflate(R.layout.help_tabhost_tabspec, null);
			((TextView) tabspecView.findViewById(R.id.title_TextView)).setText(tabTitleString);
			tabHost.addTab(tabHost.newTabSpec(tabTagString).setIndicator(tabspecView).setContent(tabContentFactory));
		}
	}
	
	private void loadNetErrorProcessUIAndInitialize(final String netErrorMessageString, final Enum<?> netRequestTagEnum) {
		
		if (netRequestTagEnum == NetRequestTagEnum.HELP) {
			// 如果真正的界面UI还未加载, 就还是使用 "预加载UI" 来显示网络错误信息
			final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
			if (infoLabelForPreLoadedUiTextView != null || !TextUtils.isEmpty(netErrorMessageString)) {
				infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
				infoLabelForPreLoadedUiTextView.setText(netErrorMessageString);
			}
			final Button functionButtonForPreloadedUiButton = (Button) findViewById(R.id.function_button_for_preloaded_ui_Button);
			if (functionButtonForPreloadedUiButton != null) {
				functionButtonForPreloadedUiButton.setVisibility(View.VISIBLE);
				functionButtonForPreloadedUiButton.setText("刷新");
				functionButtonForPreloadedUiButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						requestHelp();
					}
				});
			}
		}
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	private void requestHelp() {
		final HelpNetRequestBean helpNetRequestBean = new HelpNetRequestBean();
		netRequestIndexForHelp = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, helpNetRequestBean, NetRequestTagEnum.HELP, domainNetRespondCallback);
		if (netRequestIndexForHelp != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(HelpActivity.this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			//
			SimpleProgressDialog.dismiss(HelpActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				DebugLog.i(TAG, "SHOW_NET_ERROR_MESSAGE");
				
				final String netErrorMessageString = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessageString, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				
				DebugLog.i(TAG, "REFRESH_UI");
				final HelpNetRespondBean helpNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getHelpNetRespondBean();
				loadRealUIAndUseNetRespondBeanInitialize(helpNetRespondBean);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		netRequestIndexForHelp = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	private IDomainNetRespondCallback domainNetRespondCallback = new IDomainNetRespondCallback() {
		
		@Override
		public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean) {
			DebugLog.i(TAG, "domainNetRespondHandleInNonUIThread --- start ! ");
			clearNetRequestIndexByRequestEvent(requestEvent);
			
			if (errorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal();
				msg.getData().putString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name(), errorBean.getErrorMessage());
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.HELP) {// 2.16 帮助
			
				final HelpNetRespondBean helpNetRespondBean = (HelpNetRespondBean) respondDomainBean;
				GlobalDataCacheForMemorySingleton.getInstance.setHelpNetRespondBean(helpNetRespondBean);
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
}
