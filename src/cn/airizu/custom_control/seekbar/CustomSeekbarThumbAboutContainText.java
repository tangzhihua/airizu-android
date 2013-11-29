package cn.airizu.custom_control.seekbar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

public class CustomSeekbarThumbAboutContainText extends BitmapDrawable {
	Paint mPaint;
	String mText;
	int num = 1;
	
	public CustomSeekbarThumbAboutContainText(Bitmap paramInt, int i) {
		super();
		this.num = i;
		this.mPaint = new Paint();
		this.mPaint.setColor(-16747593);
		this.mPaint.setTextSize(30.0F);
	}
	
	@Override
	public void draw(Canvas paramCanvas) {
		// TODO Auto-generated method stub
		super.draw(paramCanvas);
		
	}
	
}
