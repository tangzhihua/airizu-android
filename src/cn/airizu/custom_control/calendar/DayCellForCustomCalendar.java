package cn.airizu.custom_control.calendar;

import java.util.Calendar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

/**
 * 日期控件中的 单个日期盘控件
 * 
 * @author zhihua.tang
 */
public class DayCellForCustomCalendar extends View {
	private final String TAG = this.getClass().getSimpleName();
	
	// 基本元素
	private OnItemClick itemClick = null;
	private Paint paint = new Paint();
	private RectF backgroundRect = new RectF();
	private String dayString = "";
	
	// 当前日期
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;
	private int iDateDayOfWeek = 0;
	
	// 布尔变量
	private boolean isSelected;
	private boolean isActiveMonth;
	private boolean isToday;
	private boolean bTouchedDown;
	private boolean isHoliday;
	private boolean isHasRecord;
	
	public static int ANIM_ALPHA_DURATION = 100;
	
	public interface OnItemClick {
		public void OnClick(DayCellForCustomCalendar item);
	}
	
	// 构造函数
	public DayCellForCustomCalendar(Context context, int layoutWidth, int layoutHeight) {
		super(context);
		setFocusable(true);
		setLayoutParams(new LayoutParams(layoutWidth, layoutHeight));
	}
	
	// 取变量值
	public Calendar getDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.DAY_OF_WEEK, iDateDayOfWeek);
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);// 要注意, 月份范围是 0~11
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}
	
	// 设置变量值
	public void setData(int iYear, int iMonth, int iDay, int iDayOfWeek, boolean isToday, boolean isHoliday, int iActiveMonth, boolean isHasRecord) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;
		iDateDayOfWeek = iDayOfWeek;
		
		this.dayString = Integer.toString(iDateDay);
		this.isActiveMonth = (iDateMonth == iActiveMonth);
		this.isToday = isToday;
		this.isHoliday = isHoliday;
		this.isHasRecord = isHasRecord;
	}
	
	// 重载绘制方法
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		backgroundRect.set(0, 0, this.getWidth(), this.getHeight());
		backgroundRect.inset(1, 1);
		
		final boolean bFocused = IsViewFocused();
		
		drawDayBackgroundView(canvas, bFocused);
		drawDayNumber(canvas);
	}
	
	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}
	
	// 绘制日历方格
	private void drawDayBackgroundView(Canvas canvas, boolean bFocused) {
		
		if (isSelected || bFocused) {
			LinearGradient lGradBkg = null;
			
			if (bFocused) {
				lGradBkg = new LinearGradient(backgroundRect.left, 0, backgroundRect.right, 0, 0xffaa5500, 0xffffddbb, Shader.TileMode.CLAMP);
			}
			
			if (isSelected) {
				lGradBkg = new LinearGradient(backgroundRect.left, 0, backgroundRect.right, 0, 0xff225599, 0xffbbddff, Shader.TileMode.CLAMP);
			}
			
			if (lGradBkg != null) {
				paint.setShader(lGradBkg);
				canvas.drawRect(backgroundRect, paint);
			}
			
			paint.setShader(null);
			
		} else {
			paint.setColor(getColorBkg(isHoliday, isToday));
			canvas.drawRect(backgroundRect, paint);
		}
		
		if (isHasRecord) {
			CreateReminder(canvas, CustomCalendar.special_Reminder);
		}
		// else if (!hasRecord && !bToday && !bSelected) {
		// CreateReminder(canvas, Calendar_TestActivity.Calendar_DayBgColor);
		// }
	}
	
	// 绘制日历中的数字
	public void drawDayNumber(Canvas canvas) {
		// draw day number
		paint.setTypeface(null);
		paint.setAntiAlias(true);
		paint.setShader(null);
		paint.setFakeBoldText(true);
		paint.setTextSize(28);
		paint.setColor(CustomCalendar.isPresentMonth_FontColor);
		paint.setUnderlineText(false);
		
		if (!isActiveMonth)
			paint.setColor(CustomCalendar.unPresentMonth_FontColor);
		
		if (isToday)
			paint.setUnderlineText(true);
		
		final int iPosX = (int) backgroundRect.left + ((int) backgroundRect.width() >> 1) - ((int) paint.measureText(dayString) >> 1);
		
		final int iPosY = (int) (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - paint.getFontMetrics().bottom);
		
		canvas.drawText(dayString, iPosX, iPosY, paint);
		
		paint.setUnderlineText(false);
	}
	
	// 得到字体高度
	private int getTextHeight() {
		return (int) (-paint.ascent() + paint.descent());
	}
	
	// 根据条件返回不同颜色值
	public static int getColorBkg(boolean bHoliday, boolean bToday) {
		if (bToday)
			return CustomCalendar.isToday_BgColor;
		// if (bHoliday) //如需周末有特殊背景色，可去掉注释
		// return Calendar_TestActivity.isHoliday_BgColor;
		return CustomCalendar.Calendar_DayBgColor;
	}
	
	// 设置是否被选中
	@Override
	public void setSelected(boolean bEnable) {
		if (this.isSelected != bEnable) {
			this.isSelected = bEnable;
			this.invalidate();
		}
	}
	
	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}
	
	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}
	
	// 点击事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(DayCellForCustomCalendar.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		return bHandled;
	}
	
	// 点击事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}
	
	// 不透明度渐变
	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}
	
	public void CreateReminder(Canvas canvas, int Color) {
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color);
		Path path = new Path();
		path.moveTo(backgroundRect.right - backgroundRect.width() / 4, backgroundRect.top);
		path.lineTo(backgroundRect.right, backgroundRect.top);
		path.lineTo(backgroundRect.right, backgroundRect.top + backgroundRect.width() / 4);
		path.lineTo(backgroundRect.right - backgroundRect.width() / 4, backgroundRect.top);
		path.close();
		canvas.drawPath(path, paint);
	}
}