package cn.airizu.activity.room_detail_photo;

import cn.airizu.activity.R;
import cn.airizu.activity.free_book_confirm_checkin_time.FreebookConfirmCheckinTimeActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RoomPhotoGalleryAdapter;
import cn.airizu.custom_control.page_change_light.PageChangeLight;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class RoomDetailPhotoActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 房间详情
		ROOM_DETAIL_BEAN,
		// 当前正在浏览的房间图片的索引
		CURRENTLY_BROWSING_PHOTO_INDEX
	};
	
	private int currentlyBrowsingPicturesIndex = 0;
	private RoomDetailNetRespondBean roomDetailNetRespondBean;
	
	// 
	private PageChangeLight pageChangeLight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		// 必须放在开始的位置
		if (parseIntent(getIntent())) {
			loadRealUIAndUseRoomDetailNetRespondBeanInitialize(roomDetailNetRespondBean);
		} else {
			loadErrorProcessUIAndInitialize();
		}
		
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
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			currentlyBrowsingPicturesIndex = intent.getIntExtra(IntentExtraTagEnum.CURRENTLY_BROWSING_PHOTO_INDEX.name(), 0);
			roomDetailNetRespondBean = (RoomDetailNetRespondBean) intent.getSerializableExtra(IntentExtraTagEnum.ROOM_DETAIL_BEAN.name());
			if (roomDetailNetRespondBean == null) {
				break;
			}
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void loadErrorProcessUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("房间图片");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("加载错误,请返回上一页...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void loadRealUIAndUseRoomDetailNetRespondBeanInitialize(RoomDetailNetRespondBean roomDetailNetRespondBean) {
		if (roomDetailNetRespondBean == null) {
			return;
		}
		
		setContentView(R.layout.room_detail_photo_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		String titleBarTitleString = "房间 : " + roomDetailNetRespondBean.getNumber();
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setRightButtonByImage(R.drawable.phone);
		titleBar.setDelegate(titleBarOnActionDelegate);
		//
		//
		pageChangeLight = (PageChangeLight) findViewById(R.id.page_change_light_PageChangeLight);
		pageChangeLight.setIndicatorCount(roomDetailNetRespondBean.getImageM().size());
		
		// 房间图片 "画廊"
		final Gallery roomPhotoGallery = (Gallery) findViewById(R.id.room_photo_Gallery);
		roomPhotoGallery.setSpacing(60);
		roomPhotoGallery.setSelection(currentlyBrowsingPicturesIndex);
		roomPhotoGallery.setAdapter(new RoomPhotoGalleryAdapter(this, roomDetailNetRespondBean.getImageM(), ScaleType.CENTER_INSIDE));
		roomPhotoGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pageChangeLight.setHightlightIndicator(position);
			}
		});
		
		// 免费预订
		final Button freebookButton = (Button) findViewById(R.id.freebook_Button);
		freebookButton.setOnClickListener(onClickListener);
		
		// 房间单价
		final TextView priceTextView = (TextView) findViewById(R.id.price_TextView);
		priceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(roomDetailNetRespondBean.getPrice()));
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			} else if (actionTypeEnum == CustomTitleBar.ActionEnum.RIGHT_BUTTON_CLICKED) {
				
			}
		}
	};
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (roomDetailNetRespondBean == null) {
				return;
			}
			
			switch (v.getId())
			{
				// 免费预订
				case R.id.freebook_Button: {
					final Intent intent = new Intent(RoomDetailPhotoActivity.this, FreebookConfirmCheckinTimeActivity.class);
					intent.putExtra(FreebookConfirmCheckinTimeActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomDetailNetRespondBean.getNumber());
					startActivity(intent);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
}
