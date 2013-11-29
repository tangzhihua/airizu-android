package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;

import cn.airizu.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityInfoListAdapter extends BaseAdapter {
	private List<Map<String, String>> list;
	private LayoutInflater mInflater;
	
	public CityInfoListAdapter(Context context, List<Map<String, String>> list) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_city_list_item, null);
			holder = new ViewHolder();
			holder.cityLetter = (TextView) convertView.findViewById(R.id.search_city_letter);
			holder.cityName = (TextView) convertView.findViewById(R.id.search_city_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.cityName.setText(list.get(position).get("cityName"));
		
		int idx = position - 1;
		// 判断前后Item是否匹配，如果不匹配则设置并显示，匹配则取消
		char previewChar = idx >= 0 ? list.get(idx).get("code").charAt(0) : ' ';
		char currentChar = list.get(position).get("code").charAt(0);
		// 将小写字符转换为大写字符
		char newPreviewChar = Character.toUpperCase(previewChar);
		char newCurrentChar = Character.toUpperCase(currentChar);
		if (newCurrentChar != newPreviewChar) {
			holder.cityLetter.setVisibility(View.VISIBLE);
			holder.cityLetter.setText(String.valueOf(newCurrentChar));
		} else {
			// 此段代码不可缺：实例化一个CurrentView后，会被多次赋值并且只有最后一次赋值的position是正确
			holder.cityLetter.setVisibility(View.GONE);
		}
		return convertView;
		
	}
	
	public final class ViewHolder {
		public TextView cityLetter;
		public TextView cityName;
	}
}
