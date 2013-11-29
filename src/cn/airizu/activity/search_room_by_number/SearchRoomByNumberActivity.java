package cn.airizu.activity.search_room_by_number;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.activity.room_detail_basic_information.RoomDetailOfBasicInformationActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.toolutils.DebugLog;

public class SearchRoomByNumberActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private EditText roomNumberSearchEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_room_by_number_layout);
		
		// TitleBar
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setTitleByString(R.string.search_room_number);
		
		roomNumberSearchEditText = (EditText) findViewById(R.id.room_number_searche_EditText);
		
		final Button roomNumberSearchButton = (Button) findViewById(R.id.room_number_searche_Button);
		roomNumberSearchButton.setOnClickListener(buttonClickListener);
	}
	
	@Override
	protected void onDestroy() {
		DebugLog.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		DebugLog.i(TAG, "onPause");
		super.onPause();
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
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String errorMessag = "";
			do {
				String roomNumber = roomNumberSearchEditText.getText().toString();
				if (TextUtils.isEmpty(roomNumber)) {
					errorMessag = "请输入房间编号";
					break;
				}
				
				final Intent intent = new Intent(SearchRoomByNumberActivity.this, RoomDetailOfBasicInformationActivity.class);
				intent.putExtra(RoomDetailOfBasicInformationActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberSearchEditText.getText().toString());
				startActivity(intent);
				
				return;
			} while (false);
			
			Toast.makeText(SearchRoomByNumberActivity.this, errorMessag, Toast.LENGTH_SHORT).show();
		}
	};
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
}