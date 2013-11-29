package cn.airizu.toolutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

public final class StreamTools {
	
	private StreamTools() {
		
	}
	
	private static final int READ_BUF_SIZE = 1024;
	
	/**
	 * 从输入流读取数据
	 * 
	 * @param inStream
	 * @return byte[]数据
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream inStream) throws IOException {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[READ_BUF_SIZE];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	
	public static String toUTF8String(String s) {
		String utf8 = "";
		
		do {
			if (TextUtils.isEmpty(s)) {
				break;
			}
			
			try {
				utf8 = new String(s.getBytes("UTF-8"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				utf8 = "";
			}
		} while (false);
		return utf8;
	}
	
}
