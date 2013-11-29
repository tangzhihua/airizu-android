package cn.airizu.activity.room_detail_tenant_reviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.airizu.activity.R;
import cn.airizu.activity.free_book_confirm_checkin_time.FreebookConfirmCheckinTimeActivity;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.custom_control.adapter.RoomReviewListAdapter;
import cn.airizu.custom_control.ratingbar.CustomRatingBar;
import cn.airizu.custom_control.titlebar.CustomTitleBar;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.DomainProtocolNetHelperSingleton;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;
import cn.airizu.domain.protocol.review_review_list.RoomReview;
import cn.airizu.domain.protocol.review_review_list.RoomReviewNetRequestBean;
import cn.airizu.domain.protocol.review_review_list.RoomReviewNetRespondBean;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleProgressDialog;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 租客点评界面
 * 
 * @author zhihua.tang
 */
public class RoomDetailTenantReviewsActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum IntentExtraTagEnum {
		// 房间编号
		ROOM_NUMBER,
		// 房间单价
		ROOM_PRICE
	};
	
	private boolean isRealUI = false;
	private boolean isLastLine = false;
	
	// 从1开始
	private int pageNum = 1;
	private String roomNumberString;// 房间ID
	private String roomPriceString;// 房间单价
	private RoomReviewNetRespondBean roomReviewNetRespondBean;
	
	private RoomReviewListAdapter roomReviewListAdapter;
	private List<Map<String, String>> dataSourceForRoomReviewListView = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> newDataForRoomReview;
	private List<RoomReview> roomReviewBeanList = new ArrayList<RoomReview>();
	
	private TextView listStateHintTextView;
	
	private enum NetRequestTagEnum {
		// 2.25 获得房间评论
		ROOM_REVIEW
	};
	
	private int netRequestIndexForRoomReview = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		loadPreLoadedUIAndInitialize();
		
		if (!parseIntent(getIntent())) {
			return;
		}
		
		requestRoomReviewByRoomNumber(roomNumberString, pageNum);
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
	}
	
	private boolean parseIntent(Intent intent) {
		do {
			if (intent == null) {
				break;
			}
			
			roomNumberString = intent.getStringExtra(IntentExtraTagEnum.ROOM_NUMBER.name());
			if (TextUtils.isEmpty(roomNumberString)) {
				break;
			}
			
			roomPriceString = intent.getStringExtra(IntentExtraTagEnum.ROOM_PRICE.name());
			if (TextUtils.isEmpty(roomPriceString)) {
				break;
			}
			return true;
		} while (false);
		
		DebugLog.e(TAG, "The Intent passed over data loss ! ");
		return false;
	}
	
	private void loadPreLoadedUIAndInitialize() {
		setContentView(R.layout.pre_loaded_layout);
		
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar_for_preloaded_ui_TitleBar);
		titleBar.setTitleByString("租客点评");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		titleBar.setVisibility(View.VISIBLE);
		
		final TextView infoLabelForPreLoadedUiTextView = (TextView) findViewById(R.id.info_label_for_preloaded_ui_TextView);
		infoLabelForPreLoadedUiTextView.setText("数据加载中...");
		infoLabelForPreLoadedUiTextView.setVisibility(View.VISIBLE);
	}
	
	private void loadNetErrorProcessUIAndInitialize(final String netErrorMessageString, final NetRequestTagEnum netRequestTagEnum) {
		
		if (isRealUI) {
			// 如果真正的界面UI已经加载了, 那么就使用这个TextView来提示网络加载错误信息
			listStateHintTextView.setVisibility(View.VISIBLE);
			listStateHintTextView.setText(netErrorMessageString);
		} else {
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
						requestRoomReviewByRoomNumber(roomNumberString, pageNum);
					}
				});
			}
		}
	}
	
	private void loadRealUIAndUseRoomReviewNetRespondBeanInitialize(RoomReviewNetRespondBean roomReviewNetRespondBean) {
		if (roomReviewNetRespondBean == null) {
			return;
		}
		// 加载当前Activity真正的UI界面
		isRealUI = true;
		setContentView(R.layout.room_detail_tenant_reviews_activity);
		
		// titlebar
		final CustomTitleBar titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
		titleBar.setTitleByString("租客点评");
		titleBar.setLeftButtonByImage(R.drawable.selector_main_navigation_titlebar_back_button);
		titleBar.setDelegate(titleBarOnActionDelegate);
		
		// 免费预订
		final Button freebookButton = (Button) findViewById(R.id.freebook_Button);
		freebookButton.setOnClickListener(onClickListener);
		
		// 房间单价
		final TextView priceTextView = (TextView) findViewById(R.id.price_TextView);
		priceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(roomPriceString));
		
		// 平均分
		final TextView averageTextTextView = (TextView) findViewById(R.id.average_text_TextView);
		averageTextTextView.setText(roomReviewNetRespondBean.getAvgScore());
		
		// 评论总数
		final TextView commentTextTextView = (TextView) findViewById(R.id.comment_text_TextView);
		commentTextTextView.setText(String.valueOf(roomReviewNetRespondBean.getReviewCount()));
		
		// 房间评分控件
		final CustomRatingBar scoreCustomRatingBar = (CustomRatingBar) findViewById(R.id.score_CustomRatingBar);
		scoreCustomRatingBar.setDataSourceAndInitialize(roomReviewNetRespondBean.getReviewItemMap(), CustomRatingBar.StyleEnum.FOR_TENANT_REVIEWS);
		
		listStateHintTextView = (TextView) findViewById(R.id.list_state_hint_TextView);
		listStateHintTextView.setVisibility(View.GONE);
		
		final ListView roomReviewListView = (ListView) findViewById(R.id.room_review_ListView);
		roomReviewListAdapter = new RoomReviewListAdapter(this, dataSourceForRoomReviewListView);
		roomReviewListView.setAdapter(roomReviewListAdapter);
		roomReviewListView.setOnScrollListener(systemMessageListViewOnScrollListener);
	}
	
	private CustomControlDelegate titleBarOnActionDelegate = new CustomControlDelegate() {
		
		@Override
		public void customControlOnAction(final Object contorl, final Object actionTypeEnum) {
			if (actionTypeEnum == CustomTitleBar.ActionEnum.LEFT_BUTTON_CLICKED) {
				finish();
			} else if (actionTypeEnum == CustomTitleBar.ActionEnum.RIGHT_BUTTON_CLICKED) {
				// TODO:拨打免费客服电话
			}
		}
	};
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (roomReviewNetRespondBean == null) {
				return;
			}
			
			switch (v.getId())
			{
				// 免费预订
				case R.id.freebook_Button: {
					final Intent intent = new Intent(RoomDetailTenantReviewsActivity.this, FreebookConfirmCheckinTimeActivity.class);
					intent.putExtra(FreebookConfirmCheckinTimeActivity.IntentExtraTagEnum.ROOM_NUMBER.name(), roomNumberString);
					startActivity(intent);
				}
				break;
				
				default:
				break;
			}
		}
	};
	
	private void requestRoomReviewByRoomNumber(final String roomNumber, final int pageNum) {
		if (TextUtils.isEmpty(roomNumber) || pageNum < 0) {
			return;
		}
		
		final RoomReviewNetRequestBean roomReviewNetRequestBean = new RoomReviewNetRequestBean(roomNumber, pageNum);
		netRequestIndexForRoomReview = DomainProtocolNetHelperSingleton.getInstance().requestDomainProtocol(this, roomReviewNetRequestBean, NetRequestTagEnum.ROOM_REVIEW, domainNetRespondCallback);
		if (netRequestIndexForRoomReview != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
			SimpleProgressDialog.show(this, progressDialogOnCancelListener);
		}
	}
	
	private static enum HandlerMsgTypeEnum {
		//
		SHOW_NET_ERROR_MESSAGE,
		//
		REFRESH_UI
	};
	
	private static enum HandlerExtraDataTypeEnum {
		//
		NET_REQUEST_TAG,
		//
		NET_ERROR_MESSAGE
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			SimpleProgressDialog.dismiss(RoomDetailTenantReviewsActivity.this);
			
			if (msg.what == HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.ordinal()) {
				final String netErrorMessage = msg.getData().getString(HandlerMsgTypeEnum.SHOW_NET_ERROR_MESSAGE.name());
				final NetRequestTagEnum netRequestTagEnum = (NetRequestTagEnum) msg.getData().getSerializable(HandlerExtraDataTypeEnum.NET_REQUEST_TAG.name());
				loadNetErrorProcessUIAndInitialize(netErrorMessage, netRequestTagEnum);
				
			} else if (msg.what == HandlerMsgTypeEnum.REFRESH_UI.ordinal()) {
				
				// 网络请求的有效数据成功返回, 如果还未加载真正的界面UI, 就先加载real ui, 否则只要刷新 listview就可以了
				dataSourceForRoomReviewListView.addAll(newDataForRoomReview);
				if (dataSourceForRoomReviewListView.size() >= roomReviewNetRespondBean.getReviewCount()) {
					isLastLine = true;
				}
				if (!isRealUI) {
					loadRealUIAndUseRoomReviewNetRespondBeanInitialize(roomReviewNetRespondBean);
				} else {
					if (isLastLine) {
						listStateHintTextView.setVisibility(View.VISIBLE);
						int resIdForlistStateHintTextView;
						if (roomReviewBeanList.size() <= 0) {
							resIdForlistStateHintTextView = R.string.no_valid_data_hint;
						} else {
							resIdForlistStateHintTextView = R.string.last_line_hint;
						}
						listStateHintTextView.setText(resIdForlistStateHintTextView);
					} else {
						roomReviewListAdapter.notifyDataSetChanged();
					}
					newDataForRoomReview = null;
				}
			}
		}
	};
	
	private void clearNetRequestIndexByRequestEvent(final Enum<?> requestEvent) {
		if (NetRequestTagEnum.ROOM_REVIEW == requestEvent) {
			netRequestIndexForRoomReview = DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID;
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
			
			if (requestEvent == NetRequestTagEnum.ROOM_REVIEW) {// 2.25 获得房间评论
			
				roomReviewNetRespondBean = (RoomReviewNetRespondBean) respondDomainBean;
				DebugLog.i(TAG, roomReviewNetRespondBean.toString());
				
				roomReviewBeanList.addAll(roomReviewNetRespondBean.getRoomReviewList());
				newDataForRoomReview = parseRoomReviewListToListViewAdapterDataSource(roomReviewNetRespondBean.getRoomReviewList());
				if (newDataForRoomReview.size() > 0) {
					// 只有当网络请求回有效数据时, 才要累加 page 计数器
					pageNum++;
				} else {
					// 后台已经没有有效的数据了
					// 这是跟后台约定好的协议, 如果到达最后一行数据时, 后台会返回 {"data", []}
					isLastLine = true;
				}
				
				final Message msg = new Message();
				msg.what = HandlerMsgTypeEnum.REFRESH_UI.ordinal();
				handler.sendMessage(msg);
			}
		}
	};
	
	private List<Map<String, String>> parseRoomReviewListToListViewAdapterDataSource(final List<RoomReview> roomReviewList) {
		DebugLog.i(TAG, "parseRecommendCityListToListViewAdapterDataSource --> start ! ");
		
		final List<Map<String, String>> dataSource = new ArrayList<Map<String, String>>();
		for (RoomReview review : roomReviewList) {
			
			final Map<String, String> map = new HashMap<String, String>();
			map.put(RoomReviewListAdapter.DataSourceKeyEnum.USER_NAME.name(), review.getUserName());
			map.put(RoomReviewListAdapter.DataSourceKeyEnum.USER_REVIEW_TIME.name(), ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(review.getUserReviewTime()));
			map.put(RoomReviewListAdapter.DataSourceKeyEnum.USER_REVIEW.name(), review.getUserReview());
			map.put(RoomReviewListAdapter.DataSourceKeyEnum.HOST_REVIEW_TIME.name(), ToolsFunctionForThisProgect.getDateStringWithYearMonthDayFormat(review.getHostReviewTime()));
			map.put(RoomReviewListAdapter.DataSourceKeyEnum.HOST_REVIEW.name(), review.getHostReview());
			
			dataSource.add(map);
		}
		
		return dataSource;
	}
	
	// 这个方法是为列表添加滚动监听
	private OnScrollListener systemMessageListViewOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState)
			{
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE: {
					// 判断滚动到底部
					do {
						final int listViewItemTotal = view.getCount();
						if (listViewItemTotal <= 0) {
							// listview 为空
							break;
						}
						final int listViewLastVisiblePosition = view.getLastVisiblePosition();
						if (netRequestIndexForRoomReview != DomainProtocolNetHelperSingleton.IDLE_NETWORK_REQUEST_ID) {
							SimpleProgressDialog.show(RoomDetailTenantReviewsActivity.this, progressDialogOnCancelListener);
							break;
						}
						if (listViewLastVisiblePosition != (listViewItemTotal - 1)) {
							// 如果当前listview没有滚到最后一条, 就要隐藏 listStateHintTextView
							listStateHintTextView.setVisibility(View.GONE);
							break;
						}
						
						DebugLog.i(TAG, "OnScrollListener->LastScroll");
						
						// listview 已经滚到了最后了一行
						if (isLastLine) {
							listStateHintTextView.setVisibility(View.VISIBLE);
							listStateHintTextView.setText(R.string.last_line_hint);
						} else {
							requestRoomReviewByRoomNumber(roomNumberString, pageNum);
						}
					} while (false);
				}
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}
	};
	
	private DialogInterface.OnCancelListener progressDialogOnCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
		}
	};
}
