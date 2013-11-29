package cn.airizu.custom_control.adapter;

import java.util.List;
import cn.airizu.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RadioPopListAdapter extends BaseAdapter {
	private List<String> dataSource;
	private LayoutInflater inflater;
	
	public RadioPopListAdapter(Context context, List<String> dataSource) {
		this.inflater = LayoutInflater.from(context);
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
		ListItemViewHolder holder = null;
		if (convertView == null) {
			holder = new ListItemViewHolder();
			convertView = inflater.inflate(R.layout.radio_pop_list_item, null);
			holder.contentTextView = (TextView) convertView.findViewById(R.id.content_TextView);
			convertView.setTag(holder);
		} else {
			holder = (ListItemViewHolder) convertView.getTag();
		}
		
		holder.contentTextView.setText(dataSource.get(position));
		return convertView;
	}
	
	private final class ListItemViewHolder {
		// item名称
		public TextView contentTextView;
	}
}
