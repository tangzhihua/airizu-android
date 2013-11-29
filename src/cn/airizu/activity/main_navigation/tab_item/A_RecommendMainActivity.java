package cn.airizu.activity.main_navigation.tab_item;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.activity.city_list.CityListActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RecommendCityListAdapter;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.room_recommend.RecommendCity;
import cn.airizu.domain.protocol.room_recommend.RecommendNetRequestBean;
import cn.airizu.domain.protocol.room_recommend.RecommendNetRespondBean;
import cn.airizu.domain.protocol.room_search.RoomSearchDatabaseFieldsConstant;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

/**
 * "推荐" 界面
 * 
 * @author zhihua.tang
 */
public class A_RecommendMainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private ListView recommendCityListListView;
	
	private static enum NetRequestTagEnum {
		//
		ROOM_RECOMMEND
	};
	
	private int netRequestIndexForRecommendCity = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private List<Map<String, String>> dataSourceForRecommendCityListView;
	private RecommendCityListAdapter recommendCityListAdapter;
	
	private void activityFirstUILoadInOnCreateMethod() {
		do {
			// TODO : 这里还未考虑好本地缓存的数据何时过期
			final RecommendNetRespondBean recommendNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance().getRecommendNetRespondBean();
			if (recommendNetRespondBean == null) {
				break;
			}
			if (recommendNetRespondBean.getRecommendCityList() == null || recommendNetRespondBean.getRecommendCityList().size() <= 0) {
				break;
			}
			dataSourceForRecommendCityListView = parseRecommendCityListToListViewAdapterDataSource(recommendNetRespondBean.getRecommendCityList());
			loadRealUIAndUseRecommendNetRespondBeanInitialize(dataSourceForRecommendCityListView);
			
			// 从本地缓存加载, 不需要联网重新获取
			return;
		} while (false);
		
		// 从网络侧拉取推荐城市列表数据
		loadPreLoadedUIAndInitialize();
		requestRecommendCityList();
		return;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		
		activityFirstUILoadInOnCreateMethod();
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
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		
		netRequestIndexForRecommendCity = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DebugLog.i(TAG, "onKeyDown");
		
		// 目前统一在 "推荐首页" 中进行App退出
		ToolsFunctionForThisProgect.onKeyDownForFinishApp(this, keyCode, event);
		return true;
	}
	
	// 加载 "预加载UI界面"
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	// 加载 "出现网络错误时的UI界面, 在这里给用户重新请求网络数据的机会"
	private void loadNetErrorProcessUIAndInitialize(final String netErrorMessageString, final Enum<?> netRequestTagEnum) {
		
		if (netRequestTagEnum == NetRequestTagEnum.ROOM_RECOMMEND) {
			
			// 错误提示 TextView
			final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
			if (infoLabelForPreLoadedUiTextView != null || !TextUtils.isEmpty(netErrorMessageString)) {
				infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
				infoLabelForPreLoadedUiTextView.setText(netErrorMessageString);
			}
			
			// 重新请求网络数据的按钮
			final Button functionButtonForPreloadedUiButton = (Button) findViewById(R.id.function_button_for_preloaded_ui_Button);
			if (functionButtonForPreloadedUiButton != null) {
				functionButtonForPreloadedUiButton.setVisibility(View.VISIBLE);
				functionButtonForPreloadedUiButton.setText("刷新");
				functionButtonForPreloadedUiButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						requestRecommendCityList();
					}
				});
			}
		}
	}
	
	// 加载 "真正的UI界面"
	private void loadRealUIAndUseRecommendNetRespondBeanInitialize(final List<Map<String, String>> dataSourceForRecommendListView) {
		setContentView(R.layout.recommend_main_activity);
		
		// 推荐城市列表
		recommendCityListListView = (ListView) findViewById(R.id.recommend_city_ListView);
		recommendCityListAdapter = new RecommendCityListAdapter(this, dataSourceForRecommendListView, recommendCityListDelegate);
		recommendCityListListView.setAdapter(recommendCityListAdapter);
		
		// 跳转到城市列表界面的图片
		final ImageView gotoCityListActivityImageView = (ImageView) findViewById(R.id.goto_city_list_activity_ImageView);
		gotoCityListActivityImageView.setOnClickListener(gotoCityListActivityImageViewOnClickListener);
	}
	
	private OnClickListener gotoCityListActivityImageViewOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final Intent intent = new Intent(A_RecommendMainActivity.this, CityListActivity.class);
			intent.putExtra(CityListActivity.TAG_SELECT_THE_CITY_AFTER_THE_OPERATION, CityListActivity.SelectTheCityAfterTheOperationEnum.SEARCHING_ROOM_WITH_CITY);
			startActivity(intent);
		}
	};
	
	private void gotoRoomListActivity(String cityIdString, String cityNameString, String orderString, String streetNameString) {
		final Intent intent = new Intent(this, C_RoomListMainActivity.class);
		final Bundle data = new Bundle();
		if (!TextUtils.isEmpty(cityIdString)) {
			data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name(), cityIdString);
		}
		if (!TextUtils.isEmpty(cityNameString)) {
			data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name(), cityNameString);
		}
		if (!TextUtils.isEmpty(orderString)) {
			data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), orderString);
		}
		if (!TextUtils.isEmpty(streetNameString)) {
			data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name(), streetNameString);
		}
		
		intent.putExtra(C_RoomListMainActivity.IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name(), data);
		startActivity(intent);
	}
	
	private CustomControlDelegate recommendCityListDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			
			do {
				if (contorl == null || actionTypeEnum == null) {
					break;
				}
				
				// 一个 ListView , 每个Item上, 有几个可被点击的按钮类控件,
				// 这里的设计思路是 : 不在 ListView 的适配器中编写业务逻辑代码, 也就是说, 不在ListView的适配器中空监控到某个Item
				// 的按钮被选中后, 立刻执行这个按钮对应的 逻辑业务 代码, 逻辑业务代码应该在 控制层中执行,
				// 而View 和 View的适配器, 只是单纯的UI显示, 并且当用户何其交互时, 通过代理方式告知控制层, 来进行具体的业务逻辑执行.
				// 具体设计概述 :
				// 1. 复杂的ListView 的适配编写上, 要增加一个 getCurrentlyPosition() 方法, 通过这个方法来获取当前子按钮属于ListView的那个Item的索引
				// 2. 可以在控制层 保存 适配器对象句柄, 这样当delegate返回时, 可以通过 Adapter. getCurrentlyPosition() 来知道Item索引
				// 3. 在控制层, 要保存 "业务Bean列表", 这样就可以通过 Item索引来从 "业务Bean列表" 中获取具体的 "业务Bean", 来获取数据
				// 通过上面的设计, 就可以将适配器和业务逻辑分开了, 这样适配器就有了复用性
				final RecommendNetRespondBean recommendNetRespondBean = GlobalDataCacheForMemorySingleton.getInstance.getRecommendNetRespondBean();
				if (recommendNetRespondBean == null || recommendNetRespondBean.getRecommendCityList() == null) {
					break;
				}
				
				// 当前哪个item被选中了
				final int currentlyPosition = recommendCityListAdapter.getCurrentlyPosition();
				if (currentlyPosition < 0 || currentlyPosition >= recommendNetRespondBean.getRecommendCityList().size()) {
					break;
				}
				final RecommendCity recommendCity = recommendNetRespondBean.getRecommendCityList().get(currentlyPosition);
				if (recommendCity == null) {
					break;
				}
				
				final RecommendCityListAdapter.ActionEnum actionEnum = (RecommendCityListAdapter.ActionEnum) actionTypeEnum;
				switch (actionEnum)
				{
					case CITY_PHOTO_CLICKED: {
						gotoRoomListActivity(recommendCity.getCityId(), recommendCity.getCityName(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue(), "");
					}
					break;
					
					case STREET_1_CLICKED: {
						gotoRoomListActivity(recommendCity.getCityId(), recommendCity.getCityName(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue(), recommendCity.getStreet1Name());
					}
					break;
					case STREET_2_CLICKED: {
						gotoRoomListActivity(recommendCity.getCityId(), recommendCity.getCityName(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue(), recommendCity.getStreet2Name());
					}
					break;
					default:
					break;
				}
			} while (false);
			
		}
	};
	
	private List<Map<String, String>> parseRecommendCityListToListViewAdapterDataSource(final List<RecommendCity> recommendCityList) {
		
		final List<Map<String, String>> dataSource = new ArrayList<Map<String, String>>();
		if (recommendCityList != null) {
			for (RecommendCity city : recommendCityList) {
				
				final Map<String, String> map = new HashMap<String, String>();
				map.put(RecommendCityListAdapter.DataSourceKeyEnum.CITY_NAME.name(), city.getCityName());
				String imageFullUrl = "http://124.65.163.102:819/app" + city.getImage();
				map.put(RecommendCityListAdapter.DataSourceKeyEnum.CITY_PHOTO.name(), imageFullUrl);
				map.put(RecommendCityListAdapter.DataSourceKeyEnum.STREET_1_NAME.name(), city.getStreet1Name());
				map.put(RecommendCityListAdapter.DataSourceKeyEnum.STREET_2_NAME.name(), city.getStreet2Name());
				
				dataSource.add(map);
			}
		}
		
		return dataSource;
	}
	
	private void requestRecommendCityList() {
		// 2.4 推荐城市
		final RecommendNetRequestBean recommendNetRequestBean = new RecommendNetRequestBean();
		netRequestIndexForRecommendCity = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, recommendNetRequestBean, NetRequestTagEnum.ROOM_RECOMMEND, domainNetRespondCallback);
		if (netRequestIndexForRecommendCity != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI_FOR_RECOMMEND_CITY_LIST
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(A_RecommendMainActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessageString = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessageString, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI_FOR_RECOMMEND_CITY_LIST.ordinal()) {
				loadRealUIAndUseRecommendNetRespondBeanInitialize(dataSourceForRecommendCityListView);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ROOM_RECOMMEND == requestEvent) {
			netRequestIndexForRecommendCity = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.ROOM_RECOMMEND) {// 2.4 房间推荐
				final RecommendNetRespondBean recommendNetRespondBean = (RecommendNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, recommendNetRespondBean.toString());
				
				// 缓存 推荐城市列表
				GlobalDataCacheForMemorySingleton.getInstance().setRecommendNetRespondBean(recommendNetRespondBean);
				// 耗时的初始化工作, 在非UI线程中完成
				dataSourceForRecommendCityListView = parseRecommendCityListToListViewAdapterDataSource(recommendNetRespondBean.getRecommendCityList());
				
				//
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI_FOR_RECOMMEND_CITY_LIST.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
		}
	};
}
