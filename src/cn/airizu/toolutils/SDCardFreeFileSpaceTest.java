package cn.airizu.toolutils;

import android.os.Environment;
import android.os.StatFs;

public final class SDCardFreeFileSpaceTest {
	
	private final static String TAG = SDCardFreeFileSpaceTest.class.getSimpleName();
	
	private SDCardFreeFileSpaceTest() {
		
	}
	
	/**
	 * 判断是否有足够的空间供下载
	 * 
	 * @param downloadSize
	 * @return
	 */
	public static boolean isEnoughForDownload(long downloadSize) {
		
		long freeSpace = sdCardFreeFileSpace();
		if (freeSpace < downloadSize) {
			return false;
		}
		
		return true;
	}
	
	public static long sdCardFreeFileSpace() {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		
		// SD卡分区数
		int blockCounts = statFs.getBlockCount();
		
		DebugLog.e(TAG, "BlockCount=" + blockCounts);
		
		// SD卡可用分区数
		int avCounts = statFs.getAvailableBlocks();
		
		DebugLog.e(TAG, "AvailableBlocks=" + avCounts);
		
		// 一个分区数的大小
		long blockSize = statFs.getBlockSize();
		
		DebugLog.e(TAG, "BlockSize=" + blockSize);
		
		// SD卡可用空间
		long freeSpace = avCounts * blockSize;
		
		return freeSpace;
	}
}
