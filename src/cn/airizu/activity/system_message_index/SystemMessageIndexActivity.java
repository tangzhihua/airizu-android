package cn.airizu.activity.system_message_index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.activity.system_message_detail.SystemMessageDetailActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.SystemMessageListAdapter;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_system_message.SystemMessage;
import cn.airizu.domain.protocol.account_system_message.SystemMessagesNetRequestBean;
import cn.airizu.domain.protocol.account_system_message.SystemMessagesNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

public class SystemMessageIndexActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private boolean isRealUI = false;
	private boolean isLastLine = false;
	
	private enum NetRequestTagEnum {
		// 2.18 获得系统通知
		SYSTEM_MESSAGE
	};
	
	private int netRequestIndexForSystemMessage = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	// page 从 1 开始
	private int pageNumberForSystemMessageList = 1;
	
	private SystemMessageListAdapter systemMessageListAdapter;
	private List<Map<String, String>> dataSourceForSystemMessageListView = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> newDataForSystemMessage;
	private List<SystemMessage> systemMessageBeanList = new ArrayList<SystemMessage>();
	
	private TextView listStateHintTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		
		requestSystemMessageByPage(pageNumberForSystemMessageList);
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
	}
	
	private void loadPreLoadedUIAndInitialize() {
		// 加载 "预加载UI"
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("系统消息");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText(R.string.data_loading_hint);
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void loadNetErrorProcessUIAndInitialize(final String netErrorMessageString, final NetRequestTagEnum netRequestTagEnum) {
		
		if (isRealUI) {
			// 如果真正的界面UI已经加载了, 那么就使用这个TextView来提示网络加载错误信息
			listStateHintTextView.setVisibility(View.VISIBLE);
			listStateHintTextView.setText(netErrorMessageString);
		} else {
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
						requestSystemMessageByPage(pageNumberForSystemMessageList);
					}
				});
			}
		}
	}
	
	private void loadRealUI() {
		// 加载当前Activity真正的UI界面
		isRealUI = true;
		setContentView(R.layout.system_message_index_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		String titleBarTitleString = "系统消息";
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		listStateHintTextView = (TextView) findViewById(R.id.list_state_hint_TextView);
		listStateHintTextView.setVisibility(View.GONE);
		
		final ListView systemMessageListView = (ListView) findViewById(R.id.system_message_ListView);
		systemMessageListAdapter = new SystemMessageListAdapter(this, dataSourceForSystemMessageListView);
		systemMessageListView.setAdapter(systemMessageListAdapter);
		systemMessageListView.setOnItemClickListener(systemMessageListViewItemClickListener);
		systemMessageListView.setOnScrollListener(systemMessageListViewOnScrollListener);
	}
	
	private void gotoSystemMessageDetailActivite(SystemMessage systemMessage) {
		if (systemMessage != null) {
			Intent intent = new Intent(SystemMessageIndexActivity.this, SystemMessageDetailActivity.class);
			intent.putExtra(SystemMessageDetailActivity.IntentExtraTagEnum.SYSTEM_MESSAGE_BEAN.name(), systemMessage);
			startActivity(intent);
		}
	}
	
	private OnItemClickListener systemMessageListViewItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			gotoSystemMessageDetailActivite(systemMessageBeanList.get(position));
		}
	};
	
	// 这个方法是为列表添加滚动监听
	private OnScrollListener systemMessageListViewOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState)
			{
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE: {
					// 判断滚动到底部
					do {
						final int listViewItemTotal = view.getCount();
						if (listViewItemTotal <= 0) {
							// listview 为空
							break;
						}
						if (netRequestIndexForSystemMessage != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
							SimpleProgressDialog.show(SystemMessageIndexActivity.this, progressDialogOnCancelListener);
							break;
						}
						final int listViewLastVisiblePosition = view.getLastVisiblePosition();
						if (listViewLastVisiblePosition != (listViewItemTotal - 1)) {
							// 如果当前listview没有滚到最后一条, 就要隐藏 listStateHintTextView
							listStateHintTextView.setVisibility(View.GONE);
							break;
						}
						
						DebugLog.i(TAG, "OnScrollListener->LastScroll");
						
						// listview 已经滚到了最后了一行
						if (isLastLine) {
							listStateHintTextView.setVisibility(View.VISIBLE);
							listStateHintTextView.setText(R.string.last_line_hint);
						} else {
							requestSystemMessageByPage(pageNumberForSystemMessageList);
						}
					} while (false);
				}
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}
	};
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	private void requestSystemMessageByPage(final int pageNum) {
		if (pageNum < 0) {
			return;
		}
		
		final SystemMessagesNetRequestBean systemMessagesNetRequestBean = new SystemMessagesNetRequestBean(pageNum);
		netRequestIndexForSystemMessage = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, systemMessagesNetRequestBean, NetRequestTagEnum.SYSTEM_MESSAGE,
				domainNetRespondCallback);
		if (netRequestIndexForSystemMessage != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI_FOR_SYSTEM_MESSAGE_LIST
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(SystemMessageIndexActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessage, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI_FOR_SYSTEM_MESSAGE_LIST.ordinal()) {
				
				// 网络请求的有效数据成功返回, 如果还未加载真正的界面UI, 就先加载real ui, 否则只要刷新 listview就可以了
				dataSourceForSystemMessageListView.addAll(newDataForSystemMessage);
				newDataForSystemMessage = null;
				
				if (!isRealUI) {
					loadRealUI();
				}
				if (isLastLine) {
					listStateHintTextView.setVisibility(View.VISIBLE);
					int resIdForlistStateHintTextView;
					if (systemMessageBeanList.size() <= 0) {
						// 当前用户没有任何 "系统消息"
						final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						layoutParams.alignWithParent = true;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						listStateHintTextView.setLayoutParams(layoutParams);
						resIdForlistStateHintTextView = R.string.no_valid_data_hint;
					} else {
						resIdForlistStateHintTextView = R.string.last_line_hint;
					}
					listStateHintTextView.setText(resIdForlistStateHintTextView);
				} else {
					systemMessageListAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.SYSTEM_MESSAGE == requestEvent) {
			netRequestIndexForSystemMessage = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
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
			
			if (requestEvent == NetRequestTagEnum.SYSTEM_MESSAGE) {// 2.18 获得系统通知
			
				SystemMessagesNetRespondBean systemMessagesNetRespondBean = (SystemMessagesNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, systemMessagesNetRespondBean.toString());
				
				// 先保存新数据业务Bean
				systemMessageBeanList.addAll(systemMessagesNetRespondBean.getSystemMessageList());
				newDataForSystemMessage = parseSystemMessageListToListViewAdapterDataSource(systemMessagesNetRespondBean.getSystemMessageList());
				if (newDataForSystemMessage.size() > 0) {
					// 只有当网络请求回有效数据时, 才要累加 page 计数器
					pageNumberForSystemMessageList++;
				} else {
					// 后台已经没有有效的数据了
					// 这是跟后台约定好的协议, 如果到达最后一行数据时, 后台会返回 {"data", []}
					isLastLine = true;
				}
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI_FOR_SYSTEM_MESSAGE_LIST.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private List<Map<String, String>> parseSystemMessageListToListViewAdapterDataSource(List<SystemMessage> systemMessageList) {
		DebugLog.i(TAG, "parseRecommendCityListToListViewAdapterDataSource --> start ! ");
		
		final List<Map<String, String>> dataSource = new ArrayList<Map<String, String>>();
		for (SystemMessage message : systemMessageList) {
			
			final Map<String, String> map = new HashMap<String, String>();
			map.put(SystemMessageListAdapter.DataSourceKeyEnum.MESSAGE.name(), message.getMessage());
			map.put(SystemMessageListAdapter.DataSourceKeyEnum.DATE.name(), ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(message.getDate()));
			
			dataSource.add(map);
		}
		
		return dataSource;
	}
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
}
