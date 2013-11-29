package cn.airizu.activity.room_filter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.main_navigation.tab_item.C_RoomListMainActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.custom_control.pop_list.RadioPopList;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.address_districts.DistrictInfo;
import cn.airizu.domain.protocol.address_districts.DistrictsNetRequestBean;
import cn.airizu.domain.protocol.address_districts.DistrictsNetRespondBean;
import cn.airizu.domain.protocol.room_dictionary.DictionaryNetRequestBean;
import cn.airizu.domain.protocol.room_dictionary.DictionaryNetRespondBean;
import cn.airizu.domain.protocol.room_dictionary.RentManner;
import cn.airizu.domain.protocol.room_dictionary.RootType;
import cn.airizu.domain.protocol.room_search.RoomSearchDatabaseFieldsConstant;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;

/**
 * 房间筛选界面 这个界面, 要根据是否是从 "附近"模块 进入本界面来做 2种处理.
 * 
 * @author zhihua.tang
 */
public class RoomFilterActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 当前界面为 "附近" 界面
		IS_NEARBY_ACTIVITY,
		// 房间搜索条件
		ROOM_SEARCH_CRITERIA
	};
	
	private Bundle roomSearchCriteriaBundle;
	private boolean isNearbyActivity = false;
	
	private TextView districtNameTextView;// "房屋位置"
	private TextView priceDifferenceTextView;// "每晚价格"
	private TextView roomTypeTextView;// "房屋类型"
	private TextView rentTypeTextView;// "出租方式"
	private TextView tamenitiesTextView;// "设施设备"
	private EditText streetNameEditText;// "地标名称"
	
	private static enum NetRequestTagEnum {
		// 2.8 初始化字典
		DICTIONARY,
		// 2.7 根据城市获取区县
		ADDRESS_DISTRICTS
	};
	
	private int netRequestIndexForDictionary = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForAddressDistricts = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	// 2.7 根据城市获取区县
	private DistrictsNetRespondBean districtsNetRespondBean;
	
	private CustomTitleBar titleBar;
	
	private Map<String, String> districtNameListAdapterSource = new LinkedHashMap<String, String>();
	private Map<String, String> roomTypeListAdapterSource = new LinkedHashMap<String, String>();
	private Map<String, String> rentTypeListAdapterSource = new LinkedHashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		parseIntent(getIntent());
		
		loadRealUI();
		
		// 恢复之前的查询历史
		recoveryQueryHistory(this.roomSearchCriteriaBundle);
		
		if (!isNearbyActivity) {
			
			// 请求 "区县信息"
			requestDistricts(roomSearchCriteriaBundle.getString(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name()));
		}
		
		// 请求 "数据字典"
		if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
			
			requestDictionary();
			
		} else {
			
			// 初始化 "房屋类型" ListView 的数据源
			initRoomTypeListViewDataSource();
			
			// 初始化 "出租方式" ListView 的数据源
			initRentTypeListViewDataSource();
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
		
		SimpleProgressDialog.resetByThisContext(this);
	}
	
	@Override
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	private boolean parseIntent(final Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			if (!intent.hasExtra(IntentExtraTagEnum.IS_NEARBY_ACTIVITY.name())) {
				break;
			}
			isNearbyActivity = intent.getBooleanExtra(IntentExtraTagEnum.IS_NEARBY_ACTIVITY.name(), false);
			
			roomSearchCriteriaBundle = intent.getBundleExtra(IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name());
			if (roomSearchCriteriaBundle == null) {
				break;
			}
			DebugLog.i(TAG, "roomSearchCriteriaBundle=" + roomSearchCriteriaBundle.toString());
			
			return true;
		} while (false);
		
		return false;
	}
	
	private void loadRealUI() {
		setContentView(R.layout.room_filter);
		
		titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("筛选");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 房屋位置
		districtNameTextView = (TextView) findViewById(R.id.districtName_value_TextView);
		districtNameTextView.setOnClickListener(filterButtonClickListener);
		
		// 每晚价格
		priceDifferenceTextView = (TextView) findViewById(R.id.priceDifference_value_TextView);
		priceDifferenceTextView.setOnClickListener(filterButtonClickListener);
		
		// 房屋类型
		roomTypeTextView = (TextView) findViewById(R.id.roomType_value_TextView);
		roomTypeTextView.setOnClickListener(filterButtonClickListener);
		
		// 出租方式
		rentTypeTextView = (TextView) findViewById(R.id.rentType_value_TextView);
		rentTypeTextView.setOnClickListener(filterButtonClickListener);
		
		// 设施设备
		tamenitiesTextView = (TextView) findViewById(R.id.tamenities_value_TextView);
		tamenitiesTextView.setOnClickListener(filterButtonClickListener);
		
		// 地标输入框
		streetNameEditText = (EditText) findViewById(R.id.street_name_EditText);
		
		// "确定" 按钮
		final Button okButton = (Button) findViewById(R.id.ok_Button);
		okButton.setOnClickListener(okButtonClickListener);
		
		if (isNearbyActivity) {
			final View districtNameLayout = (View) findViewById(R.id.districtName_RelativeLayout);
			districtNameLayout.setVisibility(View.GONE);
			
			final View priceDifferenceLayout = (View) findViewById(R.id.priceDifference_RelativeLayout);
			priceDifferenceLayout.setBackgroundResource(R.drawable.bg_2);
			streetNameEditText.setVisibility(View.GONE);
		}
	}
	
	private void requestDistricts(final String cityId) {
		if (TextUtils.isEmpty(cityId)) {
			return;
		}
		
		final DistrictsNetRequestBean requestBean = new DistrictsNetRequestBean(cityId);
		netRequestIndexForAddressDistricts = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, requestBean, NetRequestTagEnum.ADDRESS_DISTRICTS,
				domainNetRespondCallback);
		if (netRequestIndexForAddressDistricts != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private void requestDictionary() {
		final DictionaryNetRequestBean requestBean = new DictionaryNetRequestBean();
		netRequestIndexForDictionary = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, requestBean, NetRequestTagEnum.DICTIONARY, domainNetRespondCallback);
		if (netRequestIndexForDictionary != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	/**
	 * 复位 "房屋位置" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryDistrictName(String oldData) {
		do {
			if (TextUtils.isEmpty(oldData)) {
				break;
			}
			
			districtNameTextView.setText(oldData);
		} while (false);
	}
	
	/**
	 * 复位 "每晚价格" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryPriceDifference(String oldData) {
		do {
			if (TextUtils.isEmpty(oldData)) {
				break;
			}
			
			for (Map.Entry<String, String> entry : GlobalConstant.dataSourceForPriceDifferenceList.entrySet()) {
				if (entry.getValue().equals(oldData)) {
					priceDifferenceTextView.setText(entry.getKey());
				}
			}
		} while (false);
	}
	
	/**
	 * 复位 "房屋类型" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryRoomType(String oldData) {
		do {
			if (TextUtils.isEmpty(oldData)) {
				break;
			}
			
			if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
				break;
			}
			
			final List<RootType> rootTypes = GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean().getRootTypes();
			if (rootTypes == null || rootTypes.size() <= 0) {
				break;
			}
			
			String value = roomTypeTextView.getText().toString();
			for (RootType rootType : rootTypes) {
				if (rootType.getTypeId().equals(oldData)) {
					value = rootType.getTypeName();
					break;
				}
			}
			roomTypeTextView.setText(value);
		} while (false);
	}
	
	/**
	 * 复位 "出租方式" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryRentType(String oldData) {
		do {
			if (TextUtils.isEmpty(oldData)) {
				break;
			}
			
			if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
				break;
			}
			
			final List<RentManner> rentManners = GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean().getRentManners();
			if (rentManners == null || rentManners.size() <= 0) {
				break;
			}
			
			String value = rentTypeTextView.getText().toString();
			for (RentManner rentManner : rentManners) {
				value = rentManner.getRentalWayName();
				break;
			}
			rentTypeTextView.setText(value);
		} while (false);
	}
	
	/**
	 * 复位 "设施设备" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryTamenities(List<String> oldData) {
		do {
			if (oldData == null || oldData.size() <= 0) {
				break;
			}
		} while (false);
	}
	
	/**
	 * 复位 "地标名称" 的查询条件
	 * 
	 * @param oldData
	 */
	private void recoveryStreetName(String oldData) {
		do {
			if (TextUtils.isEmpty(oldData)) {
				break;
			}
			
			streetNameEditText.setText(oldData);
		} while (false);
	}
	
	// 恢复之前的查询历史
	private void recoveryQueryHistory(Bundle dataBundle) {
		if (dataBundle == null) {
			return;
		}
		
		// "房屋位置"
		recoveryDistrictName(dataBundle.getString("districtName"));
		
		// "每晚价格"
		recoveryPriceDifference(dataBundle.getString("priceDifference"));
		
		// "房屋类型"
		recoveryRoomType(dataBundle.getString("roomType"));
		
		// "出租方式"
		recoveryRentType(dataBundle.getString("rentType"));
		
		// "设施设备"
		recoveryTamenities(dataBundle.getStringArrayList("tamenities"));
		
		// "地标名称"
		recoveryStreetName(dataBundle.getString("streetName"));
	}
	
	private View.OnClickListener filterButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// 房屋位置
				case R.id.districtName_value_TextView: {
					final List<String> list = new ArrayList<String>(districtNameListAdapterSource.keySet());
					activateRadioPopWindow("房租位置", list, (View) districtNameTextView);
				}
				break;
				
				// 每晚价格
				case R.id.priceDifference_value_TextView: {
					final List<String> list = new ArrayList<String>();
					list.add("不限");
					list.addAll(GlobalConstant.dataSourceForPriceDifferenceList.keySet());
					activateRadioPopWindow("每晚价格", list, (View) priceDifferenceTextView);
				}
				break;
				
				// 房屋类型
				case R.id.roomType_value_TextView: {
					final List<String> list = new ArrayList<String>(roomTypeListAdapterSource.keySet());
					activateRadioPopWindow("房屋类型", list, (View) roomTypeTextView);
				}
				break;
				
				// 出租方式
				case R.id.rentType_value_TextView: {
					final List<String> list = new ArrayList<String>(rentTypeListAdapterSource.keySet());
					activateRadioPopWindow("出租方式", list, (View) rentTypeTextView);
				}
				break;
				
				// 设施设备
				case R.id.tamenities_value_TextView: {
					
				}
				break;
				
				default:
				break;
			}
			
		}
	};
	
	private View.OnClickListener okButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (roomSearchCriteriaBundle == null) {
				// 异常情况
				DebugLog.e(TAG, "房源查询条件不能为null");
				roomSearchCriteriaBundle = new Bundle();
			}
			
			String key = "";
			String value = "";
			
			// "房屋位置"
			key = districtNameTextView.getText().toString();
			value = districtNameListAdapterSource.get(key);
			if (value == null || value.equals("不限")) {
				roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name());
				roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name());
			} else {
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name(), key);
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name(), value);
			}
			
			// "每晚价格"
			key = priceDifferenceTextView.getText().toString();
			value = GlobalConstant.dataSourceForPriceDifferenceList.get(key);
			if (value == null || value.equals("不限")) {
				roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name());
			} else {
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name(), value);
			}
			
			// "房屋类型"
			key = roomTypeTextView.getText().toString();
			value = roomTypeListAdapterSource.get(key);
			if (value == null || value.equals("不限")) {
				roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name());
			} else {
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name(), value);
			}
			
			// "出租方式"
			key = rentTypeTextView.getText().toString();
			value = rentTypeListAdapterSource.get(key);
			if (value == null || value.equals("不限")) {
				roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name());
			} else {
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name(), value);
			}
			
			// "设施设备"
			if (!tamenitiesTextView.getText().equals("不限")) {
				roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.tamenities.name(), tamenitiesTextView.getText().toString());
			}
			
			if (!isNearbyActivity) {
				// "地标名称"
				String oldStreetName = roomSearchCriteriaBundle.getString(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name());
				String newStreetName = streetNameEditText.getText().toString();
				
				if (TextUtils.isEmpty(newStreetName)) {
					// 如果地标输入框中没有任何数据的话, 就清理掉地标查询关键字
					roomSearchCriteriaBundle.remove(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name());
					
					// 如果之前的房源查询关键字中有 "地标" , 此时用户是删除了之前的地标关键字, 那么就要重置 "排序" 关键字
					if (!TextUtils.isEmpty(oldStreetName)) {
						roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue());
					}
				} else if (!newStreetName.equals(oldStreetName)) {
					// 如果地标关键字发生了变化, 就更新地标查询关键字, 并且重新设置 "排序" 方式为 "由近及远"
					roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name(), newStreetName);
					
					// 房源列表页，按照地标搜索后，默认按照距离由近至远进行排序
					roomSearchCriteriaBundle.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue());
				} else {
					// 用户没有改变地标查询关键字
				}
			}
			
			final Intent intent = new Intent();
			intent.putExtra(C_RoomListMainActivity.IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name(), new Bundle(roomSearchCriteriaBundle));
			setResult(RESULT_OK, intent);
			DebugLog.i(TAG, "back to VicinityMainActivity ! ");
			finish();
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
			
			SimpleProgressDialog.dismiss(RoomFilterActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(RoomFilterActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.DICTIONARY == requestEvent) {
			netRequestIndexForDictionary = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (NetRequestTagEnum.ADDRESS_DISTRICTS == requestEvent) {
			netRequestIndexForAddressDistricts = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.DICTIONARY) {// 2.8 初始化字典
				DictionaryNetRespondBean respondBean = (DictionaryNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, respondBean.toString());
				
				// 缓存 "字典数据"
				GlobalDataCacheForMemorySingleton.getInstance().setDictionaryNetRespondBean(respondBean);
				
				// 初始化 "房屋类型" ListView 的数据源
				initRoomTypeListViewDataSource();
				
				// 初始化 "出租方式" ListView 的数据源
				initRentTypeListViewDataSource();
				
				handler.sendEmptyMessage(GlobalConstant.HANDLER_EMPTY_MESSAGE_WHAT);
			} else if (requestEvent == NetRequestTagEnum.ADDRESS_DISTRICTS) {// 2.7 根据城市获取区县
				districtsNetRespondBean = (DistrictsNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, districtsNetRespondBean.toString());
				
				// 初始化 "房屋位置" ListView 的数据源
				initDistrictNameListViewDataSource();
				
				handler.sendEmptyMessage(GlobalConstant.HANDLER_EMPTY_MESSAGE_WHAT);
			}
		}
	};
	
	/**
	 * 初始化 "房屋位置" ListView 的数据源
	 */
	private void initDistrictNameListViewDataSource() {
		do {
			if (districtsNetRespondBean == null) {
				break;
			}
			
			final List<DistrictInfo> districtInfoList = districtsNetRespondBean.getDistrictInfoList();
			if (districtInfoList == null || districtInfoList.size() <= 0) {
				break;
			}
			
			districtNameListAdapterSource.clear();
			districtNameListAdapterSource.put("不限", "不限");
			for (DistrictInfo districtInfo : districtInfoList) {
				districtNameListAdapterSource.put(districtInfo.getName(), districtInfo.getId());
			}
		} while (false);
	}
	
	/**
	 * 初始化 "房屋类型" ListView 的数据源
	 */
	private void initRoomTypeListViewDataSource() {
		do {
			if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
				break;
			}
			
			final List<RootType> rootTypes = GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean().getRootTypes();
			if (rootTypes == null || rootTypes.size() <= 0) {
				break;
			}
			
			roomTypeListAdapterSource.clear();
			roomTypeListAdapterSource.put("不限", "不限");
			for (RootType rootType : rootTypes) {
				roomTypeListAdapterSource.put(rootType.getTypeName(), rootType.getTypeId());
			}
		} while (false);
	}
	
	/**
	 * 初始化 "出租方式" ListView 的数据源
	 */
	private void initRentTypeListViewDataSource() {
		do {
			if (GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean() == null) {
				break;
			}
			
			final List<RentManner> rentManners = GlobalDataCacheForMemorySingleton.getInstance().getDictionaryNetRespondBean().getRentManners();
			if (rentManners == null || rentManners.size() <= 0) {
				break;
			}
			rentTypeListAdapterSource.clear();
			rentTypeListAdapterSource.put("不限", "不限");
			for (RentManner rentManner : rentManners) {
				rentTypeListAdapterSource.put(rentManner.getRentalWayName(), rentManner.getRentalWayId());
			}
		} while (false);
	}
	
	private PopupWindow popupWindow;
	private RadioPopList radioPopList;
	
	// 激活一个单选弹出对话框
	private void activateRadioPopWindow(String title, List<String> dataSource, View triggerView) {
		
		if (popupWindow == null) {
			
			// 加载popupWindow的布局文件
			radioPopList = new RadioPopList(this, radioPopListDelegate);
			
			// 声明一个弹出框
			popupWindow = new PopupWindow(radioPopList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.setFocusable(true);
		}
		
		radioPopList.setTitle(title);
		radioPopList.setTag(triggerView);
		final RadioPopListAdapter radioPopListAdapter = new RadioPopListAdapter(this, dataSource);
		radioPopList.setAdapter(radioPopListAdapter);
		popupWindow.showAsDropDown(titleBar);
	}
	
	private CustomControlDelegate radioPopListDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			RadioPopList.ActionEnum actionEnum = (RadioPopList.ActionEnum) actionTypeEnum;
			switch (actionEnum)
			{
				case TITLE_LEFT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				case TITLE_RIGHT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				case LIST_ITEM_CLICK: {
					do {
						final View view = (View) contorl;
						if (view == null) {
							break;
						}
						final TextView contentTextView = (TextView) view.findViewById(R.id.content_TextView);
						if (contentTextView == null) {
							break;
						}
						final String text = contentTextView.getText().toString();
						if (TextUtils.isEmpty(text)) {
							break;
						}
						final TextView triggerView = (TextView) radioPopList.getTag();
						if (triggerView == null) {
							break;
						}
						triggerView.setText(text);
						
					} while (false);
					popupWindow.dismiss();
				}
				break;
				default:
				break;
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
