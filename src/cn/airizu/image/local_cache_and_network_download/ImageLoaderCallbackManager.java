package cn.airizu.image.local_cache_and_network_download;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * 
 * @author zhihua.tang
 *
 */
public class ImageLoaderCallbackManager {
	
	// ConcurrentHashMap是一个线程安全的Hash Table
	// 图片下载回调缓存集合, 一个图片URL可能对应对应多个 ImageLoaderCallback
	private ConcurrentHashMap<String, ArrayList<IImageLoaderImageDownlondFinishedCallback>> imageDownloadCallbackCache;
	
	public ImageLoaderCallbackManager() {
		imageDownloadCallbackCache = new ConcurrentHashMap<String, ArrayList<IImageLoaderImageDownlondFinishedCallback>>();
	}
	
	/**
	 * 增加一个新的图片下载回调方法
	 * 
	 * @param imageUrl
	 * @param imageDownloadFinishedCallback
	 */
	public void add(final String imageUrl, final IImageLoaderImageDownlondFinishedCallback imageDownloadFinishedCallback) {
		if (TextUtils.isEmpty(imageUrl) || imageDownloadFinishedCallback == null) {
			// 入参非法
			return;
		}
		
		if (!imageDownloadCallbackCache.contains(imageUrl)) {
			// 一个图片URL, 可能对应多个下载回调方法
			imageDownloadCallbackCache.put(imageUrl, new ArrayList<IImageLoaderImageDownlondFinishedCallback>());
		}
		
		imageDownloadCallbackCache.get(imageUrl).add(imageDownloadFinishedCallback);
	}
	
	/**
	 * 删除一个图片下载回调方法(这就意味着, 调用方取消了之前的图片下载请求)
	 * 
	 * @param imageUrl
	 * @param imageDownloadFinishedCallback
	 */
	public void remove(final String imageUrl, final IImageLoaderImageDownlondFinishedCallback imageDownloadFinishedCallback) {
		if (TextUtils.isEmpty(imageUrl) || imageDownloadFinishedCallback == null) {
			// 入参非法
			return;
		}
		
		if (imageDownloadCallbackCache.contains(imageUrl)) {
			imageDownloadCallbackCache.get(imageUrl).remove(imageDownloadFinishedCallback);
		}
	}
	
	/**
	 * 通知所有跟这个图片有关的下载监听者, 图片已经下载完成
	 * 
	 * @param imageUrl
	 * @param bitmapForJustDownloaded
	 */
	public void notifyAllDownloadListenersOfThisImage(final String imageUrl, final Bitmap bitmapForJustDownloaded) {
		
		do {
			if (TextUtils.isEmpty(imageUrl)) {
				// 入参异常
				break;
			}
			
			List<IImageLoaderImageDownlondFinishedCallback> callbacks = imageDownloadCallbackCache.get(imageUrl);
			if (callbacks == null) {
				// 没有目标图片的 "图片下载回调方法"
				break;
			}
			
			for (IImageLoaderImageDownlondFinishedCallback callback : callbacks) {
				if (callback != null) {
					// 通知上层调用者, 目标图片已经下载完成
					callback.imageDownloadFinishedCallback(imageUrl, bitmapForJustDownloaded);
				}
			}
			
			// 清空图片下载回调方法缓存
			callbacks.clear();
			imageDownloadCallbackCache.remove(imageUrl);
		} while (false);
	}
}

// ConcurrentHashMap是一个线程安全的Hash Table，它的主要功能是提供了一组和HashTable功能相同但是线程安全的方法。
// ConcurrentHashMap可以做到读取数据不加锁，并且其内部的结构可以让其在进行写操作的时候能够将锁的粒度保持地尽量地小，
// 不用对整个ConcurrentHashMap加锁。
