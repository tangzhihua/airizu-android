package cn.airizu.activity.city_list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baidu.mapapi.MKAddrInfo;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.main_navigation.tab_item.C_RoomListMainActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.CityInfoListAdapter;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.address_citys.CityInfo;
import cn.airizu.domain.protocol.address_citys.CitysNetRequestBean;
import cn.airizu.domain.protocol.address_citys.CitysNetRespondBean;
import cn.airizu.domain.protocol.room_search.RoomSearchDatabaseFieldsConstant;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;

/**
 * 城市列表界面
 * 
 * @author zhihua.tang
 */
public class CityListActivity extends Activity implements OnScrollListener, CustomControlDelegate {
	private final String TAG = this.getClass().getSimpleName();
	
	// 选中城市之后要进行的操作
	public final static String TAG_SELECT_THE_CITY_AFTER_THE_OPERATION = "SelectTheCityAfterTheOperation";
	
	public static enum SelectTheCityAfterTheOperationEnum {
		// 立刻开始搜素目标城市的房源
		SEARCHING_ROOM_WITH_CITY,
		// 返回搜索界面
		BACK_TO_SEARCH_ACTIVITY
	};
	
	private static enum NetRequestTagEnum {
		SEARCH_CITY
	};
	
	private int netRequestIndexForSearchCity = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private SelectTheCityAfterTheOperationEnum selectTheCityAfterTheOperation;
	
	private AutoCompleteTextView autoCompleteForCityName;
	private ListView cityInfoListView;
	private ListView cityNameFirstLetterListView;
	private TextView letterPopupTextView;
	private DisapearThread disapearThread;
	private WindowManager windowManager;
	private String[] autoCompleteStrings;
	private CustomTitleBar customTitleBar;
	public static final int ACTION_RECOMMEND = 0;
	private TextView userCitytTextView;
	
	private SimpleLocationForBaiduLBS userLocationForBaiduLBS;
	private SimpleLocationForBaiduLBS.AddrInfoDelegate userAddress = new SimpleLocationForBaiduLBS.AddrInfoDelegate() {
		
		@Override
		public void addrInfoCallback(MKAddrInfo location) {
			unregisterReceiverForUserAddress();
			userCitytTextView.setText(location.addressComponents.city);
		}
	};
	
	private CitysNetRespondBean cityInfoNetRespondBean;
	private List<Map<String, String>> dataSourceForCityInfoAdapter = new ArrayList<Map<String, String>>();
	private Map<String, String> dataSourceForAutoCompleteTextView = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_city);
		
		// 获知, 当点击城市之后, 要进行的后续操作
		parseIntent(getIntent());
		
		// 加载title
		customTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		customTitleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		customTitleBar.setTitleByString(R.string.choose_city);
		customTitleBar.setDelegate(this);
		
		// 首先检查是否有用户当前地址的缓存, 如果没有, 就获取用户当前地址
		userCitytTextView = (TextView) findViewById(R.id.gps_value_TextView);
		if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() == null) {
			registerBroadcastReceiverForUserAddress();
			userLocationForBaiduLBS = new SimpleLocationForBaiduLBS(null, userAddress, true);
		} else {
			userCitytTextView.setText(SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city);
		}
		
		// 获取查询城市字母的列表
		cityNameFirstLetterListView = (ListView) findViewById(R.id.search_city_letter_ListView);
		ArrayAdapter<String> cityLetterListAdapter = new ArrayAdapter<String>(this, R.layout.search_city_letter_textview, py);
		cityNameFirstLetterListView.setAdapter(cityLetterListAdapter);
		cityNameFirstLetterListView.setDivider(null);
		cityNameFirstLetterListView.setOnItemClickListener(onItemClickListener);
		
		// 加载悬浮的字母
		letterPopupTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.search_city_letter_popup_textview, null); // 这个是悬浮出来的TextView
		// 默认设置为不可见。
		letterPopupTextView.setVisibility(View.INVISIBLE);
		// 设置WindowManager
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
		// 设置为无焦点状态
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				// 半透明效果
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(letterPopupTextView, layoutParams);
		disapearThread = new DisapearThread();
		
		// 获取查询的城市列表的listview
		cityInfoListView = (ListView) findViewById(R.id.search_city_ListView);
		cityInfoListView.setOnItemClickListener(cityListItemClickListener);
		
		// 自动补全
		autoCompleteForCityName = (AutoCompleteTextView) this.findViewById(R.id.autocom);
		autoCompleteForCityName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView cityNameTextView = (TextView) view;
				String cityName = cityNameTextView.getText().toString();
				String cityId = dataSourceForAutoCompleteTextView.get(cityName);
				gotoTargetActivityWithCityNameAndID(cityName, cityId);
			}
		});
		
		// 首先检查是否有城市信息列表的缓存, 如果没有, 就从网络中拉取数据
		cityInfoNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance().getCityInfoNetRespondBean();
		if (cityInfoNetRespondBean == null || cityInfoNetRespondBean.getCityInfoList() == null) {
			GlobalDataCacheForMemorySingleton.getInstance().setCityInfoNetRespondBean(null);
			
			requestCityList();
		} else {
			refreshUI(buildCityInfoListAdapterByNetRespondBean(cityInfoNetRespondBean), buildAutoCompleteTextViewAdapterByDataSource(dataSourceForCityInfoAdapter));
		}
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		windowManager.removeView(letterPopupTextView);
		
		// 一定要注销 "广播"
		unregisterReceiverForUserAddress();
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		
		SimpleProgressDialog.resetByThisContext(this);
		
		if (userLocationForBaiduLBS != null) {
			userLocationForBaiduLBS.stopLocationRequest();
		}
	}
	
	@Override
	protected void onResume() {
		DebugLog.i(TAG, "onResume");
		super.onResume();
		if (userLocationForBaiduLBS != null) {
			userLocationForBaiduLBS.startLocationRequest();
		}
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
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			if (intent.hasExtra(TAG_SELECT_THE_CITY_AFTER_THE_OPERATION)) {
				this.selectTheCityAfterTheOperation = (SelectTheCityAfterTheOperationEnum) intent.getSerializableExtra(TAG_SELECT_THE_CITY_AFTER_THE_OPERATION);
			}
			
			return true;
		} while (false);
		
		return false;
	}
	
	private void gotoTargetActivityWithCityNameAndID(final String cityName, final String cityId) {
		if (CityListActivity.SelectTheCityAfterTheOperationEnum.SEARCHING_ROOM_WITH_CITY.equals(selectTheCityAfterTheOperation)) {
			// 开始查询目标城市下的房源列表
			Intent intent = new Intent(CityListActivity.this, C_RoomListMainActivity.class);
			Bundle dataBundle = new Bundle();
			dataBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name(), cityId);
			dataBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name(), cityName);
			dataBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue());
			intent.putExtra(C_RoomListMainActivity.IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name(), dataBundle);
			startActivity(intent);
		} else {
			// 返回到 "搜索"界面
			Intent intent = new Intent();
			intent.putExtra(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name(), cityId);
			intent.putExtra(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name(), cityName);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	// 点击城市列表的监听
	private OnItemClickListener cityListItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			do {
				if (cityInfoNetRespondBean.getCityInfoList() == null) {
					break;
				}
				
				CityInfo cityInfo = cityInfoNetRespondBean.getCityInfoList().get(position);
				if (cityInfo == null) {
					break;
				}
				
				gotoTargetActivityWithCityNameAndID(cityInfo.getName(), cityInfo.getId());
			} while (false);
			
		}
	};
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String s = ((TextView) arg1).getText().toString();
			letterPopupTextView.setText(s);
			letterPopupTextView.setVisibility(View.VISIBLE);
			handler.removeCallbacks(disapearThread);
			// 提示延迟1.5s再消失
			handler.postDelayed(disapearThread, 1500);
			int localPosition = binSearch(dataSourceForCityInfoAdapter, s); // 接收返回值
			if (localPosition != -1) {
				// searchCityLetterPopupTextView.setVisibility(View.INVISIBLE);
				// // 防止点击出现的txtOverlay与滚动出现的txtOverlay冲突
				cityInfoListView.setSelection(localPosition); // 让List指向对应位置的Item
			}
		}
	};
	
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
	
	private void refreshUI(CityInfoListAdapter adapterForCityInfoList, ArrayAdapter<String> adapterForAutoComplete) {
		cityInfoListView.setAdapter(adapterForCityInfoList);
		autoCompleteForCityName.setAdapter(adapterForAutoComplete);
	}
	
	private CityInfoListAdapter adapterForCityInfoList;
	private ArrayAdapter<String> adapterForAutoComplete;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//
			SimpleProgressDialog.dismiss(CityListActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(CityListActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				refreshUI(adapterForCityInfoList, adapterForAutoComplete);
				adapterForCityInfoList = null;
				adapterForAutoComplete = null;
			}
		}
	};
	
	private void requestCityList() {
		CitysNetRequestBean searchCityNetRequestBean = new CitysNetRequestBean();
		netRequestIndexForSearchCity = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, searchCityNetRequestBean, NetRequestTagEnum.SEARCH_CITY, domainNetRespondCallback);
		if (netRequestIndexForSearchCity != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(CityListActivity.this, progressDialogOnCancelListener);
		}
	}
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.SEARCH_CITY == requestEvent) {
			netRequestIndexForSearchCity = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
	}
	
	private IDomainNetRespondCallback domainNetRespondCallback = new IDomainNetRespondCallback() {
		@Override
		public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean) {
			DebugLog.i(TAG, "domainNetRespondHandleInNonUIThread --- start ! ");
			clearNetRequestIndexByRequestEvent(requestEvent);
			
			if (errorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal();
				msg.getData().putString(HandlerExtraDataTypeEnum.NET_ERROR_MESSAGE.name(), errorBean.getErrorMessage());
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.SEARCH_CITY) {// 2.6 搜索城市
			
				cityInfoNetRespondBean = (CitysNetRespondBean) respondDomainBean;
				// 缓存城市列表
				GlobalDataCacheForMemorySingleton.getInstance().setCityInfoNetRespondBean(cityInfoNetRespondBean);
				
				// 对城市信息列表进行排序(按照拼音顺序)
				Collections.sort(cityInfoNetRespondBean.getCityInfoList(), new Comparator<CityInfo>() {
					
					@Override
					public int compare(CityInfo lhs, CityInfo rhs) {
						String lhsCodeString = lhs.getCode();
						String rhsCodeString = rhs.getCode();
						return lhsCodeString.compareTo(rhsCodeString);
					}
				});
				
				adapterForCityInfoList = buildCityInfoListAdapterByNetRespondBean(cityInfoNetRespondBean);
				adapterForAutoComplete = buildAutoCompleteTextViewAdapterByDataSource(dataSourceForCityInfoAdapter);
				
				//
				Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
			}
			
		}
	};
	
	private CityInfoListAdapter buildCityInfoListAdapterByNetRespondBean(CitysNetRespondBean cityInfoNetRespondBean) {
		if (cityInfoNetRespondBean == null || cityInfoNetRespondBean.getCityInfoList() == null) {
			return null;
		}
		
		for (CityInfo cityInfo : cityInfoNetRespondBean.getCityInfoList()) {
			
			// 城市名列表
			Map<String, String> map = new HashMap<String, String>();
			map.put("cityId", cityInfo.getId());
			map.put("cityName", cityInfo.getName());
			map.put("code", cityInfo.getCode());
			dataSourceForCityInfoAdapter.add(map);
			
			// 城市名自动补全
			dataSourceForAutoCompleteTextView.put(cityInfo.getName(), cityInfo.getId());
		}
		
		CityInfoListAdapter adapter = new CityInfoListAdapter(CityListActivity.this, dataSourceForCityInfoAdapter);
		return adapter;
	}
	
	private ArrayAdapter<String> buildAutoCompleteTextViewAdapterByDataSource(List<Map<String, String>> dataSourceForCityInfoAdapter) {
		if (dataSourceForCityInfoAdapter == null || dataSourceForCityInfoAdapter.size() <= 0) {
			return null;
		}
		
		autoCompleteStrings = new String[dataSourceForCityInfoAdapter.size()];
		for (int j = 0; j < dataSourceForCityInfoAdapter.size(); j++) {
			autoCompleteStrings[j] = dataSourceForCityInfoAdapter.get(j).get("cityName");
		}
		// 自动补全
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CityListActivity.this, android.R.layout.simple_dropdown_item_1line, autoCompleteStrings);
		return adapter;
	}
	
	private final String py[] =
	{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private int scrollState; // 滚动的状态
	
	private class DisapearThread implements Runnable {
		public void run() {
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) { // 是否已经停止
				letterPopupTextView.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public static int binSearch(List<Map<String, String>> list, String s) {
		do {
			if (list == null) {
				break;
			}
			for (int i = 0; i < list.size(); i++) {
				if (s.equalsIgnoreCase("" + list.get(i).get("code").charAt(0))) { // 不区分大小写
					return i;
				}
			}
			return -1;
		} while (false);
		return -1;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		letterPopupTextView.setText(String.valueOf(dataSourceForCityInfoAdapter.get(firstVisibleItem + (visibleItemCount >> 1)).get("code").charAt(0)).toUpperCase());
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			handler.removeCallbacks(disapearThread);
			// 提示延迟1.5s再消失
			handler.postDelayed(disapearThread, 1500);
		} else {
			letterPopupTextView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
		if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
			finish();
		}
	}
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private BroadcastReceiverForCityListActivity broadcastReceiver;
	
	// 接收用户成功登录的广播消息
	private class BroadcastReceiverForCityListActivity extends BroadcastReceiver {
		
		public BroadcastReceiverForCityListActivity() {
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			DebugLog.i(TAG, "BroadcastReceiverForMainNavigationActivity:onReceive");
			
			if (intent == null) {
				return;
			}
			
			if (intent.getAction().equals(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name())) {
				if (userLocationForBaiduLBS != null) {
					userLocationForBaiduLBS.stopLocationRequest();
				}
				MKAddrInfo userAddress = GlobalDataCacheForMemorySingleton.getInstance().getLastMKAddrInfo();
				if (userAddress != null) {
					userCitytTextView.setText(userAddress.addressComponents.city);
				}
			}
		}
	}
	
	private void registerBroadcastReceiverForUserAddress() {
		broadcastReceiver = new BroadcastReceiverForCityListActivity();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name());
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	private void unregisterReceiverForUserAddress() {
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}
	}
}
