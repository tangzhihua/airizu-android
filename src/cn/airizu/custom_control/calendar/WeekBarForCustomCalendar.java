package cn.airizu.custom_control.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

/**
 * 日期控件中的星期条控件
 * 
 * @author zhihua.tang
 */
public class WeekBarForCustomCalendar extends View {
	private Paint paint = new Paint();
	private RectF backgroudRect = new RectF();
	private int iWeekDay = -1;
	
	public WeekBarForCustomCalendar(Context context) {
		super(context);
	}
	
	public WeekBarForCustomCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public WeekBarForCustomCalendar(Context context, int layoutWidth, int layoutHeight) {
		super(context);
		setLayoutParams(new LayoutParams(layoutWidth, layoutHeight));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 设置矩形大小
		backgroudRect.set(0, 0, this.getWidth(), this.getHeight());
		backgroudRect.inset(1, 1);
		
		// 绘制日历头部
		drawDayHeader(canvas);
	}
	
	private void drawDayHeader(Canvas canvas) {
		// 画矩形，并设置矩形画笔的颜色
		paint.setColor(CustomCalendar.Calendar_WeekBgColor);
		canvas.drawRect(backgroudRect, paint);
		
		// 写入日历头部，设置画笔参数
		paint.setTypeface(null);
		paint.setTextSize(22);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setColor(CustomCalendar.Calendar_WeekFontColor);
		
		// draw week name
		final String weekName = DayStyle.getWeekName(iWeekDay);
		final int iPosX = (int) backgroudRect.left + ((int) backgroudRect.width() >> 1) - ((int) paint.measureText(weekName) >> 1);
		final int iPosY = (int) (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - paint.getFontMetrics().bottom);
		canvas.drawText(weekName, iPosX, iPosY, paint);
	}
	
	// 得到字体高度
	private int getTextHeight() {
		return (int) (-paint.ascent() + paint.descent());
	}
	
	// 得到一星期的第几天的文本标记
	public void setData(int iWeekDay) {
		this.iWeekDay = iWeekDay;
	}
}