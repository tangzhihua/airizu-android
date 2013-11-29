package cn.airizu.toolutils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class BitmapTools {
	private BitmapTools() {
		
	}
	
	public static Bitmap zoomBitmap(Bitmap srcBitmap, float destWidth, float destHeigth) {
		if (srcBitmap == null || destWidth <= 0 || destWidth >= Float.MAX_VALUE || destHeigth <= 0 || destHeigth >= Float.MAX_VALUE) {
			return null;
		}
		
		int w = srcBitmap.getWidth();// 源文件的大小
		int h = srcBitmap.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = destWidth / w;// 宽度缩小比例
		float scaleHeight = destHeigth / h;// 高度缩小比例
		Matrix matrix = new Matrix();// 矩阵
		matrix.postScale(scaleWidth, scaleHeight);// 设置矩阵比例
		Bitmap newBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, matrix, true);// 直接按照矩阵的比例把源文件画入进行
		return newBitmap;
	}
}
