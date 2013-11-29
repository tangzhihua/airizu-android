package cn.airizu.image.local_cache_and_network_download;

import android.graphics.Bitmap;

/**
 * 
 * @author zhihua.tang
 *
 */
public interface IImageLoaderImageDownlondFinishedCallback {
	void imageDownloadFinishedCallback(final String imageUrl, final Bitmap bitmapForJustDownloaded);
}
