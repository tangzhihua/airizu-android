package cn.airizu.activity.room_detail_overview;

import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomDetailOfOverviewActivity extends ExpandableListActivity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 房间详情
		ROOM_DETAIL_BEAN
	};
	
	private RoomDetailNetRespondBean roomDetailNetRespondBean;
	
	private MyExpandableListAdapter expandableListAdapter;
	
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_detail_overview_activity);
		
		// 必须放在开始的位置
		if (parseIntent(getIntent())) {
			loadRealUIAndUseRoomDetailNetRespondBeanInitialize(roomDetailNetRespondBean);
		} else {
			loadErrorProcessUIAndInitialize();
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
			roomDetailNetRespondBean = (RoomDetailNetRespondBean) intent.getSerializableExtra(IntentExtraTagEnum.ROOM_DETAIL_BEAN.name());
			if (roomDetailNetRespondBean == null) {
				break;
			}
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void loadErrorProcessUIAndInitialize() {
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("房间概述");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView errorHintTextView = (TextView) findViewById(R.id.error_hint_TextView);
		errorHintTextView.setText("加载错误,请返回上一页...");
		errorHintTextView.setVisibility(View.VISIBLE);
		
		final View freeBookToolbarLayout = findViewById(R.id.free_book_toolbar_layout);
		freeBookToolbarLayout.setVisibility(View.INVISIBLE);
	}
	
	private void loadRealUIAndUseRoomDetailNetRespondBeanInitialize(RoomDetailNetRespondBean roomDetailNetRespondBean) {
		if (roomDetailNetRespondBean == null) {
			return;
		}
		
		// titlebar
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		final String titleBarTitleString = "房间 : " + roomDetailNetRespondBean.getNumber();
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 房间单价
		final TextView priceTextView = (TextView) findViewById(R.id.price_TextView);
		priceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(roomDetailNetRespondBean.getPrice()));
		
		expandableListAdapter = new MyExpandableListAdapter();
		setListAdapter(expandableListAdapter);
		// final WebView webView = (WebView) findViewById(R.id.room_description_WebView);
		// String string = StreamTools.toUTF8String(roomDetailNetRespondBean.getDescription());
		// Spanned apSpanned = new SpannedString(string);
		// String html = Html.toHtml(apSpanned);
		// //webView.getSettings().setDefaultTextEncodingName("utf-8");
		// webView.loadData(apSpanned.toString(), "text/html", "utf-8");
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			} else if (actionTypeEnum == CustomTitleBar.ActionEnum.RIGHT_BUTTON_CLICKED) {
				
			}
		}
	};
	
	private static enum GroupIdEnum {
		// "基本信息"
		BASE_INFO(R.string.basic_information),
		// "配套设施"
		EQUIPMENT(R.string.supporting_facilities),
		// "房间概况"
		ROOM_DESCRIPTION(R.string.room_overview),
		// "交易规则"
		TRADING_RULE(R.string.trading_rules),
		// "使用规则"
		USE_RULE(R.string.use_rules);
		
		private final int groupTitleResId;
		
		public int getGroupTitleResId() {
			return groupTitleResId;
		}
		
		private GroupIdEnum(int groupTitleResId) {
			this.groupTitleResId = groupTitleResId;
		}
	};
	
	private class MyExpandableListAdapter extends BaseExpandableListAdapter {
		
		// "基本信息"
		private View buildBaseInfoLayoutAndInitialize() {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View childrenView = inflaterInstance.inflate(R.layout.room_description_for_base_info, null);
			
			// 房屋类型
			final TextView property_type_content_TextView = (TextView) childrenView.findViewById(R.id.property_type_content_TextView);
			property_type_content_TextView.setText(roomDetailNetRespondBean.getPropertyType());
			// 租住方式
			final TextView privacy_content_TextView = (TextView) childrenView.findViewById(R.id.privacy_content_TextView);
			privacy_content_TextView.setText(roomDetailNetRespondBean.getPrivacy());
			// 卫浴类型
			final TextView rest_room_content_TextView = (TextView) childrenView.findViewById(R.id.rest_room_content_TextView);
			rest_room_content_TextView.setText(roomDetailNetRespondBean.getRestRoom());
			// 卫生间数
			final TextView bath_room_number_content_TextView = (TextView) childrenView.findViewById(R.id.bath_room_number_content_TextView);
			bath_room_number_content_TextView.setText(roomDetailNetRespondBean.getBathRoomNum());
			// 卧室数量
			final TextView bed_room_content_TextView = (TextView) childrenView.findViewById(R.id.bed_room_content_TextView);
			bed_room_content_TextView.setText(roomDetailNetRespondBean.getBedRoom());
			// 可住人数
			final TextView accommodates_content_TextView = (TextView) childrenView.findViewById(R.id.accommodates_content_TextView);
			accommodates_content_TextView.setText(roomDetailNetRespondBean.getAccommodates());
			// 房间床型
			final TextView bed_type_content_TextView = (TextView) childrenView.findViewById(R.id.bed_type_content_TextView);
			bed_type_content_TextView.setText(roomDetailNetRespondBean.getBedType());
			// 房间床数
			final TextView beds_content_TextView = (TextView) childrenView.findViewById(R.id.beds_content_TextView);
			beds_content_TextView.setText(roomDetailNetRespondBean.getBeds());
			// 建筑面积
			final TextView size_content_TextView = (TextView) childrenView.findViewById(R.id.size_content_TextView);
			size_content_TextView.setText(roomDetailNetRespondBean.getSize());
			// 退房时间
			final TextView checkout_time_content_TextView = (TextView) childrenView.findViewById(R.id.checkout_time_content_TextView);
			checkout_time_content_TextView.setText(roomDetailNetRespondBean.getCheckOutTime());
			// 最多天数
			final TextView max_nights_content_TextView = (TextView) childrenView.findViewById(R.id.max_nights_content_TextView);
			max_nights_content_TextView.setText(roomDetailNetRespondBean.getMaxNights());
			// 最少天数
			final TextView min_nights_content_TextView = (TextView) childrenView.findViewById(R.id.min_nights_content_TextView);
			min_nights_content_TextView.setText(roomDetailNetRespondBean.getMinNights());
			// 提供发票
			final TextView tickets_content_TextView = (TextView) childrenView.findViewById(R.id.tickets_content_TextView);
			tickets_content_TextView.setText(roomDetailNetRespondBean.getTickets());
			// 退订条款
			final TextView cancellation_content_TextView = (TextView) childrenView.findViewById(R.id.cancellation_content_TextView);
			cancellation_content_TextView.setText(roomDetailNetRespondBean.getCancellation());
			// 清洁服务类型
			final TextView clean_content_TextView = (TextView) childrenView.findViewById(R.id.clean_content_TextView);
			clean_content_TextView.setText(roomDetailNetRespondBean.getClean());
			
			return childrenView;
		}
		
		// "配套设施"
		private View buildEquipmentLayoutAndInitialize() {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View childrenView = inflaterInstance.inflate(R.layout.room_description_for_equipment, null);
			
			return childrenView;
		}
		
		// "房间概况"
		private View buildRoomDescriptionLayoutAndInitialize() {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View childrenView = inflaterInstance.inflate(R.layout.room_description_room_description, null);
			
			final TextView room_description_content_TextView = (TextView) childrenView.findViewById(R.id.room_description_content_TextView);
			final String roomDescriptionString = roomDetailNetRespondBean.getDescription();
			if (!TextUtils.isEmpty(roomDescriptionString)) {
				final Spanned roomDescriptionSpanned = new SpannedString(roomDescriptionString);
				final String roomDescriptionHtmlString = Html.toHtml(roomDescriptionSpanned);
				room_description_content_TextView.setText(Html.fromHtml(roomDescriptionHtmlString));
			}
			
			return childrenView;
		}
		
		// "交易规则"
		private View buildTradingRuleLayoutAndInitialize() {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View childrenView = inflaterInstance.inflate(R.layout.room_description_for_trading_rule, null);
			
			final TextView trading_rule_content_TextView = (TextView) childrenView.findViewById(R.id.trading_rule_content_TextView);
			final String tradingRuleString = roomDetailNetRespondBean.getRuleContent();
			if (!TextUtils.isEmpty(tradingRuleString)) {
				trading_rule_content_TextView.setText(Html.fromHtml(tradingRuleString));
			}
			
			return childrenView;
		}
		
		// "使用规则"
		private View buildUseRuleLayoutAndInitialize() {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View childrenView = inflaterInstance.inflate(R.layout.room_description_for_use_rule, null);
			
			final TextView use_rule_content_TextView = (TextView) childrenView.findViewById(R.id.use_rule_content_TextView);
			final String roomRuleString = roomDetailNetRespondBean.getRoomRule();
			if (!TextUtils.isEmpty(roomRuleString)) {
				use_rule_content_TextView.setText(Html.fromHtml(roomRuleString));
			}
			
			return childrenView;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}
		
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			View childrenView = null;
			
			if (groupPosition == GroupIdEnum.BASE_INFO.ordinal()) {// "基本信息"
				childrenView = buildBaseInfoLayoutAndInitialize();
			} else if (groupPosition == GroupIdEnum.EQUIPMENT.ordinal()) {// "配套设施"
				childrenView = buildEquipmentLayoutAndInitialize();
			} else if (groupPosition == GroupIdEnum.ROOM_DESCRIPTION.ordinal()) {// "房间概况"
				childrenView = buildRoomDescriptionLayoutAndInitialize();
			} else if (groupPosition == GroupIdEnum.TRADING_RULE.ordinal()) {// "交易规则"
				childrenView = buildTradingRuleLayoutAndInitialize();
			} else {// "使用规则"
				childrenView = buildUseRuleLayoutAndInitialize();
			}
			
			return childrenView;
		}
		
		// 获取组在给定的位置编号，即groupTitleStrings中元素的ID
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		@Override
		public int getGroupCount() {
			return GroupIdEnum.values().length;
		}
		
		@Override
		public Object getGroup(int groupPosition) {
			final int groupTitleResId = (GroupIdEnum.values())[groupPosition].getGroupTitleResId();
			return RoomDetailOfOverviewActivity.this.getResources().getString(groupTitleResId);
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final LayoutInflater inflaterInstance = LayoutInflater.from(RoomDetailOfOverviewActivity.this);
			final View groupView = inflaterInstance.inflate(R.layout.expandable_list_group_for_room_description, null);
			
			if (groupPosition == 0) {
				groupView.setBackgroundResource(R.drawable.sss);
			} else if (groupPosition >= GroupIdEnum.values().length - 1) {
				groupView.setBackgroundResource(R.drawable.xxx);
			} else {
				groupView.setBackgroundResource(R.drawable.zzz);
			}
			final TextView groupTitleTextView = (TextView) groupView.findViewById(R.id.expandable_group_title_TextView);
			groupTitleTextView.setText(getGroup(groupPosition).toString());
			final ImageView groupIconImageView = (ImageView) groupView.findViewById(R.id.expandable_group_icon_ImageView);
			if (isExpanded) {
				groupIconImageView.setImageResource(R.drawable.js);
			} else {
				groupIconImageView.setImageResource(R.drawable.jx);
			}
			return groupView;
		}
		
		// Indicates whether the item ids are stable across changes to the underlying data.
		//
		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
