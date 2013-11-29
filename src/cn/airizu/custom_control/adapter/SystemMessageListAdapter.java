package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.custom_control.textview_ellipse_end_fixed.TextViewEllipseEndFixed;

public class SystemMessageListAdapter extends BaseAdapter {
	private List<Map<String, String>> dataSource;
	private LayoutInflater inflaterInstance;
	
	public SystemMessageListAdapter(Context context, List<Map<String, String>> dataSource) {
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
		
		SystemMessageListItem holder = null;
		if (convertView == null) {
			convertView = inflaterInstance.inflate(R.layout.system_message_listview_item, null);
			holder = new SystemMessageListItem(convertView);
			convertView.setTag(holder);
		} else {
			holder = (SystemMessageListItem) convertView.getTag();
		}
		
		holder.messageTextTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.MESSAGE.name()));
		holder.messageDateTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.DATE.name()));
		
		return convertView;
	}
	
	public static enum DataSourceKeyEnum {
		//
		MESSAGE,
		//
		DATE
	}
	
	private static final class SystemMessageListItem {
		private SystemMessageListItem(View view) {
			messageTextTextView = (TextViewEllipseEndFixed) view.findViewById(R.id.message_text_TextView);
			messageDateTextView = (TextView) view.findViewById(R.id.message_date_TextView);
		}
		
		private final TextViewEllipseEndFixed messageTextTextView;
		private final TextView messageDateTextView;
	}
}
