package cn.airizu.custom_control.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.airizu.activity.R;
import cn.airizu.custom_control.CustomControlDelegate;
import cn.airizu.toolutils.SimpleImageLoader;

public class RecommendCityListAdapter extends BaseAdapter {
	private List<Map<String, String>> dataSource;
	private LayoutInflater inflaterInstance;
	private CustomControlDelegate delegate;
	
	public RecommendCityListAdapter(Context context, List<Map<String, String>> dataSource, CustomControlDelegate delegate) {
		this.inflaterInstance = LayoutInflater.from(context);
		this.dataSource = dataSource;
		this.delegate = delegate;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		RecommendCiytListItem holder = null;
		if (convertView == null) {
			convertView = inflaterInstance.inflate(R.layout.recommend_city_listview_item, null);
			holder = new RecommendCiytListItem(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RecommendCiytListItem) convertView.getTag();
		}
		
		holder.cityNameTextView.setText(dataSource.get(position).get(DataSourceKeyEnum.CITY_NAME.name()));
		SimpleImageLoader.loadImageFromLocalCacheAndNetworkDownload(holder.cityPhotoImageView, dataSource.get(position).get(DataSourceKeyEnum.CITY_PHOTO.name()), null);
		holder.cityPhotoImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (delegate != null) {
					currentlyPosition = position;
					delegate.customControlOnAction(this, ActionEnum.CITY_PHOTO_CLICKED);
				}
			}
		});
		holder.street1Button.setText(dataSource.get(position).get(DataSourceKeyEnum.STREET_1_NAME.name()));
		holder.street1Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (delegate != null) {
					currentlyPosition = position;
					delegate.customControlOnAction(this, ActionEnum.STREET_1_CLICKED);
				}
			}
		});
		holder.street2Button.setText(dataSource.get(position).get(DataSourceKeyEnum.STREET_2_NAME.name()));
		holder.street2Button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (delegate != null) {
					currentlyPosition = position;
					delegate.customControlOnAction(this, ActionEnum.STREET_2_CLICKED);
				}
			}
		});
		
		return convertView;
	}
	
	public static enum ActionEnum {
		//
		CITY_PHOTO_CLICKED,
		//
		STREET_1_CLICKED,
		//
		STREET_2_CLICKED
	}
	
	private int currentlyPosition = 0;
	
	public int getCurrentlyPosition() {
		return currentlyPosition;
	}
	
	public static enum DataSourceKeyEnum {
		//
		CITY_NAME,
		//
		CITY_PHOTO,
		//
		STREET_1_NAME,
		//
		STREET_2_NAME
	}
	
	private final class RecommendCiytListItem {
		private RecommendCiytListItem(View view) {
			cityNameTextView = (TextView) view.findViewById(R.id.recommend_city_listitem_city_name_TextView);
			cityPhotoImageView = (ImageView) view.findViewById(R.id.recommend_city_listitem_photo_ImageView);
			street1Button = (Button) view.findViewById(R.id.recommend_city_listitem_street1_Button);
			street2Button = (Button) view.findViewById(R.id.recommend_city_listitem_street2_Button);
		}
		
		private final TextView cityNameTextView;
		private final ImageView cityPhotoImageView;
		private final Button street1Button;
		private final Button street2Button;
	}
}