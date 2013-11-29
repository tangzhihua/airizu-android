package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;
import cn.airizu.activity.R;
import cn.airizu.toolutils.SimpleImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserOrderListAdapter extends BaseAdapter {
	
	private List<Map<String, String>> dataSource;
	private LayoutInflater inflaterInstance;
	
	public UserOrderListAdapter(Context context, List<Map<String, String>> dataSource) {
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
		
		UserOrderListItem holder = null;
		if (convertView == null) {
			convertView = inflaterInstance.inflate(R.layout.user_order_main_activity_listview_item, null);
			holder = new UserOrderListItem(convertView);
			convertView.setTag(holder);
		} else {
			holder = (UserOrderListItem) convertView.getTag();
		}
		
		String roomPhotoUrl = dataSource.get(position).get(DataSourceKeyEnum.ROOM_PHOTO_URL.name());
		SimpleImageLoader.loadImageFromLocalCacheAndNetworkDownload(holder.roomPhotoImageView, roomPhotoUrl, null);
		holder.roomTitleTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ROOM_TITLE.name()));
		holder.checkinDateTextTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.CHECKIN_DATE.name()));
		holder.checkoutDateTextTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.CHECKOUT_DATE.name()));
		holder.orderStateTextTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ORDER_STATE.name()));
		holder.roomPriceTextTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.ORDER_PRICE.name()));
		
		return convertView;
	}
	
	public static enum DataSourceKeyEnum {
		//
		ROOM_PHOTO_URL,
		//
		ROOM_TITLE,
		//
		CHECKIN_DATE,
		//
		CHECKOUT_DATE,
		//
		ORDER_STATE,
		//
		ORDER_PRICE,
		//
		ORDER_ID
	}
	
	private static final class UserOrderListItem {
		private UserOrderListItem(View view) {
			roomPhotoImageView = (ImageView) view.findViewById(R.id.room_photo_ImageView);
			roomTitleTextView = (TextView) view.findViewById(R.id.room_title_TextView);
			checkinDateTextTextView = (TextView) view.findViewById(R.id.checkin_date_text_TextView);
			checkoutDateTextTextView = (TextView) view.findViewById(R.id.checkout_date_text_TextView);
			orderStateTextTextView = (TextView) view.findViewById(R.id.order_state_text_TextView);
			roomPriceTextTextView = (TextView) view.findViewById(R.id.room_price_text_TextView);
		}
		
		private final ImageView roomPhotoImageView;
		private final TextView roomTitleTextView;
		private final TextView checkinDateTextTextView;
		private final TextView checkoutDateTextTextView;
		private final TextView orderStateTextTextView;
		private final TextView roomPriceTextTextView;
	}
	
}
