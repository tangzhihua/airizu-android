package cn.airizu.activity.user_information;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RadioPopListAdapter;
import cn.airizu.custom_control.pop_list.RadioPopList;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.account_index.AccountIndexNetRequestBean;
import cn.airizu.domain.protocol.account_index.AccountIndexNetRespondBean;
import cn.airizu.global_data_cache.GlobalConstant;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.INetFileRespondCallback;
import cn.airizu.net_file.NetFileHelper;
import cn.airizu.net_file.upload.HttpFormFileBean;
import cn.airizu.toolutils.BitmapTools;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleImageLoader;
import cn.airizu.toolutils.SimpleProgressDialog;

/**
 * 个人信息界面
 * 
 * @author zhihua.tang
 */
// note : 这个界面不能设为自动切换屏幕方向, 因为屏幕一切换就会重新进入onCreate()方法,
// 那么就会重新加载预加载布局, 那么如果此时调用了相册或者照相机, 那么回到 onActivityResult 之前会先调用onCreate()方法,
// 导致设置 userPhotoImageView失效, 因为此时 userPhotoImageView为空
public class UserInformationActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	private int netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	private int netRequestIndexForUpdateAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	private enum NetRequestTagEnum {
		// 2.15 获取账号首页信息
		GET_ACCOUNT_INFO,
		// 更新账户信息
		UPDATE_ACCOUNT_INFO
	};
	
	private static enum IntentRequestCodeEnum {
		//
		TO_PICK,
		//
		TO_IMAGE_CAPTURE
	};
	
	private AccountIndexNetRespondBean accountIndexNetRespondBean;
	
	private View userPhotoClickView;
	private ImageView userPhotoImageView;
	private EditText usernameEditText;
	private RadioGroup sexRadioGroup;
	private RadioButton menRadioButton;
	private RadioButton womenRadioButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		requestUserAccountIndexInfo();
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
		
		SimpleProgressDialog.resetByThisContext(this);
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
		
		DomainProtocolNetHelperSingleton.getInstance().cancelAllNetRequestWithThisContext(this);
		netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	}
	
	private Bitmap userPhotoBitmap;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.i(TAG, "onActivityResult");
		
		do {
			if (resultCode != RESULT_OK) {
				// 用户取消了操作
				break;
			}
			
			if (requestCode == IntentRequestCodeEnum.TO_PICK.ordinal()) {
				final Uri imageUri = data.getData();
				if (imageUri == null) {
					break;
				}
				final String[] projectionStringArray = new String[1];
				projectionStringArray[0] = "_data";
				final Cursor imageCursor = getContentResolver().query(imageUri, projectionStringArray, null, null, null);
				if (imageCursor == null) {
					break;
				}
				imageCursor.moveToFirst();
				final String imagePath = imageCursor.getString(imageCursor.getColumnIndex(projectionStringArray[0]));
				imageCursor.close();
				if (TextUtils.isEmpty(imagePath)) {
					break;
				}
				userPhotoBitmap = BitmapFactory.decodeFile(imagePath);
			} else if (requestCode == IntentRequestCodeEnum.TO_IMAGE_CAPTURE.ordinal()) {
				if (data == null || data.getExtras() == null || !data.getExtras().containsKey("data")) {
					break;
				}
				userPhotoBitmap = (Bitmap) data.getExtras().get("data");
			}
			if (userPhotoBitmap == null) {
				break;
			}
			
			// 先将图片压缩
			userPhotoBitmap = BitmapTools.zoomBitmap(userPhotoBitmap, 80.0f, 80.f);
			
			// 20121121 tangzhihua : 发现从照相机返回时, userPhotoImageView竟然为null, 详情见前面的note
			if (userPhotoImageView != null) {
				userPhotoImageView.setImageBitmap(userPhotoBitmap);
			}
			
		} while (false);
	}
	
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("个人信息");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.user_photo_click_layout: {
					final List<String> list = new ArrayList<String>();
					list.addAll(GlobalConstant.dataSourceForPhotoGetTypeList.keySet());
					activateRadioPopWindow("选择头像", list);
				}
				break;
				
				case R.id.submit_Button: {
					String errorMessageString = "";
					do {
						final String userNameString = usernameEditText.getText().toString();
						if (TextUtils.isEmpty(userNameString)) {
							errorMessageString = "用户名不能为空.";
							break;
						}
						String userSexString = "";
						if (menRadioButton.isChecked()) {
							userSexString = GlobalConstant.SexEnum.MEN.getValue();
						} else {
							userSexString = GlobalConstant.SexEnum.WOMEN.getValue();
						}
						
						requestUpdateUserAccountInfo(userNameString, userSexString);
						
						return;
					} while (false);
					
					Toast.makeText(UserInformationActivity.this, errorMessageString, 0);
				}
				break;
				
				default:
				break;
			}
			
		}
	};
	
	private void loadRealUIAndUseNetRespondBeanInitialize(AccountIndexNetRespondBean accountIndexNetRespondBean) {
		if (accountIndexNetRespondBean == null) {
			return;
		}
		
		// 加载真正的UI界面
		setContentView(R.layout.user_information_activity);
		
		//
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		String titleBarTitleString = "个人信息";
		titleBar.setTitleByString(titleBarTitleString);
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		userPhotoClickView = findViewById(R.id.user_photo_click_layout);
		userPhotoClickView.setOnClickListener(onClickListener);
		
		// 用户头像图片
		userPhotoImageView = (ImageView) findViewById(R.id.user_photo_content_ImageView);
		SimpleImageLoader.loadImageFromLocalCache(userPhotoImageView, accountIndexNetRespondBean.getUserImageURL(), null);
		
		// 用户名文本输入框
		usernameEditText = (EditText) findViewById(R.id.user_name_EditText);
		usernameEditText.setText(accountIndexNetRespondBean.getUserName());
		// 性别单选按钮组
		sexRadioGroup = (RadioGroup) findViewById(R.id.sex_RadioGroup);
		menRadioButton = (RadioButton) findViewById(R.id.men_RadioButton);
		womenRadioButton = (RadioButton) findViewById(R.id.women_RadioButton);
		if (accountIndexNetRespondBean.getSex().equals(GlobalConstant.SexEnum.MEN.getValue())) {
			menRadioButton.setChecked(true);
		} else {
			womenRadioButton.setChecked(true);
		}
		// 用户手机
		final TextView userPhoneTextView = (TextView) findViewById(R.id.user_phone_TextView);
		userPhoneTextView.setText(accountIndexNetRespondBean.getPhoneNumber());
		// 用户手机是否验证的icon
		if (accountIndexNetRespondBean.isValidatePhone()) {
			ImageView phoneVerifiedImageView = (ImageView) findViewById(R.id.phone_verified_ImageView);
			phoneVerifiedImageView.setVisibility(View.VISIBLE);
		}
		// 用户邮箱
		final TextView userEmailTextView = (TextView) findViewById(R.id.user_email_TextView);
		userEmailTextView.setText(accountIndexNetRespondBean.getEmail());
		// 用户邮箱是否验证的icon
		if (accountIndexNetRespondBean.isValidateEmail()) {
			ImageView emailVerifiedImageView = (ImageView) findViewById(R.id.email_verified_ImageView);
			emailVerifiedImageView.setVisibility(View.VISIBLE);
		}
		
		// "确定按钮"
		final Button submitButton = (Button) findViewById(R.id.submit_Button);
		submitButton.setOnClickListener(onClickListener);
	}
	
	private void loadNetErrorProcessUIAndInitialize(final String netErrorMessageString, final Enum<?> netRequestTagEnum) {
		if (netRequestTagEnum == NetRequestTagEnum.GET_ACCOUNT_INFO) {
			// 如果真正的界面UI还未加载, 就还是使用 "预加载UI" 来显示网络错误信息
			final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
			if (infoLabelForPreLoadedUiTextView != null || !TextUtils.isEmpty(netErrorMessageString)) {
				infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
				infoLabelForPreLoadedUiTextView.setText(netErrorMessageString);
			}
			final Button functionButtonForPreloadedUiButton = (Button) findViewById(R.id.function_button_for_preloaded_ui_Button);
			if (functionButtonForPreloadedUiButton != null) {
				functionButtonForPreloadedUiButton.setVisibility(View.VISIBLE);
				functionButtonForPreloadedUiButton.setText("刷新");
				functionButtonForPreloadedUiButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						requestUserAccountIndexInfo();
					}
				});
			}
		} else if (netRequestTagEnum == NetRequestTagEnum.UPDATE_ACCOUNT_INFO) {
			Toast.makeText(UserInformationActivity.this, netErrorMessageString, 0).show();
		}
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			}
		}
	};
	
	private void requestUserAccountIndexInfo() {
		// 开始请求, 2.15 获取账号首页信息
		final AccountIndexNetRequestBean netRequestDomainBean = new AccountIndexNetRequestBean();
		netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, netRequestDomainBean, NetRequestTagEnum.GET_ACCOUNT_INFO, domainNetRespondCallback);
		if (netRequestIndexForGetAccountInfo != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI_FOR_ACCOUNT_INDEX_INFO,
		//
		UPDATE_ACCOUNT_INFO_SUCCESS
		
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			SimpleProgressDialog.dismiss(UserInformationActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				DebugLog.i(TAG, "SHOW_NET_ERROR_MESSAGE");
				SimpleProgressDialog.resetByThisContext(UserInformationActivity.this);
				
				final String netErrorMessageString = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessageString, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI_FOR_ACCOUNT_INDEX_INFO.ordinal()) {
				DebugLog.i(TAG, "REFRESH_UI");
				loadRealUIAndUseNetRespondBeanInitialize(accountIndexNetRespondBean);
				accountIndexNetRespondBean = null;
			} else if (msg.what == HandlerMsgTypeEnum.UPDATE_ACCOUNT_INFO_SUCCESS.ordinal()) {
				finish();
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (requestEvent == NetRequestTagEnum.GET_ACCOUNT_INFO) {
			netRequestIndexForGetAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		} else if (requestEvent == NetRequestTagEnum.UPDATE_ACCOUNT_INFO) {
			netRequestIndexForUpdateAccountInfo = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
		}
	}
	
	private IDomainNetRespondCallback domainNetRespondCallback = new IDomainNetRespondCallback() {
		
		@Override
		public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean) {
			DebugLog.i(TAG, "domainNetRespondHandleInNonUIThread --- start ! ");
			clearNetRequestIndexByRequestEvent(requestEvent);
			
			if (errorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal();
				msg.getData().putString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name(), errorBean.getErrorMessage());
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.GET_ACCOUNT_INFO) {// 2.15 获取账号首页信息
			
				accountIndexNetRespondBean = (AccountIndexNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, accountIndexNetRespondBean.toString());
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI_FOR_ACCOUNT_INDEX_INFO.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private INetFileProgressCallback netFileProgressCallback = new INetFileProgressCallback() {
		
		@Override
		public void netFileProgressCallbackInNonUIThread(long totalSize, long completedSize) {
			DebugLog.e(TAG, "totalSize=" + totalSize + ",completedSize=" + completedSize);
			
		}
	};
	
	private INetFileRespondCallback netFileRespondCallback = new INetFileRespondCallback() {
		
		@Override
		public void netFileRespondCallback(final Enum<?> requestEvent, final String fullFilePathName, final NetErrorTypeEnum errorType, final String errorMessage) {
			DebugLog.i(TAG, "netFileRespondCallback --- start ! ");
			clearNetRequestIndexByRequestEvent(requestEvent);
			
			if (errorType != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal();
				msg.getData().putString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name(), errorMessage);
				msg.getData().putSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name(), requestEvent);
				handler.sendMessage(msg);
				return;
			}
			
			if (requestEvent == NetRequestTagEnum.UPDATE_ACCOUNT_INFO) {// 更新账号户信息成功
			
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.UPDATE_ACCOUNT_INFO_SUCCESS.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private void requestUpdateUserAccountInfo(final String userNameString, final String userSexString) {
		if (TextUtils.isEmpty(userNameString) || TextUtils.isEmpty(userSexString)) {
			return;
		}
		
		try {
			HttpFormFileBean imagehHttpFormFile = null;
			if (userPhotoBitmap != null) {
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				userPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				imagehHttpFormFile = new HttpFormFileBean("userphoto.jpg", outputStream.toByteArray(), "image", "multipart/form-data; boundary=0xKhTmLbOuNdArY");
			}
			final String uploadPath = "http://124.65.163.102:819/app/account/update";
			final Map<String, String> textParameterMap = new HashMap<String, String>();
			textParameterMap.put("userName", userNameString);
			textParameterMap.put("sex", userSexString);
			
			netRequestIndexForUpdateAccountInfo = NetFileHelper.getInstance().uploadFile(NetRequestTagEnum.UPDATE_ACCOUNT_INFO, uploadPath, textParameterMap, imagehHttpFormFile,
					netFileProgressCallback, netFileRespondCallback);
			if (netRequestIndexForUpdateAccountInfo != NetFileHelper.IDLE_NETWORK_REQUEST_ID) {
				SimpleProgressDialog.show(this, progressDialogOnCancelListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// 单选列表控件
	private PopupWindow popupWindow;
	private RadioPopList radioPopList;
	
	private void activateRadioPopWindow(String title, List<String> dataSource) {
		
		if (popupWindow == null) {
			
			// 加载popupWindow的布局文件
			radioPopList = new RadioPopList(this, radioPopListDelegate);
			
			// 声明一个弹出框
			popupWindow = new PopupWindow(radioPopList, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.setFocusable(true);
		}
		
		radioPopList.setTitle(title);
		final RadioPopListAdapter radioPopListAdapter = new RadioPopListAdapter(this, dataSource);
		radioPopList.setAdapter(radioPopListAdapter);
		popupWindow.showAsDropDown(findViewById(R.id.title_bar));
	}
	
	private CustomControlDelegate radioPopListDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			final RadioPopList.ActionEnum actionEnum = (RadioPopList.ActionEnum) actionTypeEnum;
			switch (actionEnum)
			{
				case TITLE_LEFT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				
				case TITLE_RIGHT_BUTTON_CLICK: {
					popupWindow.dismiss();
				}
				break;
				
				case LIST_ITEM_CLICK: {
					do {
						final View view = (View) contorl;
						if (view == null) {
							break;
						}
						final TextView contentTextView = (TextView) view.findViewById(R.id.content_TextView);
						if (contentTextView == null) {
							break;
						}
						final String text = contentTextView.getText().toString();
						if (TextUtils.isEmpty(text)) {
							break;
						}
						
						final String photoGetTypeActionString = GlobalConstant.dataSourceForPhotoGetTypeList.get(text);
						if (photoGetTypeActionString.equals(GlobalConstant.PhotoGetTypeActionEnum.PICK.getValue())) {
							final Intent intent = new Intent(photoGetTypeActionString, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, IntentRequestCodeEnum.TO_PICK.ordinal());
						} else if (photoGetTypeActionString.equals(GlobalConstant.PhotoGetTypeActionEnum.IMAGE_CAPTURE.getValue())) {
							final Intent intent = new Intent(photoGetTypeActionString);
							startActivityForResult(intent, IntentRequestCodeEnum.TO_IMAGE_CAPTURE.ordinal());
						}
					} while (false);
					popupWindow.dismiss();
				}
				break;
				
				default:
				break;
			}
		}
	};
}
