package cn.airizu.custom_control.titlebar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;

public class CustomTitleBar extends LinearLayout {

	private CustomControlDelegate delegate;

	public enum ActionEnum {
		//
		LEFT_BUTTON_CLICKED,
		//
		RIGHT_BUTTON_CLICKED
	}

	public static int INVISIBLE_BUTTON = -1;
	private TextView titleTextView;
	private ImageButton leftImageButton, rightImageButton;

	public CustomTitleBar(Context context) {
		super(context);
	}

	public CustomTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.titlebar_custom_control, this, true);

		leftImageButton = (ImageButton) findViewById(R.id.left_button_ImageButton);
		titleTextView = (TextView) findViewById(R.id.title_TextView);
		rightImageButton = (ImageButton) findViewById(R.id.right_button_ImageButton);

		leftImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (delegate != null) {
					delegate.customControlOnAction(CustomTitleBar.this, ActionEnum.LEFT_BUTTON_CLICKED);
				}
			}
		});
		rightImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (delegate != null) {
					delegate.customControlOnAction(CustomTitleBar.this, ActionEnum.RIGHT_BUTTON_CLICKED);
				}

			}
		});

		leftImageButton.setBackgroundResource(R.drawable.back);
		rightImageButton.setBackgroundResource(R.drawable.back);
		leftImageButton.setVisibility(View.INVISIBLE);
		rightImageButton.setVisibility(View.INVISIBLE);
	}

	public void setLeftButtonByImage(final int resid) {
		if (resid != INVISIBLE_BUTTON) {
			leftImageButton.setBackgroundResource(resid);
			leftImageButton.setVisibility(View.VISIBLE);
		} else {
			leftImageButton.setVisibility(View.INVISIBLE);
		}
	}

	public void setRightButtonByImage(final int resid) {
		if (resid != INVISIBLE_BUTTON) {
			rightImageButton.setBackgroundResource(resid);
			rightImageButton.setVisibility(View.VISIBLE);
		} else {
			rightImageButton.setVisibility(View.INVISIBLE);
		}
	}

	public void setTitleByString(int resid) {
		titleTextView.setBackgroundDrawable(null);
		titleTextView.setText(resid);
	}

	public void setTitleByString(String titleNameString) {
		titleTextView.setBackgroundDrawable(null);
		titleTextView.setText(titleNameString);
	}

	public void setTitleByImage(int resid) {
		titleTextView.setText("");
		titleTextView.setBackgroundResource(resid);
	}

	public void setDelegate(CustomControlDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
