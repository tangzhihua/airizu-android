package cn.airizu.custom_control.pop_list;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.toolutils.DebugLog;

public class RadioPopList extends LinearLayout {
	private final String TAG = this.getClass().getSimpleName();
	
	public enum ActionEnum {
		// 取消按钮被点击
		TITLE_LEFT_BUTTON_CLICK,
		// 确定按钮被点击
		TITLE_RIGHT_BUTTON_CLICK,
		// ITEM点击
		LIST_ITEM_CLICK
	}
	
	private CustomControlDelegate delegate;
	
	public void setTitle(String title) {
		TextView titleTextView = (TextView) findViewById(R.id.title_TextView);
		if (titleTextView != null) {
			titleTextView.setText(title);
		}
	}
	
	// 左按钮
	private View leftButton;
	// 右按钮
	private View rightButton;
	//
	private ListView listView;
	
	public RadioPopList(Context context) {
		super(context);
	}
	
	public RadioPopList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RadioPopList(Context context, CustomControlDelegate delegate) {
		super(context);
		
		this.delegate = delegate;
		
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.pop_list, this, true);
		
		leftButton = findViewById(R.id.left_Button);
		leftButton.setOnClickListener(buttonClickListener);
		
		rightButton = findViewById(R.id.right_Button);
		rightButton.setOnClickListener(buttonClickListener);
		
		listView = (ListView) findViewById(R.id.list_item_ListView);
		listView.setOnItemClickListener(listItemClickListener);
	}
	
	public void setAdapter(RadioPopListAdapter radioPopListAdapter) {
		if (radioPopListAdapter == null) {
			DebugLog.e(TAG, "radioPopListAdapter is null");
			return;
		}
		listView.setAdapter(radioPopListAdapter);
	}
	
	private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
		
		// parent : The AdapterView where the click happened.
		// view : The view within the AdapterView that was clicked (this will be a view provided by the adapter)
		// position : The position of the view in the adapter.
		// id : The row id of the item that was clicked.
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (delegate != null) {
				delegate.customControlOnAction(view, ActionEnum.LIST_ITEM_CLICK);
			}
		}
	};
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.left_Button: {
					if (delegate != null) {
						delegate.customControlOnAction(v, ActionEnum.TITLE_LEFT_BUTTON_CLICK);
					}
				}
					
				break;
				case R.id.right_Button: {
					if (delegate != null) {
						delegate.customControlOnAction(v, ActionEnum.TITLE_RIGHT_BUTTON_CLICK);
					}
				}
					
				break;
				default:
				break;
			}
		}
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
}
