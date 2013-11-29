package cn.airizu.custom_control.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;

/**
 * 自定义的日期控件
 * 
 * @author zhihua.tang
 */
public class CustomCalendar extends LinearLayout {
	
	public enum ACTION {
		// 取消按钮被点击
		ACTION_CANCEL_BUTTON_CLICK,
		// 确定按钮被点击
		ACTION_OK_BUTTON_CLICK,
		// 日期被点击
		ACTION_DAY_CLICK
	}
	
	private CustomControlDelegate delegate;
	
	public void setDelegate(CustomControlDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void setTitle(String title) {
		TextView titleTextView = (TextView) findViewById(R.id.title_TextView);
		if (titleTextView != null) {
			titleTextView.setText(title);
		}
	}
	
	// 星期条和日期所在的布局
	private LinearLayout weekbarAndDayLayout;
	private ArrayList<DayCellForCustomCalendar> days = new ArrayList<DayCellForCustomCalendar>();
	
	// 日期变量
	public static Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	
	// 当前操作日期
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private int iFirstDayOfWeek = Calendar.MONDAY;
	
	// private int Calendar_Width = 0;
	private int calendarCellWidth = 0;
	
	// 页面控件
	
	// 整个日期控件的根布局
	private LinearLayout rootLayout = null;
	private LinearLayout calendarBodyLayout = null;
	
	// 关闭按钮
	private View closeCalendarButton;
	// 确认按钮
	private View selectCalendarButton;
	// //////////////////////////////////////////////
	// 当前日期
	private TextView currentlyDateTextView = null;
	// "上一个月" 的触发按钮
	private View lastMonthButton = null;
	// "下一个月" 的触发按钮
	private View nextMonthButton = null;
	private TextView arrange_text = null;
	private LinearLayout arrange_layout = null;
	
	// 数据源
	private ArrayList<String> Calendar_Source = null;
	private Hashtable<Integer, Integer> calendar_Hashtable = new Hashtable<Integer, Integer>();
	private Boolean[] flag = null;
	private Calendar startDate = null;
	private Calendar endDate = null;
	private int dayvalue = -1;
	
	//
	public static int Calendar_WeekBgColor = 0;
	public static int Calendar_DayBgColor = 0;
	public static int isHoliday_BgColor = 0;
	public static int unPresentMonth_FontColor = 0;
	public static int isPresentMonth_FontColor = 0;
	public static int isToday_BgColor = 0;
	public static int special_Reminder = 0;
	public static int common_Reminder = 0;
	public static int Calendar_WeekFontColor = 0;
	
	public CustomCalendar(Context context) {
		super(context);
	}
	
	public CustomCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomCalendar(Context context, final int layoutWidth, final int layoutHeight) {
		super(context);
		// setLayoutParams(new LayoutParams(layoutWidth, layoutHeight));
		
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.calendar_custom_control, this, true);
		rootLayout.setBackgroundColor(Color.WHITE);
		// 获得屏幕宽和高，并計算出屏幕寬度分七等份的大小
		calendarCellWidth = layoutWidth / 7 + 1;
		
		closeCalendarButton = findViewById(R.id.left_Button);
		closeCalendarButton.setOnClickListener(buttonClickListener);
		selectCalendarButton = findViewById(R.id.right_Button);
		selectCalendarButton.setOnClickListener(buttonClickListener);
		
		// 日期控件控制工具栏("上个月按钮" <--> "当前日期" <--> "下个月按钮")
		currentlyDateTextView = (TextView) findViewById(R.id.currently_date_TextView);
		lastMonthButton = findViewById(R.id.last_month_layout);
		lastMonthButton.setOnClickListener(lastMonthButtonClickListener);
		nextMonthButton = findViewById(R.id.next_month_layout);
		nextMonthButton.setOnClickListener(nextMonthButtonClickListener);
		
		// 计算本月日历中的第一天(一般是上月的某天)，并更新日历
		calStartDate = getCalendarStartDate();
		
		// 日历控件 主布局
		calendarBodyLayout = (LinearLayout) findViewById(R.id.body_layout);
		calendarBodyLayout.addView(buildWeekbarAndDayDiscView(context));
		
		DayCellForCustomCalendar daySelected = updateCalendar();
		
		if (daySelected != null) {
			daySelected.requestFocus();
		}
		
		startDate = GetStartDate();
		calToday = GetTodayDate();
		endDate = GetEndDate(startDate);
		
		// 线性布局(垂直)
		// TODO: 不知道这里的代码有何作用
		arrange_layout = createLinearLayout(context, LinearLayout.VERTICAL);
		arrange_layout.setPadding(5, 2, 0, 0);
		
		arrange_text = new TextView(context);
		arrange_text.setTextColor(Color.BLACK);
		arrange_text.setTextSize(18);
		arrange_layout.addView(arrange_text);
		
		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(arrange_layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		rootLayout.addView(scrollView);
		
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				int day = GetNumFromDate(calToday, startDate);
				
				if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
					dayvalue = calendar_Hashtable.get(day);
				}
			}
			
		}.start();
		
		Calendar_WeekBgColor = this.getResources().getColor(R.color.Calendar_WeekBgColor);
		Calendar_DayBgColor = this.getResources().getColor(R.color.Calendar_DayBgColor);
		isHoliday_BgColor = this.getResources().getColor(R.color.isHoliday_BgColor);
		unPresentMonth_FontColor = this.getResources().getColor(R.color.unPresentMonth_FontColor);
		isPresentMonth_FontColor = this.getResources().getColor(R.color.isPresentMonth_FontColor);
		isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
		special_Reminder = this.getResources().getColor(R.color.specialReminder);
		common_Reminder = this.getResources().getColor(R.color.commonReminder);
		Calendar_WeekFontColor = this.getResources().getColor(R.color.Calendar_WeekFontColor);
		
	}
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.left_Button: {
					if (delegate != null) {
						delegate.customControlOnAction(v, ACTION.ACTION_CANCEL_BUTTON_CLICK);
					}
				}
					
				break;
				case R.id.right_Button: {
					if (delegate != null) {
						delegate.customControlOnAction(v, ACTION.ACTION_OK_BUTTON_CLICK);
					}
				}
					
				break;
				default:
				break;
			}
		}
	};
	
	protected String GetDateShortString(Calendar date) {
		String returnString = date.get(Calendar.YEAR) + "/";
		returnString += date.get(Calendar.MONTH) + 1 + "/";
		returnString += date.get(Calendar.DAY_OF_MONTH);
		
		return returnString;
	}
	
	// 得到当天在日历中的序号
	private int GetNumFromDate(Calendar now, Calendar returnDate) {
		Calendar cNow = (Calendar) now.clone();
		Calendar cReturnDate = (Calendar) returnDate.clone();
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);
		
		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		int index = millisecondsToDays(intervalMs);
		
		return index;
	}
	
	private int millisecondsToDays(long intervalMs) {
		return Math.round((intervalMs / (1000 * 86400)));
	}
	
	private void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	// 生成布局
	private LinearLayout createLinearLayout(Context context, int iOrientation) {
		LinearLayout lay = new LinearLayout(context);
		lay.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		
		return lay;
	}
	
	// 生成星期条
	private View buildWeekbarView(Context context) {
		LinearLayout layRow = createLinearLayout(context, LinearLayout.HORIZONTAL);
		layRow.setBackgroundColor(Color.argb(255, 207, 207, 205));
		
		for (int weekKey = 0; weekKey < 7; weekKey++) {
			WeekBarForCustomCalendar day = new WeekBarForCustomCalendar(context, calendarCellWidth, 35);
			
			final int iWeekDay = DayStyle.getWeekKeyAfterAdjust(weekKey, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}
		
		return layRow;
	}
	
	// 生成日期盘中的一行，仅画矩形
	private View buildADayBarForDayDisc(Context context) {
		LinearLayout dayBarLayout = createLinearLayout(context, LinearLayout.HORIZONTAL);
		
		for (int iDay = 0; iDay < 7; iDay++) {
			DayCellForCustomCalendar dayCell = new DayCellForCustomCalendar(context, calendarCellWidth, calendarCellWidth);
			dayCell.setItemClick(dayCellClickListener);
			days.add(dayCell);
			dayBarLayout.addView(dayCell);
		}
		
		return dayBarLayout;
	}
	
	// 生成日历 星期条和日期
	private View buildWeekbarAndDayDiscView(Context context) {
		weekbarAndDayLayout = createLinearLayout(context, LinearLayout.VERTICAL);
		weekbarAndDayLayout.setBackgroundColor(Color.WHITE);
		weekbarAndDayLayout.addView(buildWeekbarView(context));
		days.clear();
		
		for (int i = 0; i < 5; i++) {
			weekbarAndDayLayout.addView(buildADayBarForDayDisc(context));
		}
		
		return weekbarAndDayLayout;
	}
	
	// 设置当天日期和被选中日期
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);
		
		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}
		
		//
		UpdateStartDateForMonth();
		return calStartDate;
	}
	
	// 由于本日历上的日期都是从周一开始的，此方法可推算出上月在本月日历中显示的天数
	private void UpdateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		// update days for week
		UpdateCurrentMonthDisplay();
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0) {
				iDay = 6;
			}
		}
		
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0) {
				iDay = 6;
			}
		}
		
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}
	
	// 更新日历
	private DayCellForCustomCalendar updateCalendar() {
		DayCellForCustomCalendar daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		
		for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);// 要注意, 月份范围是 0~11
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DayCellForCustomCalendar dayCell = days.get(i);
			
			// 判断是否当天
			boolean bToday = false;
			
			if (calToday.get(Calendar.YEAR) == iYear) {
				if (calToday.get(Calendar.MONTH) == iMonth) {
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay) {
						bToday = true;
					}
				}
			}
			
			// check holiday
			boolean isHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY) || (iDayOfWeek == Calendar.SUNDAY)) {
				isHoliday = true;
			}
			if ((iMonth == Calendar.JANUARY) && (iDay == 1)) {
				isHoliday = true;
			}
			
			// 是否被选中
			bSelected = false;
			
			if (bIsSelection) {
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth) && (iSelectedYear == iYear)) {
					bSelected = true;
				}
			}
			
			dayCell.setSelected(bSelected);
			
			// 是否有记录
			boolean hasRecord = false;
			
			if (flag != null && flag[i] == true && calendar_Hashtable != null && calendar_Hashtable.containsKey(i)) {
				// hasRecord = flag[i];
				hasRecord = Calendar_Source.get(calendar_Hashtable.get(i)).contains("TTTTTTTTTTT");
			}
			
			if (bSelected) {
				daySelected = dayCell;
			}
			
			dayCell.setData(iYear, iMonth, iDay, iDayOfWeek, bToday, isHoliday, iMonthViewCurrentMonth, hasRecord);
			
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		weekbarAndDayLayout.invalidate();
		
		return daySelected;
	}
	
	// 更新日历标题上显示的年月
	private void UpdateCurrentMonthDisplay() {
		// 要注意, 月份范围是 0~11
		String date = calStartDate.get(Calendar.YEAR) + "年" + (calStartDate.get(Calendar.MONTH) + 1) + "月";
		currentlyDateTextView.setText(date);
	}
	
	// "显示上个月日历" 按钮
	private View.OnClickListener lastMonthButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			arrange_text.setText("");
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth--;
			
			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}
			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.HOUR_OF_DAY, 0);
			calStartDate.set(Calendar.MINUTE, 0);
			calStartDate.set(Calendar.SECOND, 0);
			calStartDate.set(Calendar.MILLISECOND, 0);
			UpdateStartDateForMonth();
			
			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			
			// 新建线程
			new Thread() {
				@Override
				public void run() {
					
					int day = GetNumFromDate(calToday, startDate);
					
					if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();
			
			updateCalendar();
		}
		
	};
	
	// "显示下个月日历" 按钮
	private View.OnClickListener nextMonthButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			arrange_text.setText("");
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth++;
			
			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}
			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateStartDateForMonth();
			
			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			
			// 新建线程
			new Thread() {
				@Override
				public void run() {
					int day = 5;
					
					if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();
			
			updateCalendar();
		}
	};
	
	// 监听点击日历中的日期控件
	private DayCellForCustomCalendar.OnItemClick dayCellClickListener = new DayCellForCustomCalendar.OnItemClick() {
		public void OnClick(DayCellForCustomCalendar cell) {
			calSelected.setTimeInMillis(cell.getDate().getTimeInMillis());
			updateCalendar();
			cell.setSelected(true);
			
			if (delegate != null) {
				delegate.customControlOnAction(cell, ACTION.ACTION_DAY_CLICK);
			}
			// int day = GetNumFromDate(calSelected, startDate);
			// if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
			// arrange_text.setText(Calendar_Source.get(calendar_Hashtable.get(day)));
			// } else {
			// arrange_text.setText("暂无数据记录");
			// }
			
		}
	};
	
	/**
	 * 获取今天的日期
	 * 
	 * @return
	 */
	public Calendar GetTodayDate() {
		Calendar cal_Today = Calendar.getInstance();
		cal_Today.set(Calendar.HOUR_OF_DAY, 0);
		cal_Today.set(Calendar.MINUTE, 0);
		cal_Today.set(Calendar.SECOND, 0);
		cal_Today.setFirstDayOfWeek(Calendar.MONDAY);
		
		return cal_Today;
	}
	
	// 得到当前日历中的第一天
	public Calendar GetStartDate() {
		int iDay = 0;
		Calendar cal_Now = Calendar.getInstance();
		cal_Now.set(Calendar.DAY_OF_MONTH, 1);
		cal_Now.set(Calendar.HOUR_OF_DAY, 0);
		cal_Now.set(Calendar.MINUTE, 0);
		cal_Now.set(Calendar.SECOND, 0);
		cal_Now.setFirstDayOfWeek(Calendar.MONDAY);
		
		iDay = cal_Now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		
		if (iDay < 0) {
			iDay = 6;
		}
		
		cal_Now.add(Calendar.DAY_OF_WEEK, -iDay);
		
		return cal_Now;
	}
	
	public Calendar GetEndDate(Calendar startDate) {
		// Calendar end = GetStartDate(enddate);
		Calendar endDate = Calendar.getInstance();
		endDate = (Calendar) startDate.clone();
		endDate.add(Calendar.DAY_OF_MONTH, 41);
		return endDate;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
}
