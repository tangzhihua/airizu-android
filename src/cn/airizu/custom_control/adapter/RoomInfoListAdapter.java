package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.toolutils.SimpleImageLoader;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;

public final class RoomInfoListAdapter extends BaseAdapter {
	
	private List<Map<String, String>> dataSource;
	private LayoutInflater inflaterInstance;
	
	public RoomInfoListAdapter(Context context, List<Map<String, String>> dataSource) {
		this.inflaterInstance = LayoutInflater.from(context);
		this.dataSource = dataSource;
	}
	
	@Override
	public int getCount() {
		return dataSource.size();
	}
	
	@Override
	public Object getItem(int position) {
		return dataSource.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RoomInfoListItem holder = null;
		if (convertView == null) {
			convertView = inflaterInstance.inflate(R.layout.room_info_listview_item, null);
			holder = new RoomInfoListItem(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RoomInfoListItem) convertView.getTag();
		}
		
		// 房间图片
		final String roomImageUrl = dataSource.get(position).get(DataSourceKeyEnum.ROOM_IMAGE.name()).toString();
		holder.roomImageImageView.setTag(roomImageUrl);
		SimpleImageLoader.loadImageFromLocalCacheAndNetworkDownload(holder.roomImageImageView, roomImageUrl, null);
		// 当前房间是否是 100%验证的
		if (dataSource.get(position).get(DataSourceKeyEnum.ROOM_IS_VERIFY.name()).toString().equals("true")) {
			holder.roomIsVerifyImageView.setVisibility(View.VISIBLE);
		} else {
			holder.roomIsVerifyImageView.setVisibility(View.INVISIBLE);
		}
		// 房间标题
		holder.roomTitleTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ROOM_TITLE.name()).toString());
		// 当前房间与目标的距离
		if (dataSource.get(position).containsKey(DataSourceKeyEnum.DISTANCE_TO_TARGET.name())) {
			holder.roomDistanceToTargetTextView.setVisibility(View.VISIBLE);
			String distanceStirng = dataSource.get(position).get(DataSourceKeyEnum.DISTANCE_TO_TARGET.name()).toString();
			Double distanceDouble = Double.valueOf(distanceStirng);
			int distance = distanceDouble.intValue();
			String distanceUnits = "m";
			if (distance > 1000) {
				distance /= 1000;
				distanceUnits = "km";
			}
			holder.roomDistanceToTargetTextView.setText(distance + distanceUnits);
		} else {
			holder.roomDistanceToTargetTextView.setVisibility(View.GONE);
		}
		// 当前房间的租住方式
		holder.roomRentalWayNameTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ROOM_RENTAL_WAY_NAME.name()).toString());
		// 当前房间限住人数
		holder.roomCheckAmountTextView.setText("  限住" + dataSource.get(position).get(DataSourceKeyEnum.ROOM_CHECK_AMOUNT.name()).toString() + "人");
		// 当前房间评论数
		holder.roomReviewCountTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ROOM_REVIEW_COUNT.name()).toString());
		// 该房间曾被预定的次数
		holder.roomScheduledTotalTextView.setText("已预订" + dataSource.get(position).get(DataSourceKeyEnum.ROOM_SCHEDULED_TOTAL.name()).toString() + "晚");
		// 房间单价
		holder.roomPriceTextView.setText(ToolsFunctionForThisProgect.getFormattedPriceString(dataSource.get(position).get(DataSourceKeyEnum.ROOM_PRICE.name()).toString()));
		return convertView;
	}
	
	public static enum ActionEnum {
		//
		ITEM_CLICKED
	}
	
	private int currentlyPosition = 0;
	
	public int getCurrentlyPosition() {
		return currentlyPosition;
	}
	
	public static enum DataSourceKeyEnum {
		// 房间图片
		ROOM_IMAGE,
		// 当前房间是否是 100%验证的
		ROOM_IS_VERIFY,
		// 房间标题
		ROOM_TITLE,
		// 当前房间与目标的距离
		DISTANCE_TO_TARGET,
		// 当前房间的租住方式
		ROOM_RENTAL_WAY_NAME,
		// 当前房间限住人数
		ROOM_CHECK_AMOUNT,
		// 当前房间评论数
		ROOM_REVIEW_COUNT,
		// 该房间曾被预定的次数
		ROOM_SCHEDULED_TOTAL,
		// 房间单价
		ROOM_PRICE
		
	}
	
	private final class RoomInfoListItem {
		
		private RoomInfoListItem(View view) {
			roomImageImageView = (ImageView) view.findViewById(R.id.room_content_ImageView);
			roomIsVerifyImageView = (ImageView) view.findViewById(R.id.room_check_ImageView);
			roomTitleTextView = (TextView) view.findViewById(R.id.room_title_TextView);
			roomDistanceToTargetTextView = (TextView) view.findViewById(R.id.room_check_distance_TextView);
			roomRentalWayNameTextView = (TextView) view.findViewById(R.id.room_rental_way_name_TextView);
			roomCheckAmountTextView = (TextView) view.findViewById(R.id.room_check_amount_TextView);
			roomReviewCountTextView = (TextView) view.findViewById(R.id.room_review_count_TextView);
			roomScheduledTotalTextView = (TextView) view.findViewById(R.id.room_scheduled_total_TextView);
			roomPriceTextView = (TextView) view.findViewById(R.id.room_price_TextView);
		}
		
		private final ImageView roomImageImageView; // 房间图片
		private final ImageView roomIsVerifyImageView; // 当前房间是否是 100%验证的
		private final TextView roomTitleTextView; // 房间标题
		private final TextView roomDistanceToTargetTextView;// 当前房间与目标的距离
		private final TextView roomRentalWayNameTextView;// 当前房间的租住方式
		private final TextView roomCheckAmountTextView; // 当前房间限住人数
		private final TextView roomReviewCountTextView; // 当前房间评论数
		private final TextView roomScheduledTotalTextView; // 该房间曾被预定的次数
		private final TextView roomPriceTextView; // 房间单价
	}
}
