package cn.airizu.custom_control.page_change_light;

import cn.airizu.activity.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageChangeLight extends LinearLayout {
	private int lightTotalNumber;
	private int iconImageID = R.drawable.page_change_light_forblue;
	private int iconFocusImageID = R.drawable.page_change_light_focus_forblue;
	
	public PageChangeLight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setIndicatorCount(int count) {
		this.lightTotalNumber = count;
		
		Context context = getContext();
		
		for (int i = 0; i < count; i++) {
			ImageView v = new ImageView(context);
			addView(v);
		}
	}
	
	public void setIndicatorImage(int iconImage, int iconFocusImage) {
		iconImageID = iconImage;
		iconFocusImageID = iconFocusImage;
	}
	
	public void setHightlightIndicator(int index) {
		for (int i = 0; i < lightTotalNumber; i++) {
			ImageView indicatorImageView = (ImageView) getChildAt(i);
			// 通过切换图片达到指示器的效果
			indicatorImageView.setImageResource((i == index) ? iconFocusImageID : iconImageID);
		}
	}
}
