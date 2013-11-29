package cn.airizu.custom_control.search_title;

import cn.airizu.activity.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomSearchTitle extends LinearLayout {
	private ImageView checkinDateImageView , checkoutDateImageView ;
	private TextView checkinDateTextView , checkoutDateTextView , roomAmountTextView ;
	public CustomSearchTitle(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomSearchTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.check_data_bar_custom_control, this, true);
	}

}
