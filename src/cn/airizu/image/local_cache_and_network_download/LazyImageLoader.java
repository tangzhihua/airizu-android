package cn.airizu.image.local_cache_and_network_download;

import java.lang.Thread.State;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import cn.airizu.toolutils.DebugLog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public final class LazyImageLoader {
	private final String TAG = this.getClass().getSimpleName();
	
	private static LazyImageLoader instance = null;
	
	public synchronized static LazyImageLoader getInstance() {
		if (null == instance) {
			instance = new LazyImageLoader();
		}
		
		return instance;
	}
	
	/**
	 * 加载目标图片(先从本地加载, 本地没有再从网络侧下载)
	 * 
	 * @param imageUrl
	 * @param imageDownloadFinishedCallback
	 * @return
	 */
	public Bitmap loadImageFromLocalCacheAndNetworkDownload(final String imageUrl, final IImageLoaderImageDownlondFinishedCallback imageDownloadFinishedCallback) {
		
		if (TextUtils.isEmpty(imageUrl) || imageDownloadFinishedCallback == null) {
			DebugLog.e(TAG, "image imageUrl or downloadFinishedCallback is null ! ");
			return null;
		}
		
		// 优先从本地缓存中加载目标图片
		Bitmap bitmap = imageLoaderLocalImageCacheManager.getImageFromLocalAndCache(imageUrl);
		
		// 如果本地没有目标图片的缓存(有可能加载失败), 就从网络侧下载
		if (bitmap == null) {
			
			// 缓存当前图片下载回调方法
			imageNetworkDownloadCallbackManager.add(imageUrl, imageDownloadFinishedCallback);
			// 开启一个图片下载线程, 开始下载目标图片
			this.startImageDownloadTread(imageUrl);
		}
		
		return bitmap;
	}
	
	/**
	 * 加载目标图片(只从本地缓存加载)
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap loadImageFromLocalCache(final String imageUrl) {
		
		if (TextUtils.isEmpty(imageUrl)) {
			DebugLog.e(TAG, "image imageUrl is null ! ");
			return null;
		}
		
		// 优先从本地缓存中加载目标图片
		return imageLoaderLocalImageCacheManager.getImageFromLocalAndCache(imageUrl);
	}
	
	/**
	 * 加载目标图片(如果本地有图片缓存, 先删除本地的图片缓存, 然后再通过网络下载目标图片)
	 * 
	 * @param imageUrl
	 * @return
	 */
	public void loadImageFromNetworkDownload(final String imageUrl, final IImageLoaderImageDownlondFinishedCallback imageDownloadFinishedCallback) {
		
		if (TextUtils.isEmpty(imageUrl) || imageDownloadFinishedCallback == null) {
			DebugLog.e(TAG, "image imageUrl or downloadFinishedCallback is null ! ");
			return;
		}
		
		// 如果本地有图片缓存, 先删除本地的图片缓存, 然后再通过网络下载目标图片
		imageLoaderLocalImageCacheManager.deleteImageFromLocalFileAndMemory(imageUrl);
		
		// 缓存当前图片下载回调方法
		imageNetworkDownloadCallbackManager.add(imageUrl, imageDownloadFinishedCallback);
		// 开启一个图片下载线程, 开始下载目标图片
		this.startImageDownloadTread(imageUrl);
	}
	
	/**
	 * 取消一个图片的下载完成回调
	 * 
	 * @param imageUrl
	 * @param imageDownloadFinishedCallback
	 */
	public void cancelAImageDownloadFinishedCallback(final String imageUrl, final IImageLoaderImageDownlondFinishedCallback imageDownloadFinishedCallback) {
		if (TextUtils.isEmpty(imageUrl) || imageDownloadFinishedCallback == null) {
			DebugLog.e(TAG, "image imageUrl or downloadFinishedCallback is null ! ");
			return;
		}
		
		imageNetworkDownloadCallbackManager.remove(imageUrl, imageDownloadFinishedCallback);
	}
	
	//
	private ImageLoaderImageLoadManager imageLoaderLocalImageCacheManager;
	// 图片URL缓存 阻塞队列
	private BlockingQueue<String> imageUrlCacheBlockingQueue;
	//
	private ImageDownloadThread imageDownloadThread;
	// 图片下载回调管理
	private ImageLoaderCallbackManager imageNetworkDownloadCallbackManager;
	
	private LazyImageLoader() {
		imageLoaderLocalImageCacheManager = new ImageLoaderImageLoadManager();
		imageUrlCacheBlockingQueue = new LinkedBlockingQueue<String>();
		imageDownloadThread = new ImageDownloadThread();
		imageNetworkDownloadCallbackManager = new ImageLoaderCallbackManager();
	}
	
	private void startImageDownloadTread(final String imageUrl) {
		
		// TODO : 这里会发生阻塞, 这里阻塞很危险, 会导致ANR, 所以还是不要使用 BlockingQueue
		addImageUrlToUrlQueue(imageUrl);
		
		State state = imageDownloadThread.getState();
		switch (state)
		{
			// 线程被阻塞，在等待一个锁。
			case BLOCKED: {
			}
			break;
			
			// 线程已被创建，但从未启动
			case NEW: {
				imageDownloadThread.start();
			}
			break;
			
			// 线程可能已经运行
			case RUNNABLE: {
			}
			break;
			
			// 线程已被终止
			case TERMINATED: {
				// note : 如果下载线程被异常终止了, 就重新创建一个
				imageDownloadThread = new ImageDownloadThread();
				imageDownloadThread.start();
			}
			break;
			
			// 线程正在等待一个指定的时间。
			case TIMED_WAITING: {
			}
			break;
			default:
			break;
		}
	}
	
	private void addImageUrlToUrlQueue(final String imageUrl) {
		
		if (!imageUrlCacheBlockingQueue.contains(imageUrl)) {
			try {
				// put(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue没有空间，则调用此方法的线程被阻断直到BlockingQueue里有空间再继续。
				// imageUrlCacheBlockingQueue.put(imageUrl);
				// add(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue可以容纳，则返回true，否则抛出异常。
				imageUrlCacheBlockingQueue.add(imageUrl);
				// offer(anObject)：表示如果可能的话，将anObject加到BlockingQueue里，即如果BlockingQueue可以容纳，则返回true，否则返回false。
			} catch (IllegalStateException e) {
				// if the element cannot be added at this time due to capacity restrictions
				e.printStackTrace();
			} catch (ClassCastException e) {
				// if the class of the specified element prevents it from being added to this queue
				e.printStackTrace();
			} catch (NullPointerException e) {
				// if the specified element is null
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// if some property of the specified element prevents it from being added to this queue
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static enum HandlerExtraDataKeyEnum {
		//
		IMAGE_URL,
		//
		IMAGE_OBJECT
	};
	
	private static enum HandlerMsgTypeEnum {
		//
		DOWNLOAD_IMAGE_FINISHED
	};
	
	private Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			
			if (msg.what == HandlerMsgTypeEnum.DOWNLOAD_IMAGE_FINISHED.ordinal()) {
				Bundle bundle = msg.getData();
				if (bundle != null) {
					String imageUrl = bundle.getString(HandlerExtraDataKeyEnum.IMAGE_URL.name());
					Bitmap bitmap = bundle.getParcelable(HandlerExtraDataKeyEnum.IMAGE_OBJECT.name());
					imageNetworkDownloadCallbackManager.notifyAllDownloadListenersOfThisImage(imageUrl, bitmap);
				}
			}
		};
	};
	
	private class ImageDownloadThread extends Thread {
		public void run() {
			try {
				
				// 这里会一直下载图片URL缓存队列中的图片
				while (true) {
					// poll()：取走BlockingQueue里排在首位的对象，若队列为空则返回null。
					// String imageUrl = imageUrlCacheBlockingQueue.poll();
					// take()：取走BlockingQueue里排在首位的对象，若BlockingQueue为空，阻断进入等待状态直到BlockingQueue有新的对象被加入为止。
					String imageUrl = imageUrlCacheBlockingQueue.take();
					if (imageUrl == null) {
						continue;
					}
					
					Bitmap bitmap = imageLoaderLocalImageCacheManager.getImageFromNetworkAndCache(imageUrl);
					
					Message msg = handler.obtainMessage(HandlerMsgTypeEnum.DOWNLOAD_IMAGE_FINISHED.ordinal());
					Bundle bundle = msg.getData();
					bundle.putSerializable(HandlerExtraDataKeyEnum.IMAGE_URL.name(), imageUrl);
					bundle.putParcelable(HandlerExtraDataKeyEnum.IMAGE_OBJECT.name(), bitmap);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

// BlockingQueue是一种特殊的Queue，若BlockingQueue是空的，
//
// 从BlockingQueue取东西的操作将会被阻断进入等待状态直到BlocingkQueue进了新货才会被唤醒。
// 同样，如果BlockingQueue是满的任何试图往里存东西的操作也会被阻断进入等待状态，
// 直到BlockingQueue里有新的空间才会被唤醒继续操作。
// BlockingQueue提供的方法主要有：
// add(anObject): 把anObject加到BlockingQueue里，如果BlockingQueue可以容纳返回true，否则抛出IllegalStateException异常。
// offer(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue可以容纳返回true，否则返回false。
// put(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue没有空间，调用此方法的线程被阻断直到BlockingQueue里有新的空间再继续。
// poll(time)：取出BlockingQueue里排在首位的对象，若不能立即取出可等time参数规定的时间。取不到时返回null。
// take()：取出BlockingQueue里排在首位的对象，若BlockingQueue为空，阻断进入等待状态直到BlockingQueue有新的对象被加入为止。
//
// 根据不同的需要BlockingQueue有4种具体实现：
// （1）ArrayBlockingQueue：规定大小的BlockingQueue，其构造函数必须带一个int参数来指明其大小。其所含的对象是以FIFO（先入先出）顺序排序的。
// （2）LinkedBlockingQueue：大小不定的BlockingQueue，若其构造函数带一个规定大小的参数，生成的BlockingQueue有大小限制，
// 若不带大小参数，所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定。其所含的对象是以FIFO（先入先出）顺序排序的。
// LinkedBlockingQueue和ArrayBlockingQueue比较起来，它们背后所用的数据结构不一样，
// 导致LinkedBlockingQueue的数据吞吐量要大于ArrayBlockingQueue，但在线程数量很大时其性能的可预见性低于ArrayBlockingQueue。
// （3）PriorityBlockingQueue：类似于LinkedBlockingQueue，但其所含对象的排序不是FIFO，而是依据对象的自然排序顺序或者是构造函数所带的Comparator决定的顺序。
// （4）SynchronousQueue：特殊的BlockingQueue，对其的操作必须是放和取交替完成的。
//
// 下面是用BlockingQueue来实现Producer和Consumer的例子
//
