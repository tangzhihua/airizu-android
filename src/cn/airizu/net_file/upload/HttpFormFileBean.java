package cn.airizu.net_file.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.text.TextUtils;

// 这是一个javabean, 用来封装http协议的一些数据
// Content-Disposition: form-data; name="这里填写参数名"; filename="这里填写完整文件名,如congsmall.png" Content-Type: image/x-png
// 用于使用Form的方式上传文件
public final class HttpFormFileBean {
	// 代表文件的数据, 以二进制格式存储
	private byte[] data;
	// 上传大文件时, 要使用输入流
	private InputStream inStream;
	// 代表我们要上传的那个文件
	private File file;
	// 文件名称 : 对应http协议(filename="这里填写完整文件名,如congsmall.png")
	private String fileFullName;
	// 请求参数名称 : 对应http协议(name="这里填写参数名")
	private String parameterName;
	// 内容类型 : 对应http协议(Content-Type: image/x-png)
	private String contentType;
	
	/**
	 * 用于上传小文件的情况, 可以在外部直接将文件数据读入内存中 (大小为几百K)
	 * 
	 * @param fileFullName : 文件完整的名称(包括了扩展名, 如 tangzhihua.png)
	 * @param data : 需要上传的文件数据(二进制格式)
	 * @param httpParameterName 上传文件的请求参数名称
	 * @param contentType 上传文件的内容类型(这个要跟HTTP协议对应)
	 */
	public HttpFormFileBean(final String fileFullName, 
			                    final byte[] data, 
			                    final String httpParameterName, 
			                    final String contentType) throws Exception {
		
		if (TextUtils.isEmpty(fileFullName) || data == null || data.length <= 0 || TextUtils.isEmpty(httpParameterName) || TextUtils.isEmpty(contentType)) {
			throw new IllegalArgumentException("所有参数都不能为空 ! ");
		}
		this.data = data;
		this.fileFullName = fileFullName;
		this.parameterName = httpParameterName;
		this.contentType = contentType;
	}
	
	/**
	 * 用于上传大文件的情况, 因为文件很大时, 比如100兆,而手机内存只有60兆,就不能将文件数据读入内存中,所以不能使用第一种构造方法 1兆以上
	 * 
	 * @param fileFullName
	 * @param file : 要上传的文件对象
	 * @param httpParameterName 上传文件的请求参数名称
	 * @param contentType 上传文件的内容类型(这个要跟HTTP协议对应)
	 */
	public HttpFormFileBean(final String fileFullName, 
			                    final File file, 
			                    final String httpParameterName, 
			                    final String contentType) throws Exception {
		
		if (TextUtils.isEmpty(fileFullName) || file == null || file.length() <= 0 || TextUtils.isEmpty(httpParameterName) || TextUtils.isEmpty(contentType)) {
			throw new IllegalArgumentException("所有参数都不能为空 ! ");
		}
		
		this.fileFullName = fileFullName;
		this.parameterName = httpParameterName;
		this.file = file;
		// 对于文件数据很大的情况, 我们可以创建目标文件的输入流, 这样我们就可以做到一边读取文件输入流,
		// 一边将读出的数据写入网络输出流中, 这样就可以很少的占用手机内存
		this.inStream = new FileInputStream(file);
		this.contentType = contentType;
	}
	
	public File getFile() {
		return file;
	}
	
	public InputStream getInStream() {
		return inStream;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getFileFullName() {
		return fileFullName;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void closeFileStream() {
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inStream = null;
		}
	}
}
