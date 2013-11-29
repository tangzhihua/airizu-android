package cn.airizu.toolutils;

import cn.airizu.image.local_cache_and_network_download.IImageLoaderImageDownlondFinishedCallback;
import cn.airizu.image.local_cache_and_network_download.LazyImageLoader;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

public final class SimpleImageLoader {
	private static String TAG = "SimpleImageLoader";
	
	private SimpleImageLoader() {
		
	}
	
	/**
	 * 加载一个图片到ImageView中, 如果本地有该图片的缓存, 就直接加载, 否则从网络侧下载后加载.
	 * 
	 * @param srcImageView
	 * @param imageUrl
	 * @param defaultBitmap
	 */
	public static void loadImageFromLocalCacheAndNetworkDownload(ImageView srcImageView, String imageUrl, Bitmap defaultBitmap) {
		do {
			if (srcImageView == null) {
				DebugLog.e(TAG, "imageView is null ! ");
				break;
			}
			if (TextUtils.isEmpty(imageUrl)) {
				DebugLog.e(TAG, "imageUrl is null ! ");
				break;
			}
			Bitmap bitmap = LazyImageLoader.getInstance().loadImageFromLocalCacheAndNetworkDownload(imageUrl, loadingImageFinishCallback(imageUrl, srcImageView));
			if (bitmap == null) {
				bitmap = defaultBitmap;
			}
			srcImageView.setImageBitmap(bitmap);
		} while (false);
	}
	
	/**
	 * 仅从本地缓存加载目标图片, 如果没有, 不会通过网络下载目标图片
	 * 
	 * @param srcImageView
	 * @param imageUrl
	 * @param defaultBitmap
	 */
	public static void loadImageFromLocalCache(ImageView srcImageView, String imageUrl, Bitmap defaultBitmap) {
		do {
			if (srcImageView == null) {
				DebugLog.e(TAG, "imageView is null ! ");
				break;
			}
			if (TextUtils.isEmpty(imageUrl)) {
				DebugLog.e(TAG, "imageUrl is null ! ");
				break;
			}
			
			Bitmap bitmap = LazyImageLoader.getInstance().loadImageFromLocalCache(imageUrl);
			if (bitmap == null) {
				bitmap = defaultBitmap;
			}
			srcImageView.setImageBitmap(bitmap);
		} while (false);
	}
	
	/**
	 * 直接从网络侧下载目标图片, 下载之前会先删除本地已有的目标缓存图片
	 * 
	 * @param srcImageView
	 * @param imageUrl
	 * @param defaultBitmap
	 */
	public static void loadImageFromNetworkDownload(ImageView srcImageView, String imageUrl, Bitmap defaultBitmap) {
		do {
			if (srcImageView == null) {
				DebugLog.e(TAG, "imageView is null ! ");
				break;
			}
			if (TextUtils.isEmpty(imageUrl)) {
				DebugLog.e(TAG, "imageUrl is null ! ");
				break;
			}
			
			srcImageView.setImageBitmap(defaultBitmap);
			LazyImageLoader.getInstance().loadImageFromNetworkDownload(imageUrl, loadingImageFinishCallback(imageUrl, srcImageView));
			
		} while (false);
	}
	
	private static IImageLoaderImageDownlondFinishedCallback loadingImageFinishCallback(final String url, final ImageView imageView) {
		return new IImageLoaderImageDownlondFinishedCallback() {
			public void imageDownloadFinishedCallback(final String imageUrl, final Bitmap bitmapForJustDownloaded) {
				do {
					if (imageView == null) {
						DebugLog.e(TAG, "imageView is null ! ");
						break;
					}
					if (bitmapForJustDownloaded == null) {
						DebugLog.e(TAG, "bitmap is null ! ");
						break;
					}
					imageView.setImageBitmap(bitmapForJustDownloaded);
				} while (false);
			}
		};
	}
}
