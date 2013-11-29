package cn.airizu.activity.main_navigation.tab_item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.activity.city_list.CityListActivity;
import cn.airizu.activity.search_room_by_number.SearchRoomByNumberActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.custom_control.calendar.CustomCalendar;
import cn.airizu.custom_control.calendar.DayCellForCustomCalendar;
import cn.airizu.custom_control.calendar.DayStyle;
import cn.airizu.custom_control.pop_list.RadioPopList;
import cn.airizu.domain.protocol.room_search.RoomSearchDatabaseFieldsConstant;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.StreamTools;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

public class B_SearchMainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private TextView cityNameTextView;
	private TextView occupancyCountTextView;
	private TextView priceDifferenceTextView;
	
	private View checkinView;
	private TextView checkinContentTextView;
	private TextView dayForCheckinDateTextView;
	private TextView monthForCheckinDateTextView;
	private TextView weekForCheckinDateTextView;
	
	private View checkoutView;
	private TextView checkoutContentTextView;
	private TextView dayForCheckoutDateTextView;
	private TextView monthForCheckoutDateTextView;
	private TextView weekForCheckoutDateTextView;
	
	private static enum IntentRequestCodeEnum {
		TO_CITY_LIST_ACTIVITY
	};
	
	private String cityName = StreamTools.toUTF8String("北京");
	private String cityId = "110100";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main_activity);
		
		final View selectCityLayout = findViewById(R.id.select_city_RelativeLayout);
		final View selectCheckinNumberLayout = findViewById(R.id.select_checkin_num_RelativeLayout);
		final View selectPriceLayout = findViewById(R.id.select_price_RelativeLayout);
		final Button searchButton = (Button) findViewById(R.id.search_Button);
		final Button searchRoomNumberButton = (Button) findViewById(R.id.search_roomNumber_Button);
		
		selectCityLayout.setOnClickListener(buttonClickListener);
		selectCheckinNumberLayout.setOnClickListener(buttonClickListener);
		selectPriceLayout.setOnClickListener(buttonClickListener);
		searchButton.setOnClickListener(buttonClickListener);
		searchRoomNumberButton.setOnClickListener(buttonClickListener);
		
		// "城市名称"
		cityNameTextView = (TextView) findViewById(R.id.select_city_TextView);
		cityNameTextView.setText(cityName);
		
		// "入住时间"
		checkinView = findViewById(R.id.checkin_date_layout);
		checkinView.setOnClickListener(buttonClickListener);
		checkinContentTextView = (TextView) findViewById(R.id.checkin_date_content_TextView);
		dayForCheckinDateTextView = (TextView) findViewById(R.id.checkin_date_day_TextView);
		monthForCheckinDateTextView = (TextView) findViewById(R.id.checkin_date_month_TextView);
		weekForCheckinDateTextView = (TextView) findViewById(R.id.checkin_date_week_TextView);
		// "退房时间"
		checkoutView = findViewById(R.id.checkout_date_layout);
		checkoutView.setOnClickListener(buttonClickListener);
		checkoutContentTextView = (TextView) findViewById(R.id.checkout_date_content_TextView);
		dayForCheckoutDateTextView = (TextView) findViewById(R.id.checkout_date_day_TextView);
		monthForCheckoutDateTextView = (TextView) findViewById(R.id.checkout_date_month_TextView);
		weekForCheckoutDateTextView = (TextView) findViewById(R.id.checkout_date_week_TextView);
		
		// "入住人数"
		occupancyCountTextView = (TextView) findViewById(R.id.select_checkin_num_TextView);
		// "价格区间"
		priceDifferenceTextView = (TextView) findViewById(R.id.select_price_TextView);
		
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		DebugLog.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
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
		
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		if (popupWindowForCanlendar != null) {
			popupWindowForCanlendar.dismiss();
		}
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DebugLog.i(TAG, "onKeyDown");
		// 跳转到 推荐首页
		ToolsFunctionForThisProgect.showHomePageForFirstLevelActivity(this);
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult");
		
		do {
			if (resultCode != RESULT_OK) {
				break;
			}
			
			if (requestCode == IntentRequestCodeEnum.TO_CITY_LIST_ACTIVITY.ordinal()) {
				if (data == null) {
					break;
				}
				if (!data.hasExtra("cityId") || !data.hasExtra("cityName")) {
					break;
				}
				cityId = data.getStringExtra("cityId");
				cityName = data.getStringExtra("cityName");
				cityNameTextView.setText(cityName);
			}
			
			// 一切正常
			return;
		} while (false);
		
		// 出现问题
		DebugLog.e(TAG, "onActivityResult has error ! ");
	}
	
	// 这个监听事件是判断用户点击的是哪个选项 3个 城市，入住人数，价格区间
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			switch (v.getId())
			{
				// 选择城市
				case R.id.select_city_RelativeLayout: {
					final Intent intent = new Intent(B_SearchMainActivity.this, CityListActivity.class);
					intent.putExtra(CityListActivity.TAG_SELECT_THE_CITY_AFTER_THE_OPERATION, CityListActivity.SelectTheCityAfterTheOperationEnum.BACK_TO_SEARCH_ACTIVITY);
					startActivityForResult(intent, IntentRequestCodeEnum.TO_CITY_LIST_ACTIVITY.ordinal());
				}
				break;
				
				// 选择入住人数
				case R.id.select_checkin_num_RelativeLayout: {
					final List<String> list = new ArrayList<String>();
					list.add("人数不限");
					list.addAll(GlobalConstant.dataSourceForOccupancyCountList.keySet());
					activateRadioPopWindow("选择入住人数", list, (View) occupancyCountTextView);
				}
				break;
				
				// 选择价格
				case R.id.select_price_RelativeLayout: {
					final List<String> list = new ArrayList<String>();
					list.add("价格不限");
					list.addAll(GlobalConstant.dataSourceForPriceDifferenceList.keySet());
					activateRadioPopWindow("选择价格", list, (View) priceDifferenceTextView);
				}
				break;
				
				// 搜索按钮
				case R.id.search_Button: {
					
					final Bundle data = new Bundle();
					data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name(), cityId);
					data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name(), cityName);
					
					final String checkinDate = checkinContentTextView.getText().toString();
					if (!TextUtils.isEmpty(checkinDate)) {
						if (!checkinDate.equals("不限")) {
							data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name(), checkinDate);
						}
					}
					final String checkoutDate = checkoutContentTextView.getText().toString();
					if (!TextUtils.isEmpty(checkoutDate)) {
						if (!checkoutDate.equals("不限")) {
							data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.checkoutDate.name(), checkoutDate);
						}
					}
					
					String key = "";
					String value = "";
					
					// 入住人数
					key = occupancyCountTextView.getText().toString();
					if (!key.equals("人数不限")) {
						value = GlobalConstant.dataSourceForOccupancyCountList.get(key);
						if (!TextUtils.isEmpty(value)) {
							data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.occupancyCount.name(), value);
						}
					}
					
					// 价格区间
					key = priceDifferenceTextView.getText().toString();
					if (!key.equals("价格不限")) {
						value = GlobalConstant.dataSourceForPriceDifferenceList.get(key);
						if (!TextUtils.isEmpty(value)) {
							data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name(), value);
						}
					}
					
					// 默认参数
					data.putString(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue());
					
					final Intent searchIntent = new Intent(B_SearchMainActivity.this, C_RoomListMainActivity.class);
					searchIntent.putExtra(C_RoomListMainActivity.IntentExtraTagEnum.ROOM_SEARCH_CRITERIA.name(), data);
					startActivity(searchIntent);
				}
				break;
				
				// 按房间号搜索
				case R.id.search_roomNumber_Button: {
					final Intent roomNumberIntent = new Intent(B_SearchMainActivity.this, SearchRoomByNumberActivity.class);
					startActivity(roomNumberIntent);
				}
				break;
				
				// "入住时间"
				case R.id.checkin_date_layout: {
					activatePopWindowForCanlendar("入住时间", checkinView);
				}
				break;
				
				// "退房时间"
				case R.id.checkout_date_layout: {
					activatePopWindowForCanlendar("退房时间", checkoutView);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// 单选列表控件
	private PopupWindow popupWindow;
	private RadioPopList radioPopList;
	
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
		RadioPopListAdapter radioPopListAdapter = new RadioPopListAdapter(this, dataSource);
		radioPopList.setAdapter(radioPopListAdapter);
		popupWindow.showAsDropDown(findViewById(R.id.mark_LinearLayout));
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
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// 日期控件
	private PopupWindow popupWindowForCanlendar;
	private CustomCalendar calendarView;
	
	private void activatePopWindowForCanlendar(String title, View triggerView) {
		if (popupWindowForCanlendar == null) {
			
			// 加载popupWindow的布局文件
			final int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
			final int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
			calendarView = new CustomCalendar(this, width, height);
			calendarView.setDelegate(calendarDelegate);
			
			// 声明一个弹出框
			popupWindowForCanlendar = new PopupWindow(calendarView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			// 下面这两句必须这个顺序--》点击外围关闭弹出
			popupWindowForCanlendar.setBackgroundDrawable(new BitmapDrawable());
			popupWindowForCanlendar.setOutsideTouchable(true);
			popupWindowForCanlendar.setAnimationStyle(R.style.PopupAnimation);
			popupWindowForCanlendar.setFocusable(true);
		}
		
		calendarView.setTitle(title);
		calendarView.setTag(triggerView);
		popupWindowForCanlendar.showAsDropDown(findViewById(R.id.mark_LinearLayout));
	}
	
	private CustomControlDelegate calendarDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			final CustomCalendar.ACTION actionEnum = (CustomCalendar.ACTION) actionTypeEnum;
			switch (actionEnum)
			{
				case ACTION_CANCEL_BUTTON_CLICK: {
					popupWindowForCanlendar.dismiss();
				}
				break;
				case ACTION_OK_BUTTON_CLICK: {
					popupWindowForCanlendar.dismiss();
				}
				break;
				case ACTION_DAY_CLICK: {
					final DayCellForCustomCalendar dayCell = (DayCellForCustomCalendar) contorl;
					final Calendar dateCalendar = dayCell.getDate();
					final String day = Integer.toString(dateCalendar.get(Calendar.DAY_OF_MONTH));
					final String month = Integer.toString(dateCalendar.get(Calendar.MARCH) + 1) + "月";// 要注意, 月份范围是 0~11
					final String week = DayStyle.vecStrWeekDayNames[dateCalendar.get(Calendar.DAY_OF_WEEK)];
					final View triggerView = (View) calendarView.getTag();
					final java.sql.Date date = new java.sql.Date(dayCell.getDate().getTimeInMillis());
					final String dateStringForSql = date.toString();
					
					if (triggerView.equals(checkinView)) {
						checkinContentTextView.setVisibility(View.GONE);
						checkinContentTextView.setText(dateStringForSql);
						
						dayForCheckinDateTextView.setText(day);
						monthForCheckinDateTextView.setText(month);
						weekForCheckinDateTextView.setText(week);
					} else {
						checkoutContentTextView.setVisibility(View.GONE);
						checkoutContentTextView.setText(dateStringForSql);
						
						dayForCheckoutDateTextView.setText(day);
						monthForCheckoutDateTextView.setText(month);
						weekForCheckoutDateTextView.setText(week);
					}
					popupWindowForCanlendar.dismiss();
				}
				break;
				default:
				break;
			}
		}
	};
	
}
