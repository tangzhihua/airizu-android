package cn.airizu.custom_control.adapter;

import java.util.List;

import cn.airizu.toolutils.SimpleImageLoader;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class RoomPhotoGalleryAdapter extends BaseAdapter {
	
	private final List<String> imageUrlList;
	private final Context context;
	private final ImageView.ScaleType scaleType;
	
	public RoomPhotoGalleryAdapter(final Context context, final List<String> imageUrlList, final ImageView.ScaleType scaleType) {
		this.imageUrlList = imageUrlList;
		this.context = context;
		this.scaleType = scaleType;
	}

	@Override
	public int getCount() {
		return imageUrlList.size();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		SimpleImageLoader.loadImageFromLocalCacheAndNetworkDownload(imageView, imageUrlList.get(position), null);
		imageView.setScaleType(scaleType);
		imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return imageView;
	}
	
}
