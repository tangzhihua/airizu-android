package cn.airizu.custom_control.adapter;

import java.util.List;
import cn.airizu.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectSearchInfoAdapter extends BaseAdapter {
	
	private List<String> list = null;
	private LayoutInflater mInflater;
	
	public SelectSearchInfoAdapter(Context context, List<String> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.select_search_info_textview, null);
			holder = new ViewHolder();
			holder.selectSearchInfoTextView = (TextView) convertView.findViewById(R.id.select_search_info_TextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.selectSearchInfoTextView.setText(list.get(position));
		return convertView;
	}
	
	public final class ViewHolder {
		public TextView selectSearchInfoTextView;
	}
	
}
