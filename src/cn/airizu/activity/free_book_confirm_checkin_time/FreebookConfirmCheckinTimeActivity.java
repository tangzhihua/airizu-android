package cn.airizu.activity.free_book_confirm_checkin_time;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.free_book_confirm_order_info.FreebookConfirmOrderInfoActivity;
import cn.airizu.activity.logon.LogonActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.custom_control.calendar.CustomCalendar;
import cn.airizu.custom_control.calendar.DayCellForCustomCalendar;
import cn.airizu.custom_control.pop_list.RadioPopList;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRequestBean;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRespondBean;
import cn.airizu.domain.protocol.room_calendar.RoomCalendarNetRequestBean;
import cn.airizu.domain.protocol.room_calendar.RoomCalendarNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.global_data_cache.GlobalDataCacheForMemorySingleton;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

/**
 * 免费预订 - 确认入住时间界面
 * 
 * @author zhihua.tang
 */
public class FreebookConfirmCheckinTimeActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 房间编号
		ROOM_NUMBER
	};
	
	private String roomNumberString;
	
	private static enum NetRequestTagEnum {
		// 2.28 房间日历
		ROOM_CALENDAR,
		// 2.20 订单预订
		ORDER_FREEBOOK
	};
	
	private TextView checkinDateContentTextView;
	private TextView checkoutDateContentTextView;
	private TextView occupancyCountContentTextView;
	private TextView orderTotalPriceContentTextView;
	
	private View occupancyCountBodyLayout;
	private View orderTotalPriceBodyLayout;
	
	private final int INTENT_REQUEST_CODE_FOR_USER_LOGON = 0;
	
	private int netRequestIndexForRoomCalendar = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private RoomCalendarNetRespondBean roomCalendarNetRespondBean;
	private FreeBookNetRequestBean freeBookNetRequestBean;
	private FreeBookNetRespondBean freeBookNetRespondBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.free_book_confirm_checkin_time_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("免费预订");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 必须放在开始的位置
		if (!parseIntent(getIntent())) {
			return;
		}
		
		final View checkInDateView = findViewById(R.id.checkin_date_layout);
		checkInDateView.setOnClickListener(controlClickListener);
		checkinDateContentTextView = (TextView) findViewById(R.id.checkin_date_content_TextView);
		
		final View checkOutDateView = findViewById(R.id.checkout_date_layout);
		checkOutDateView.setOnClickListener(controlClickListener);
		checkoutDateContentTextView = (TextView) findViewById(R.id.checkout_date_content_TextView);
		
		final View occupancyCountView = findViewById(R.id.occupancy_count_layout);
		occupancyCountView.setOnClickListener(controlClickListener);
		occupancyCountContentTextView = (TextView) findViewById(R.id.occupancy_count_content_TextView);
		
		occupancyCountBodyLayout = findViewById(R.id.occupancy_count_body_layout);
		orderTotalPriceContentTextView = (TextView) findViewById(R.id.order_total_price_content_TextView);
		
		orderTotalPriceBodyLayout = findViewById(R.id.order_total_price_body_layout);
		
		final Button submitButton = (Button) findViewById(R.id.submit_Button);
		submitButton.setOnClickListener(controlClickListener);
		
		if (GlobalDataCacheForMemorySingleton.getInstance().getLogonNetRespondBean() == null) {
			// 用户未登录
			final Intent intent = new Intent(this, LogonActivity.class);
			startActivityForResult(intent, INTENT_REQUEST_CODE_FOR_USER_LOGON);
		} else {
			
		}
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
		
		SimpleProgressDialog.resetByThisContext(this);
		
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
		
		// 开始请求当前房间的房间日历
		requestRoomCalendar(roomNumberString);
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
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult");
		
		if (requestCode == INTENT_REQUEST_CODE_FOR_USER_LOGON) {
			if (resultCode == RESULT_OK) {
				// 用户已经登录成功
			} else {
				// 用户未登录
				// showDialog(NEED_LOGIN_DIALOG_ID);
				finish();
			}
		}
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				DebugLog.e(TAG, "intent is null ! ");
				break;
			}
			
			roomNumberString = intent.getStringExtra(IntentExtraTagEnum.ROOM_NUMBER.name());
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void requestRoomCalendar(final String roomNumberString) {
		do {
			
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			
			if (roomCalendarNetRespondBean != null) {
				break;
			}
			if (netRequestIndexForRoomCalendar != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(FreebookConfirmCheckinTimeActivity.this, progressDialogOnCancelListener);
				break;
			}
			RoomCalendarNetRequestBean roomCalendarNetRequestBean = new RoomCalendarNetRequestBean(roomNumberString);
			netRequestIndexForRoomCalendar = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, roomCalendarNetRequestBean, NetRequestTagEnum.ROOM_CALENDAR,
					domainNetRespondCallback);
			if (netRequestIndexForRoomCalendar != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(FreebookConfirmCheckinTimeActivity.this, progressDialogOnCancelListener);
			}
		} while (false);
		
	}
	
	private void clearAllWithLastFreebookInfo() {
		freeBookNetRespondBean = null;
		
		orderTotalPriceBodyLayout.setVisibility(View.GONE);
		occupancyCountBodyLayout.setBackgroundResource(R.drawable.bg_3);
		occupancyCountBodyLayout.invalidate();
		orderTotalPriceContentTextView.setText("");
	}
	
	private void requestLatestRoomPrice(final String roomNumberString) {
		
		String errorMessageString = "";
		
		do {
			
			DomainProtocolNetHelperSingleton.getInstance().cancelNetRequestByRequestIndex(netRequestIndexForFreebook);
			
			//
			clearAllWithLastFreebookInfo();
			
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			final String checkInDate = checkinDateContentTextView.getText().toString();
			final String checkOutDate = checkoutDateContentTextView.getText().toString();
			if (TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkOutDate)) {
				// 异常, 入住时间和退房时间所在的TextView的值不应该为空
				break;
			}
			if (checkInDate.equals(getResources().getString(R.string.please_select_the_checkin_time)) || checkOutDate.equals(getResources().getString(R.string.please_select_the_checkout_time))) {
				// 数据无效
				break;
			}
			if (!ToolsFunctionForThisProgect.DateCompareEnum.RIGHT_IS_GREATER_THAN_LEFT.equals(ToolsFunctionForThisProgect.dateCompare(checkInDate, checkOutDate))) {
				errorMessageString = "退房时间应该晚于入住时间";
				break;
			}
			final String mapKeyForOccupancyCount = occupancyCountContentTextView.getText().toString();
			if (TextUtils.isEmpty(mapKeyForOccupancyCount)) {
				break;
			}
			final String guestNum = GlobalConstant.dataSourceForOccupancyCountList.get(mapKeyForOccupancyCount);
			if (TextUtils.isEmpty(guestNum)) {
				break;
			}
			
			final FreeBookNetRequestBean.Builder freeBookNetRequestBeanBuilder = new FreeBookNetRequestBean.Builder(roomNumberString, checkInDate, checkOutDate,
					GlobalConstant.VoucherMethodEnum.DO_NOT_USE_PROMOTIONS.ordinal(), guestNum);
			freeBookNetRequestBean = freeBookNetRequestBeanBuilder.builder();
			netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, freeBookNetRequestBean, NetRequestTagEnum.ORDER_FREEBOOK, domainNetRespondCallback);
			if (netRequestIndexForFreebook != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(FreebookConfirmCheckinTimeActivity.this, progressDialogOnCancelListener);
			}
			
			return;
		} while (false);
		
		if (!TextUtils.isEmpty(errorMessageString)) {
			Toast.makeText(this, errorMessageString, 0).show();
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
	
	private View.OnClickListener controlClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				// 入住时间
				case R.id.checkin_date_layout: {
					activatePopWindowForCanlendar("入住时间", checkinDateContentTextView);
				}
				break;
				
				// 退房时间
				case R.id.checkout_date_layout: {
					activatePopWindowForCanlendar("退房时间", checkoutDateContentTextView);
				}
				break;
				
				// 入住人数
				case R.id.occupancy_count_layout: {
					List<String> list = new ArrayList<String>(GlobalConstant.dataSourceForOccupancyCountList.keySet());
					activateRadioPopWindow("入住人数", list, occupancyCountContentTextView);
				}
				break;
				
				// 提交 按钮
				case R.id.submit_Button: {
					// TODO : 未来应在这里进行当前订单是否有效的判断, 目前没有接口
					gotoConfirmOrderInfoActivity();
				}
				default:
				break;
			}
		}
	};
	
	private void gotoConfirmOrderInfoActivity() {
		String errorMessageString = "";
		
		do {
			final String mapKeyForOccupancyCount = occupancyCountContentTextView.getText().toString();
			if (TextUtils.isEmpty(mapKeyForOccupancyCount)) {
				// 异常
				return;
			}
			final String guestNum = GlobalConstant.dataSourceForOccupancyCountList.get(mapKeyForOccupancyCount);
			if (TextUtils.isEmpty(guestNum)) {
				// 异常
				return;
			}
			
			final String checkInDate = checkinDateContentTextView.getText().toString();
			final String checkOutDate = checkoutDateContentTextView.getText().toString();
			if (TextUtils.isEmpty(checkInDate) || checkInDate.equals(getResources().getString(R.string.please_select_the_checkin_time))) {
				errorMessageString = "请选择入住时间";
				break;
			}
			if (TextUtils.isEmpty(checkOutDate) || checkOutDate.equals(getResources().getString(R.string.please_select_the_checkout_time))) {
				errorMessageString = "请选择退房时间";
				break;
			}
			if (!ToolsFunctionForThisProgect.DateCompareEnum.RIGHT_IS_GREATER_THAN_LEFT.equals(ToolsFunctionForThisProgect.dateCompare(checkInDate, checkOutDate))) {
				errorMessageString = "退房时间应该晚于入住时间";
				break;
			}
			
			final Intent intent = new Intent(this, FreebookConfirmOrderInfoActivity.class);
			intent.putExtra(FreebookConfirmOrderInfoActivity.IntentExtraTagMenu.FREEBOOK_NET_REQUEST_BEAN.name(), freeBookNetRequestBean);
			if (freeBookNetRespondBean != null) {
				intent.putExtra(FreebookConfirmOrderInfoActivity.IntentExtraTagMenu.FREEBOOK_NET_RESPOND_BEAN.name(), freeBookNetRespondBean);
			}
			startActivity(intent);
			return;
		} while (false);
		
		Toast.makeText(this, errorMessageString, 0).show();
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI_FOR_ORDER_TOTAL_PRICE
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private void showOrderTotalPriceFromFreebookInfo(String orderTotalPriceString) {
		occupancyCountBodyLayout.setBackgroundResource(R.drawable.bg_1);
		orderTotalPriceBodyLayout.setVisibility(View.VISIBLE);
		orderTotalPriceContentTextView.setText(orderTotalPriceString);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//
			SimpleProgressDialog.dismiss(FreebookConfirmCheckinTimeActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				Toast.makeText(FreebookConfirmCheckinTimeActivity.this, netErrorMessage, Toast.LENGTH_SHORT).show();
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI_FOR_ORDER_TOTAL_PRICE.ordinal()) {
				final String orderTotalPriceString = msg.getData().getString(HandlerMsgTypeEnum.REFRESH_UI_FOR_ORDER_TOTAL_PRICE.name());
				showOrderTotalPriceFromFreebookInfo(orderTotalPriceString);
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ROOM_CALENDAR == requestEvent) {
			freeBookNetRespondBean = null;
			netRequestIndexForRoomCalendar = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {
			netRequestIndexForFreebook = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (NetRequestTagEnum.ROOM_CALENDAR == requestEvent) {// 2.28 房间日历
				roomCalendarNetRespondBean = (RoomCalendarNetRespondBean) respondDomainBean;
				handler.sendEmptyMessage(GlobalConstant.HANDLER_EMPTY_MESSAGE_WHAT);
				
			} else if (NetRequestTagEnum.ORDER_FREEBOOK == requestEvent) {// 2.20 订单预订
			
				freeBookNetRespondBean = (FreeBookNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, freeBookNetRespondBean.toString());
				
				final int totalPriceInteger = (int) freeBookNetRespondBean.getTotalPrice();
				final String orderTotalPriceString = Integer.valueOf(totalPriceInteger).toString();
				
				if (!TextUtils.isEmpty(orderTotalPriceString)) {
					final Message msg = new Message();
					msg.what = HandlerMsgTypeEnum.REFRESH_UI_FOR_ORDER_TOTAL_PRICE.ordinal();
					msg.getData().putString(HandlerMsgTypeEnum.REFRESH_UI_FOR_ORDER_TOTAL_PRICE.name(), getResources().getString(R.string.money_mark) + orderTotalPriceString);
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(GlobalConstant.HANDLER_EMPTY_MESSAGE_WHAT);
				}
			}
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
		}
	};
	
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
		RadioPopListAdapter radioPopListAdapter = new RadioPopListAdapter(this, dataSource);
		radioPopList.setAdapter(radioPopListAdapter);
		popupWindow.showAsDropDown(findViewById(R.id.title_bar));
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
						
						//
						requestLatestRoomPrice(roomNumberString);
					} while (false);
					popupWindow.dismiss();
				}
				break;
				default:
				break;
			}
		}
	};
	
	private PopupWindow popupWindowForCanlendar;
	private CustomCalendar calendarView;
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
					// Calendar dateCalendar = dayCell.getDate();
					final View triggerView = (View) calendarView.getTag();
					final java.sql.Date date = new java.sql.Date(dayCell.getDate().getTimeInMillis());
					final String dateStringForSql = date.toString();
					
					if (triggerView.equals(checkinDateContentTextView)) {
						checkinDateContentTextView.setText(dateStringForSql);
					} else {
						checkoutDateContentTextView.setText(dateStringForSql);
					}
					
					//
					requestLatestRoomPrice(roomNumberString);
					
					popupWindowForCanlendar.dismiss();
				}
				break;
				default:
				break;
			}
		}
	};
	
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
		popupWindowForCanlendar.showAsDropDown(findViewById(R.id.title_bar));
	}
}
