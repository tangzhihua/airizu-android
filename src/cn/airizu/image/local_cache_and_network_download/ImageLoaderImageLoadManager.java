package cn.airizu.image.local_cache_and_network_download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpException;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.MD5Util;
import cn.airizu.toolutils.ToolsFunctionForThisProgect;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * 图片加载管理器(如果本地没有缓存, 就从网络侧下载图片)
 * 
 * @author zhihua.tang
 */
public final class ImageLoaderImageLoadManager {
	private final String TAG = this.getClass().getSimpleName();
	
	// 本地图片缓存集合(采用软引用, 关于软引用的说明, 看下边)
	private Map<String, SoftReference<Bitmap>> localImageCacheUseSoftReference;
	
	public ImageLoaderImageLoadManager() {
		localImageCacheUseSoftReference = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	/**
	 * 从本地文件系统和内存中删除目标图片
	 * 
	 * @param imageUrl
	 */
	public void deleteImageFromLocalFileAndMemory(final String imageUrl) {
		if (TextUtils.isEmpty(imageUrl)) {
			return;
		}
		
		// 先从内存中删除目标图片
		localImageCacheUseSoftReference.remove(imageUrl);
		
		// 再从文件系统中删除目标文件
		String imageFullPath = ToolsFunctionForThisProgect.getFiledataCachePath() + "/" + getImageNameForLocalCacheUseMd5Encrypt(imageUrl);
		File imageFile = new File(imageFullPath);
		if (imageFile.exists()) {
			imageFile.delete();
		}
		
	}
	
	/**
	 * 从本地缓存中加载目标图片(本地缓存包括 "内存" + "文件(手机文件系统和SD卡", 并且将加载的图片缓存到内存中
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getImageFromLocalAndCache(final String imageUrl) {
		if (TextUtils.isEmpty(imageUrl)) {
			DebugLog.e(TAG, "入参imageUrl为空 ! ");
			return null;
		}
		
		// 优先从 "内存缓存" 中加载目标图片
		Bitmap bitmap = this.getImageFromMemory(imageUrl);
		// 其次从 "文件缓存" 中加载目标图片
		if (bitmap == null) {
			bitmap = getImageFromFile(imageUrl);
			if (bitmap != null) {
				// 缓存目标图片到内存中
				cacheImageToSoftReferenceList(imageUrl, bitmap);
			}
		}
		
		return bitmap;
	}
	
	/**
	 * 下载目标图片, 并且缓存到本地
	 * 
	 * @param imageUrl
	 * @return
	 * @throws HttpException
	 */
	public Bitmap getImageFromNetworkAndCache(final String imageUrl) {
		
		if (TextUtils.isEmpty(imageUrl)) {
			DebugLog.e(TAG, "入参imageUrl为空 ! ");
			return null;
		}
		
		Bitmap bitmap = null;
		InputStream httpInStream = null;
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(imageUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			String imageEncryptName = getImageNameForLocalCacheUseMd5Encrypt(imageUrl);
			httpInStream = httpURLConnection.getInputStream();
			if (httpInStream != null) {
				String filePathName = saveImageToFileAndReturnFilePathName(imageEncryptName, httpInStream);
				bitmap = BitmapFactory.decodeFile(filePathName);
				if (bitmap != null) {
					// 缓存目标图片到内存中
					cacheImageToSoftReferenceList(imageUrl, bitmap);
				}
			}
			
		} catch (Exception e) {
			DebugLog.e(TAG, "download image failed ! url=" + imageUrl);
			e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				try {
					httpURLConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (httpInStream != null) {
				try {
					httpInStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return bitmap;
	}
	
	/**
	 * 将图片缓存到内存中
	 * 
	 * @param imageUrl
	 * @param bitmap
	 */
	private void cacheImageToSoftReferenceList(final String imageUrl, final Bitmap bitmap) {
		if (TextUtils.isEmpty(imageUrl) || bitmap == null) {
			return;
		}
		
		synchronized (this) {
			if (!localImageCacheUseSoftReference.containsKey(imageUrl)) {
				localImageCacheUseSoftReference.put(imageUrl, new SoftReference<Bitmap>(bitmap));
			}
		}
	}
	
	/**
	 * 从 "内存缓存" 中读取图片(使用 "软引用" 来在内存中缓存图片)
	 * 
	 * @param imageUrl
	 * @return
	 */
	private Bitmap getImageFromMemory(final String imageUrl) {
		
		do {
			Bitmap bitmap = null;
			SoftReference<Bitmap> softReference = null;
			
			synchronized (this) {
				softReference = localImageCacheUseSoftReference.get(imageUrl);
			}
			if (softReference == null) {
				break;
			}
			
			bitmap = softReference.get();
			if (bitmap == null) {
				break;
			}
			return bitmap;
		} while (false);
		
		// 内存中没有目标图片的缓存
		return null;
	}
	
	/**
	 * 从 "文件缓存" 中读取图片
	 * 
	 * @param imageUrl
	 * @return
	 */
	private Bitmap getImageFromFile(final String imageUrl) {
		
		do {
			Bitmap bitmap = null;
			if (TextUtils.isEmpty(imageUrl)) {
				DebugLog.e(TAG, "入参imageUrl为空 ! ");
				break;
			}
			
			String imageName = this.getImageNameForLocalCacheUseMd5Encrypt(imageUrl);
			// 这里已经考虑了SD卡设备存储的双支持
			if (!ToolsFunctionForThisProgect.fileIsExist(imageName)) {
				// 本地文件中没有目标图片的缓存
				break;
			}
			
			//
			FileInputStream fileInputStream = null;
			try {
				// 这里已经考虑了SD卡设备存储的双支持
				fileInputStream = ToolsFunctionForThisProgect.openFileInputByFiledataCachePath(imageName);
				if (fileInputStream != null) {
					bitmap = BitmapFactory.decodeStream(fileInputStream);
				}
			} catch (Exception e) {
				bitmap = null;
				e.printStackTrace();
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if (bitmap == null) {
				// 从文件中加载目标图片失败
				DebugLog.e(TAG, "load image failed from file, file_name=" + imageName + ",image_url=" + imageUrl);
				break;
			}
			return bitmap;
		} while (false);
		
		// 从文件中未加载到目标图片
		return null;
	}
	
	/**
	 * 将图片保存到文件中
	 * 
	 * @param imageEncryptName
	 * @param imageInputStream
	 * @return
	 */
	private String saveImageToFileAndReturnFilePathName(final String imageEncryptName, final InputStream imageInputStream) {
		
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedInputStream = new BufferedInputStream(imageInputStream);
			// 传入 BufferedOutputStream 构造的输入流, 是不能释放的
			bufferedOutputStream = new BufferedOutputStream(ToolsFunctionForThisProgect.openFileOutputByFiledataCachePath(imageEncryptName));
			
			byte[] buffer = new byte[1024];
			int length = 0;
			
			while ((length = bufferedInputStream.read(buffer)) != -1) {
				bufferedOutputStream.write(buffer, 0, length);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if (null != bufferedInputStream) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != bufferedOutputStream) {
				try {
					bufferedOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ToolsFunctionForThisProgect.getFiledataCachePath() + "/" + imageEncryptName;
	}
	
	/**
	 * 获取用于本地缓存的图片名称, 使用MD5加密之后, 保证名字不重复
	 * 
	 * @param imageUrl
	 * @return
	 */
	private String getImageNameForLocalCacheUseMd5Encrypt(final String imageUrl) {
		if (TextUtils.isEmpty(imageUrl)) {
			// 入参为空
			return "";
		}
		
		return MD5Util.getMD5String(imageUrl);
	}
}

// Java中的SoftReference
// 即对象的软引用。如果一个对象具有软引用，内存空间足够，垃 圾回收器就不会回收它；如果内存空间不足了，
// 就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。
// 软引用可用来实现内存敏感的高 速缓存。使用软引用能防止内存泄露，增强程序的健壮性。
// SoftReference的特点是它的一个实例保存对一个Java对象的软引用， 该软引用的存在不妨碍垃圾收集线程对该Java对象的回收。
// 也就是说，一旦SoftReference保存了对一个Java对象的软引用后，在垃圾线程对 这个Java对象回收前，
// SoftReference类所提供的get()方法返回Java对象的强引用。另外，一旦垃圾线程回收该Java对象之 后，get()方法将返回null

// 用Map集合缓存软引用的Bitmap对象

// Map<String, SoftReference<Bitmap>> imageCache = new new HashMap<String, SoftReference<Bitmap>>();
// 强引用的Bitmap对象
// Bitmap bitmap = BitmapFactory.decodeStream(InputStream);
// 软引用的Bitmap对象
// SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(bitmap);
// 添加该对象到Map中使其缓存
// imageCache.put("1",softRbitmap);

// 从缓存中取软引用的Bitmap对象
// SoftReference<Bitmap> bitmapcache_ = imageCache.get("1");
// 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空

// Bitmap bitmap_ = bitmapcache_.get();
// 如果程序中需要从网上加载大量的图片 这时就考虑采用在sdcard上建立临时文件夹缓存这些图片了
