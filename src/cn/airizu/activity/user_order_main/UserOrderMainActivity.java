package cn.airizu.activity.user_order_main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.airizu.activity.R;
import cn.airizu.activity.main_navigation.MainNavigationActivity;
import cn.airizu.activity.user_order_detail.UserOrderDetailActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.UserOrderListAdapter;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.order_list.OrderOverview;
import cn.airizu.domain.protocol.order_list.OrderOverviewListNetRequestBean;
import cn.airizu.domain.protocol.order_list.OrderOverviewListNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class UserOrderMainActivity extends TabActivity implements TabHost.TabContentFactory {
	private final String TAG = this.getClass().getSimpleName();
	private final int INTENT_REQUEST_CODE_FOR_ORDER_DETAIL = 0;
	
	public static enum IntentExtraTagMenu {
		// 订单状态(1,待确定，2待支付，3待入住，4待评价5已完成)
		ORDER_STATE
	};
	
	private ListView waitConfirmListView;
	private ListView waitPayListView;
	private ListView waitLiveListView;
	private ListView waitCommentListView;
	private ListView waitHasEndedListView;
	
	private List<Map<String, String>> dataSourceForWaitConfirmListViewAdapter = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> dataSourceForWaitPayListViewAdapter = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> dataSourceForWaitLiveListViewAdapter = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> dataSourceForWaitCommentListViewAdapter = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> dataSourceForWaitHasEndedListViewAdapter = new ArrayList<Map<String, String>>();
	
	private UserOrderListAdapter waitConfirmListViewAdapter;
	private UserOrderListAdapter waitPayListViewAdapter;
	private UserOrderListAdapter waitLiveListViewAdapter;
	private UserOrderListAdapter waitCommentListViewAdapter;
	private UserOrderListAdapter waitHasEndedListViewAdapter;
	
	private String moneyMarkString = "";
	
	private GlobalConstant.OrderStateEnum orderStateEnum = GlobalConstant.OrderStateEnum.WAIT_CONFIRM;
	private int netRequestIndexForOrderList = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.user_order_main_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("我的订单");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			return;
		}
		
		moneyMarkString = getResources().getString(R.string.money_mark);
		
		initAllListViewAndAdapter();
		
		// 初始化 负责主导航的 Tab_Host
		initTabHost();
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
		netRequestIndexForOrderList = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult --> start ! ");
		
		if (resultCode != RESULT_OK) {
			return;
		}
		
		if (requestCode == INTENT_REQUEST_CODE_FOR_ORDER_DETAIL) {
			do {
				GlobalConstant.OrderStateEnum currentTab = GlobalConstant.OrderStateEnum.valueOf(getTabHost().getCurrentTabTag());
				if (!currentTab.equals(GlobalConstant.OrderStateEnum.HAS_ENDED)) {
					clearListViewDatasByOrderStateEnum(currentTab);
					requestOrderListByOrderState(currentTab);
				}
			} while (false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DebugLog.i(TAG, "onKeyDown");
		
		gotoAccountActivity();
		return true;
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				DebugLog.e(TAG, "intent is null ! ");
				break;
			}
			
			orderStateEnum = (GlobalConstant.OrderStateEnum) intent.getSerializableExtra(IntentExtraTagMenu.ORDER_STATE.name());
			return true;
		} while (false);
		
		return false;
	}
	
	private void clearListViewDatasByOrderStateEnum(GlobalConstant.OrderStateEnum orderStateEnum) {
		switch (orderStateEnum)
		{
			case WAIT_CONFIRM: {
				dataSourceForWaitConfirmListViewAdapter.clear();
				waitConfirmListViewAdapter.notifyDataSetChanged();
			}
			break;
			case WAIT_PAY: {
				dataSourceForWaitPayListViewAdapter.clear();
				waitPayListViewAdapter.notifyDataSetChanged();
			}
			break;
			case WAIT_LIVE: {
				dataSourceForWaitLiveListViewAdapter.clear();
				waitLiveListViewAdapter.notifyDataSetChanged();
			}
			break;
			case WAIT_COMMENT: {
				dataSourceForWaitCommentListViewAdapter.clear();
				waitCommentListViewAdapter.notifyDataSetChanged();
			}
			break;
			
			default:
			break;
		}
	}
	
	private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String tabId) {
			
			orderStateEnum = GlobalConstant.OrderStateEnum.valueOf(tabId);
			DomainProtocolNetHelperSingleton.getInstance().cancelNetRequestByRequestIndex(netRequestIndexForOrderList);
			netRequestIndexForOrderList = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
			
			switch (orderStateEnum)
			{
				case WAIT_CONFIRM: {
					if (dataSourceForWaitConfirmListViewAdapter.size() <= 0) {
						requestOrderListByOrderState(orderStateEnum);
					}
				}
				break;
				case WAIT_PAY: {
					if (dataSourceForWaitPayListViewAdapter.size() <= 0) {
						requestOrderListByOrderState(orderStateEnum);
					}
				}
				break;
				case WAIT_LIVE: {
					if (dataSourceForWaitLiveListViewAdapter.size() <= 0) {
						requestOrderListByOrderState(orderStateEnum);
					}
				}
				break;
				case WAIT_COMMENT: {
					if (dataSourceForWaitCommentListViewAdapter.size() <= 0) {
						requestOrderListByOrderState(orderStateEnum);
					}
				}
				break;
				case HAS_ENDED: {
					if (dataSourceForWaitHasEndedListViewAdapter.size() <= 0) {
						requestOrderListByOrderState(orderStateEnum);
					}
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	@Override
	public View createTabContent(String tag) {
		
		GlobalConstant.OrderStateEnum orderStateEnum = GlobalConstant.OrderStateEnum.valueOf(tag);
		switch (orderStateEnum)
		{
			case WAIT_CONFIRM: {
				return waitConfirmListView;
			}
			case WAIT_PAY: {
				return waitPayListView;
			}
			case WAIT_LIVE: {
				return waitLiveListView;
			}
			case WAIT_COMMENT: {
				return waitCommentListView;
			}
			case HAS_ENDED: {
				return waitHasEndedListView;
			}
				
			default: {
				return waitConfirmListView;
			}
		}
		
	}
	
	private AdapterView.OnItemClickListener onItemClickListenerForListView = new AdapterView.OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String orderIdString = "";
			if (parent == waitConfirmListView) {
				orderIdString = dataSourceForWaitConfirmListViewAdapter.get(position).get(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name());
			} else if (parent == waitPayListView) {
				orderIdString = dataSourceForWaitPayListViewAdapter.get(position).get(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name());
			} else if (parent == waitLiveListView) {
				orderIdString = dataSourceForWaitLiveListViewAdapter.get(position).get(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name());
			} else if (parent == waitCommentListView) {
				orderIdString = dataSourceForWaitCommentListViewAdapter.get(position).get(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name());
			} else if (parent == waitHasEndedListView) {
				orderIdString = dataSourceForWaitHasEndedListViewAdapter.get(position).get(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name());
			}
			
			if (!TextUtils.isEmpty(orderIdString)) {
				final Intent intent = new Intent(UserOrderMainActivity.this, UserOrderDetailActivity.class);
				intent.putExtra(UserOrderDetailActivity.IntentExtraTagMenu.ORDER_ID.name(), orderIdString);
				startActivityForResult(intent, INTENT_REQUEST_CODE_FOR_ORDER_DETAIL);
			}
		}
	};
	
	private void initAllListViewAndAdapter() {
		waitConfirmListView = new ListView(this);
		waitConfirmListView.setOnItemClickListener(onItemClickListenerForListView);
		waitConfirmListViewAdapter = new UserOrderListAdapter(this, dataSourceForWaitConfirmListViewAdapter);
		waitConfirmListView.setAdapter(waitConfirmListViewAdapter);
		
		waitPayListView = new ListView(this);
		waitPayListView.setOnItemClickListener(onItemClickListenerForListView);
		waitPayListViewAdapter = new UserOrderListAdapter(this, dataSourceForWaitPayListViewAdapter);
		waitPayListView.setAdapter(waitPayListViewAdapter);
		
		waitLiveListView = new ListView(this);
		waitLiveListView.setOnItemClickListener(onItemClickListenerForListView);
		waitLiveListViewAdapter = new UserOrderListAdapter(this, dataSourceForWaitLiveListViewAdapter);
		waitLiveListView.setAdapter(waitLiveListViewAdapter);
		
		waitCommentListView = new ListView(this);
		waitCommentListView.setOnItemClickListener(onItemClickListenerForListView);
		waitCommentListViewAdapter = new UserOrderListAdapter(this, dataSourceForWaitCommentListViewAdapter);
		waitCommentListView.setAdapter(waitCommentListViewAdapter);
		
		waitHasEndedListView = new ListView(this);
		waitHasEndedListView.setOnItemClickListener(onItemClickListenerForListView);
		waitHasEndedListViewAdapter = new UserOrderListAdapter(this, dataSourceForWaitHasEndedListViewAdapter);
		waitHasEndedListView.setAdapter(waitHasEndedListViewAdapter);
	}
	
	private void initTabHost() {
		
		final LayoutInflater inflaterInstance = LayoutInflater.from(this);
		
		final TabHost tabHost = getTabHost();
		
		final String tabTag1 = GlobalConstant.OrderStateEnum.WAIT_CONFIRM.name();
		final String tabTitle1 = getResources().getString(R.string.wait_confirm);
		final View tabSpecView1 = inflaterInstance.inflate(R.layout.user_order_main_activity_tabwidget_left_edge, null);
		((TextView) tabSpecView1.findViewById(R.id.title_TextView)).setText(tabTitle1);
		tabHost.addTab(tabHost.newTabSpec(tabTag1).setIndicator(tabSpecView1).setContent(this));
		
		final String tabTag2 = GlobalConstant.OrderStateEnum.WAIT_PAY.name();
		final String tabTitle2 = getResources().getString(R.string.wait_pay);
		final View tabSpecView2 = inflaterInstance.inflate(R.layout.user_order_main_activity_tabwidget_middle, null);
		((TextView) tabSpecView2.findViewById(R.id.title_TextView)).setText(tabTitle2);
		tabHost.addTab(tabHost.newTabSpec(tabTag2).setIndicator(tabSpecView2).setContent(this));
		
		final String tabTag3 = GlobalConstant.OrderStateEnum.WAIT_LIVE.name();
		final String tabTitle3 = getResources().getString(R.string.wait_live);
		final View tabSpecView3 = inflaterInstance.inflate(R.layout.user_order_main_activity_tabwidget_middle, null);
		((TextView) tabSpecView3.findViewById(R.id.title_TextView)).setText(tabTitle3);
		tabHost.addTab(tabHost.newTabSpec(tabTag3).setIndicator(tabSpecView3).setContent(this));
		
		final String tabTag4 = GlobalConstant.OrderStateEnum.WAIT_COMMENT.name();
		final String tabTitle4 = getResources().getString(R.string.wait_comment);
		final View tabSpecView4 = inflaterInstance.inflate(R.layout.user_order_main_activity_tabwidget_middle, null);
		((TextView) tabSpecView4.findViewById(R.id.title_TextView)).setText(tabTitle4);
		tabHost.addTab(tabHost.newTabSpec(tabTag4).setIndicator(tabSpecView4).setContent(this));
		
		final String tabTag5 = GlobalConstant.OrderStateEnum.HAS_ENDED.name();
		final String tabTitle5 = getResources().getString(R.string.has_ended);
		final View tabSpecView5 = inflaterInstance.inflate(R.layout.user_order_main_activity_tabwidget_right_edge, null);
		((TextView) tabSpecView5.findViewById(R.id.title_TextView)).setText(tabTitle5);
		tabHost.addTab(tabHost.newTabSpec(tabTag5).setIndicator(tabSpecView5).setContent(this));
		
		// 监听一定要放到TabHost初始化完成后, 否则在初始化的时候就会触发tabChangeListener
		tabHost.setOnTabChangedListener(tabChangeListener);
		tabHost.setCurrentTabByTag(orderStateEnum.name());
		if (orderStateEnum.ordinal() == 0) {
			// 发现, 如果tab索引为0的话, 当调用 setCurrentTabByTag()时, 是不会触发tabChangeListener监听的
			tabChangeListener.onTabChanged(orderStateEnum.name());
		}
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				gotoAccountActivity();
			}
		}
	};
	
	private void requestOrderListByOrderState(GlobalConstant.OrderStateEnum orderStateEnum) {
		if (orderStateEnum == null) {
			return;
		}
		
		final OrderOverviewListNetRequestBean orderOverviewListNetRequestBean = new OrderOverviewListNetRequestBean(Integer.toString(orderStateEnum.getValue()));
		netRequestIndexForOrderList = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, orderOverviewListNetRequestBean, orderStateEnum, domainNetRespondCallback);
		if (netRequestIndexForOrderList != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private String getOrderStateDescriptionByOrderStatusCode(String statusCode) {
		String orderStateDescription = "";
		do {
			if (TextUtils.isEmpty(statusCode)) {
				// break;
			}
			
			switch (orderStateEnum)
			{
				case WAIT_CONFIRM: {
					orderStateDescription = getResources().getString(R.string.wait_confirm);
				}
				break;
				case WAIT_PAY: {
					orderStateDescription = getResources().getString(R.string.wait_pay);
				}
				break;
				case WAIT_LIVE: {
					orderStateDescription = getResources().getString(R.string.wait_live);
				}
				break;
				case WAIT_COMMENT: {
					orderStateDescription = getResources().getString(R.string.wait_comment);
				}
				break;
				case HAS_ENDED: {
					orderStateDescription = getResources().getString(R.string.has_ended);
				}
				break;
				
				default: {
				}
			}
		} while (false);
		
		return orderStateDescription;
	}
	
	private List<Map<String, String>> parseUserOrderListToListViewAdapterDataSource(List<OrderOverview> orderOverviewList) {
		DebugLog.i(TAG, "parseUserOrderListToListViewAdapterDataSource --> start ! ");
		
		if (orderOverviewList == null || orderOverviewList.size() <= 0) {
			return null;
		}
		
		final List<Map<String, String>> dataSource = new ArrayList<Map<String, String>>();
		for (OrderOverview orderOverview : orderOverviewList) {
			final Map<String, String> map = new HashMap<String, String>();
			map.put(UserOrderListAdapter.DataSourceKeyEnum.ROOM_PHOTO_URL.name(), orderOverview.getRoomImage());
			map.put(UserOrderListAdapter.DataSourceKeyEnum.ROOM_TITLE.name(), orderOverview.getRoomTitle());
			map.put(UserOrderListAdapter.DataSourceKeyEnum.CHECKIN_DATE.name(), ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(orderOverview.getCheckInDate()));
			map.put(UserOrderListAdapter.DataSourceKeyEnum.CHECKOUT_DATE.name(), ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(orderOverview.getCheckOutDate()));
			map.put(UserOrderListAdapter.DataSourceKeyEnum.ORDER_STATE.name(), getOrderStateDescriptionByOrderStatusCode(orderOverview.getStatusCode()));
			map.put(UserOrderListAdapter.DataSourceKeyEnum.ORDER_ID.name(), orderOverview.getOrderId());
			
			int orderTotalPriceInt = 0;
			try {
				orderTotalPriceInt = Double.valueOf(orderOverview.getOrderTotalPrice()).intValue();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
			map.put(UserOrderListAdapter.DataSourceKeyEnum.ORDER_PRICE.name(), moneyMarkString + Integer.toString(orderTotalPriceInt));
			
			dataSource.add(map);
		}
		
		return dataSource;
	}
	
	private void updateListViewAdapterByNewDataSource(final List<Map<String, String>> newDataSource, final Enum<?> netRequestTag) {
		if (newDataSource == null || netRequestTag == null) {
			DebugLog.e(TAG, "入参错误, newDataSource/netRequestTag 不能为空! ");
			return;
		}
		
		if (GlobalConstant.OrderStateEnum.WAIT_CONFIRM == netRequestTag) {
			dataSourceForWaitConfirmListViewAdapter.addAll(newDataSource);
			waitConfirmListViewAdapter.notifyDataSetChanged();
		} else if (GlobalConstant.OrderStateEnum.WAIT_PAY == netRequestTag) {
			dataSourceForWaitPayListViewAdapter.addAll(newDataSource);
			waitPayListViewAdapter.notifyDataSetChanged();
		} else if (GlobalConstant.OrderStateEnum.WAIT_LIVE == netRequestTag) {
			dataSourceForWaitLiveListViewAdapter.addAll(newDataSource);
			waitLiveListViewAdapter.notifyDataSetChanged();
		} else if (GlobalConstant.OrderStateEnum.WAIT_COMMENT == netRequestTag) {
			dataSourceForWaitCommentListViewAdapter.addAll(newDataSource);
			waitCommentListViewAdapter.notifyDataSetChanged();
		} else if (GlobalConstant.OrderStateEnum.HAS_ENDED == netRequestTag) {
			dataSourceForWaitHasEndedListViewAdapter.addAll(newDataSource);
			waitHasEndedListViewAdapter.notifyDataSetChanged();
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
			SimpleProgressDialog.dismiss(UserOrderMainActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				DebugLog.i(TAG, "SHOW_NET_ERROR_MESSAGE");
				
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(UserOrderMainActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				DebugLog.i(TAG, "REFRESH_UI");
				final Enum<?> netRequestTagEnum = (Enum<?>) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				updateListViewAdapterByNewDataSource(newDataSource, netRequestTagEnum);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		netRequestIndexForOrderList = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	private List<Map<String, String>> newDataSource = null;
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
			
			final OrderOverviewListNetRespondBean orderOverviewListNetRespondBean = (OrderOverviewListNetRespondBean) respondDomainBean;
			DebugLog.i(TAG, orderOverviewListNetRespondBean.toString());
			
			newDataSource = parseUserOrderListToListViewAdapterDataSource(orderOverviewListNetRespondBean.getOrderOverviewList());
			if (newDataSource != null) {
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
			} else {
				handler.sendEmptyMessage(GlobalConstant.HANDLER_EMPTY_MESSAGE_WHAT);
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void gotoAccountActivity() {
		final Intent intent = new Intent(UserOrderMainActivity.this, MainNavigationActivity.class);
		// note : MainNavigationActivity 在主配置文件中, 也必须配置 singleTask, 否则会先干掉之前的 MainNavigationActivity 然后重新走onCreate
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
