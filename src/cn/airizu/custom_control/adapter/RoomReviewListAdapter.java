package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;
import cn.airizu.activity.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoomReviewListAdapter extends BaseAdapter {
	private List<Map<String, String>> dataSource;
	private LayoutInflater inflaterInstance;
	
	public RoomReviewListAdapter(Context context, List<Map<String, String>> dataSource) {
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
		
		RoomReviewListItem holder = null;
		if (convertView == null) {
			convertView = inflaterInstance.inflate(R.layout.room_review_listview_item, null);
			holder = new RoomReviewListItem(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RoomReviewListItem) convertView.getTag();
		}
		
		holder.userNameTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.USER_NAME.name()));
		holder.userReviewTimeTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.USER_REVIEW_TIME.name()));
		holder.userReviewTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.USER_REVIEW.name()));
		String hostReviewTimeString = dataSource.get(position).get(DataSourceKeyEnum.HOST_REVIEW_TIME.name());
		String hostReviewString = dataSource.get(position).get(DataSourceKeyEnum.HOST_REVIEW.name());
		if (!TextUtils.isEmpty(hostReviewTimeString) && !TextUtils.isEmpty(hostReviewTimeString)) {
			holder.hostReviewTimeTextView.setText(hostReviewTimeString);
			holder.hostNameTextView.setText(hostReviewString);
		} else {
			holder.goneHostReviewLayout();
		}
		
		return convertView;
	}
	
	public static enum DataSourceKeyEnum {
		// 用户姓名
		USER_NAME,
		// 用户发表评论的时间
		USER_REVIEW_TIME,
		// 用户评论的内容
		USER_REVIEW,
		// 房东回复时间
		HOST_REVIEW_TIME,
		// 房东回复的内容
		HOST_REVIEW
	}
	
	private static final class RoomReviewListItem {
		private RoomReviewListItem(View view) {
			hostReviewLayout = view.findViewById(R.id.host_review_layout);
			userNameTextView = (TextView) view.findViewById(R.id.user_name_TextView);
			userReviewTimeTextView = (TextView) view.findViewById(R.id.user_review_time_TextView);
			userReviewTextView = (TextView) view.findViewById(R.id.user_review_TextView);
			hostReviewTimeTextView = (TextView) view.findViewById(R.id.host_review_time_TextView);
			hostNameTextView = (TextView) view.findViewById(R.id.host_name_TextView);
		}
		
		private final View hostReviewLayout;
		private final TextView userNameTextView;
		private final TextView userReviewTimeTextView;
		private final TextView userReviewTextView;
		private final TextView hostReviewTimeTextView;
		private final TextView hostNameTextView;
		
		private void goneHostReviewLayout() {
			hostReviewLayout.setVisibility(View.GONE);
		}
	}
}
