package cn.airizu.activity.main_navigation;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.main_navigation.tab_item.A_RecommendMainActivity;
import cn.airizu.activity.main_navigation.tab_item.B_SearchMainActivity;
import cn.airizu.activity.main_navigation.tab_item.C_RoomListMainActivity;
import cn.airizu.activity.main_navigation.tab_item.D_AccountMainActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForNeedSaveToFileSystem;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import cn.airizu.toolutils.lbs.baidu.SimpleLocationForBaiduLBS;

/**
 * TabHost主界面
 * 
 * @author zhihua.tang
 */
public class MainNavigationActivity extends TabActivity {
	private final String TAG = this.getClass().getSimpleName();
	
	private CustomTitleBar titleBar;
	
	private static enum TabHostTagEnum {
		// "推荐"
		RECOMMEND,
		// "搜索"
		SEARCH,
		// "附近"
		VICINITY,
		// "账号"
		ACCOUNT
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_navigation_activity);
		
		// 接收 : 用户登录成功
		// 接收 : 获取用户地址成功
		// 接收 : 跳转到 "推荐首页"
		registerBroadcastReceiver();
		
		//
		titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		initTabHost();
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
	protected void onStop() {
		DebugLog.i(TAG, "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		
		// 一定要注销 "广播"
		unregisterReceiver(broadcastReceiver);
		
		// 停止和当前App相关的所有服务
		ToolsFunctionForThisProgect.stopServiceWithThisApp();
		
		// TODO:目前只在这里保存数据
		GlobalDataCacheForNeedSaveToFileSystem.saveAllCacheData();
		
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		DebugLog.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		DebugLog.i(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
	private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String tabId) {
			
			final TabHostTagEnum nextTabIndex = TabHostTagEnum.valueOf(tabId);
			
			/**
			 * 除了 "附近"界面外, 其他界面都需要显示 TabWidget控件(就是屏幕下方的导航栏)
			 */
			getTabWidget().setVisibility(View.VISIBLE);
			
			switch (nextTabIndex)
			{
				// "推荐"
				case RECOMMEND: {
					titleBar.setLeftButtonByImage(CustomTitleBar.INVISIBLE_BUTTON);
					titleBar.setTitleByImage(R.drawable.logo_title);
					titleBar.setRightButtonByImage(R.drawable.selector_main_navigation_titlebar_phone_button);
				}
				break;
				
				// "搜索"
				case SEARCH: {
					titleBar.setLeftButtonByImage(CustomTitleBar.INVISIBLE_BUTTON);
					titleBar.setTitleByString(R.string.search);
					titleBar.setRightButtonByImage(R.drawable.selector_main_navigation_titlebar_phone_button);
				}
				break;
				
				// "附近"
				case VICINITY: {
					titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
					if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
						String userAddress = SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.district;
						titleBar.setTitleByString(userAddress);
					} else {
						titleBar.setTitleByString(R.string.vicinity);
					}
					titleBar.setRightButtonByImage(CustomTitleBar.INVISIBLE_BUTTON);
					
					// "附近" 界面中, 不需要显示屏幕下方的导航TabWeight
					getTabWidget().setVisibility(View.GONE);
				}
				break;
				
				// "账号"
				case ACCOUNT: {
					titleBar.setLeftButtonByImage(CustomTitleBar.INVISIBLE_BUTTON);
					titleBar.setTitleByString(R.string.account);
					titleBar.setRightButtonByImage(CustomTitleBar.INVISIBLE_BUTTON);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	private TabHost.TabSpec createATabSpec(int resourceIdForIcon, int resourceIdForLabel, String tag, Intent intentForTabContent) {
		if (TextUtils.isEmpty(tag) || intentForTabContent == null) {
			return null;
		}
		
		final TabHost tabHost = getTabHost();
		final String labelString = getResources().getString(resourceIdForLabel);
		final Drawable iconDrawable = getResources().getDrawable(resourceIdForIcon);
		return tabHost.newTabSpec(tag).setIndicator(labelString, iconDrawable).setContent(intentForTabContent);
	}
	
	private void initTabHost() {
		
		final TabHost tabHost = getTabHost();
		
		tabHost.setup(getLocalActivityManager());
		tabHost.setOnTabChangedListener(tabChangeListener);
		
		// "推荐"
		Intent intent = new Intent(this, A_RecommendMainActivity.class);
		tabHost.addTab(createATabSpec(R.drawable.selector_main_navigation_tabspec_recommend_icon, R.string.recommend, TabHostTagEnum.RECOMMEND.name(), intent));
		// "搜索"
		intent = new Intent(this, B_SearchMainActivity.class);
		tabHost.addTab(createATabSpec(R.drawable.selector_main_navigation_tabspec_search_icon, R.string.search, TabHostTagEnum.SEARCH.name(), intent));
		// "附近"
		intent = new Intent(this, C_RoomListMainActivity.class);
		intent.putExtra(C_RoomListMainActivity.IntentExtraTagEnum.IS_NEARBY_ACTIVITY.name(), true);
		tabHost.addTab(createATabSpec(R.drawable.selector_main_navigation_tabspec_vicinity_icon, R.string.vicinity, TabHostTagEnum.VICINITY.name(), intent));
		// "账号"
		intent = new Intent(this, D_AccountMainActivity.class);
		tabHost.addTab(createATabSpec(R.drawable.selector_main_navigation_tabspec_account_icon, R.string.account, TabHostTagEnum.ACCOUNT.name(), intent));
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			
			final TabHostTagEnum currentTab = TabHostTagEnum.valueOf(getTabHost().getCurrentTabTag());
			switch (currentTab)
			{
				case RECOMMEND:// "推荐"
				case SEARCH:// "搜索"
				{
					if (CustomTitleBar.ActionEnum.RIGHT_BUTTON_CLICKED == actionTypeEnum) {
						// 拨打电话
						Toast.makeText(MainNavigationActivity.this, "拨打电话----", 0).show();
					}
				}
				break;
				
				case VICINITY:// "附近"
				{
					if (CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED == actionTypeEnum) {
						// 跳转到 "推荐首页"
						getTabHost().setCurrentTabByTag(TabHostTagEnum.RECOMMEND.name());
					}
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BroadcastReceiverForMainNavigationActivity broadcastReceiver = new BroadcastReceiverForMainNavigationActivity();
	
	// 接收用户成功登录的广播消息
	private class BroadcastReceiverForMainNavigationActivity extends BroadcastReceiver {
		
		public BroadcastReceiverForMainNavigationActivity() {
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			DebugLog.i(TAG, "BroadcastReceiverForMainNavigationActivity:onReceive");
			
			if (intent == null) {
				return;
			}
			
			if (intent.getAction().equals(GlobalConstant.UserActionEnum.GOTO_HOME_PAGE.name())) {
				getTabHost().setCurrentTabByTag(TabHostTagEnum.RECOMMEND.name());
			} else if (intent.getAction().equals(GlobalConstant.UserActionEnum.USER_LOGON_SUCCESS.name())) {
				getTabHost().setCurrentTabByTag(TabHostTagEnum.RECOMMEND.name());
			} else if (intent.getAction().equals(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name())) {
				TabHostTagEnum currentTab = TabHostTagEnum.valueOf(getTabHost().getCurrentTabTag());
				if (currentTab.equals(TabHostTagEnum.VICINITY)) {
					if (SimpleLocationForBaiduLBS.getLastMKAddrInfo() != null) {
						String userAddress = SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.city + SimpleLocationForBaiduLBS.getLastMKAddrInfo().addressComponents.district;
						titleBar.setTitleByString(userAddress);
						titleBar.invalidate();
					}
				}
			}
		}
	}
	
	private void registerBroadcastReceiver() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalConstant.UserActionEnum.GOTO_HOME_PAGE.name());
		intentFilter.addAction(GlobalConstant.UserActionEnum.USER_LOGON_SUCCESS.name());
		intentFilter.addAction(GlobalConstant.UserActionEnum.GET_USER_ADDRESS_SUCCESS.name());
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
