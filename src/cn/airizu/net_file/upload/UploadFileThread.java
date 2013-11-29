package cn.airizu.net_file.upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.airizu.domain.net_error_handle.FileDownloadErrorHandle;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.NetFileHelper;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleCookieSingleton;
import cn.airizu.toolutils.StreamTools;

public final class UploadFileThread extends Thread {
	private final String TAG = this.getClass().getSimpleName();
	
	private UploadFileNetRequestEvent requestEvent = null;
	private Handler netFacadeSingletonHandler = null;
	
	public UploadFileThread(UploadFileNetRequestEvent requestEvent, Handler handler) throws Exception {
		
		if (requestEvent == null || handler == null) {
			throw new IllegalArgumentException("入参为空, requestEvent/handler 不能为空 ! ");
		}
		
		this.requestEvent = requestEvent;
		this.netFacadeSingletonHandler = handler;
	}
	
	private static final String BOUNDARY = "---------------------------7dc38a1c609f6"; // 数据分隔线
	private static final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志
	
	/**
	 * @param textParameterMap
	 * @return
	 */
	private String getTextDataProtocolEntity(final Map<String, String> textParameterMap) {
		
		final StringBuilder textEntity = new StringBuilder();
		if (textParameterMap != null && textParameterMap.size() > 0) {
			
			for (Map.Entry<String, String> entry : textParameterMap.entrySet()) {// 构造文本类型参数的实体数据
				textEntity.append("--");
				textEntity.append(BOUNDARY);
				textEntity.append("\r\n");
				textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
				textEntity.append(entry.getValue());
				textEntity.append("\r\n");
			}
			
		}
		
		return textEntity.toString();
	}
	
	private String getFileEntityDataHttpProcotolHeadText(final HttpFormFileBean fileBean) {
		final StringBuilder fileEntity = new StringBuilder();
		if (fileBean != null) {
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\"" + fileBean.getParameterName() + "\";filename=\"" + fileBean.getFileFullName() + "\"\r\n");
			fileEntity.append("Content-Type: " + fileBean.getContentType() + "\r\n\r\n");
		}
		return fileEntity.toString();
	}
	
	/**
	 * 要上传的文件性质的数据的总长度(包括文件数据/分割线/回车换行符号/各种HTTP协议字段)
	 * 
	 * @param fileBean
	 * @return
	 * @throws Exception
	 */
	private long getFileDataProtocolTextTotalLength(final HttpFormFileBean fileBean) throws Exception {
		// 下面代码是为了计算 Content-Length 这个参数, 它是实体数据的总长度,
		// 实体数据包括:分割线/回车换行/文件数据/各种协议字段
		long fileDataTotalLength = 0;// 要上传的文件性质的数据的总长度(包括文件数据/分割线/回车换行符号/各种HTTP协议字段)
		if (fileBean != null) {
			final StringBuilder fileEntity = new StringBuilder();
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\"" + fileBean.getParameterName() + "\";filename=\"" + fileBean.getFileFullName() + "\"\r\n");
			fileEntity.append("Content-Type: " + fileBean.getContentType() + "\r\n\r\n");
			fileEntity.append("\r\n");
			fileDataTotalLength += fileEntity.length();
			
			if (fileBean.getInStream() != null) {
				// 上传的是大文件
				fileDataTotalLength += fileBean.getFile().length();
			} else {
				// 上传的是小文件
				fileDataTotalLength += fileBean.getData().length;
			}
		}
		
		return fileDataTotalLength;
	}
	
	private void sendHttpHeadProtocolToServer(final OutputStream outStream, final String pathString, final String hostString, final int portInt, final long dataTotalLength) throws Exception {
		// 下面完成HTTP请求头的发送
		// POST /videoweb/video/manage.do HTTP/1.1
		final String requestmethod = "POST " + pathString + " HTTP/1.1\r\n";
		outStream.write(requestmethod.getBytes());
		// Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword,
		// application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*
		final String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		outStream.write(accept.getBytes());
		// Accept-Language: zh-cn
		final String language = "Accept-Language: zh-CN\r\n";
		outStream.write(language.getBytes());
		// Content-Type: multipart/form-data; boundary=---------------------------7da2137580612
		final String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
		outStream.write(contenttype.getBytes());
		// Content-Length: 4927
		final String contentlength = "Content-Length: " + dataTotalLength + "\r\n";
		outStream.write(contentlength.getBytes());
		// Connection: Keep-Alive
		final String alive = "Connection: Keep-Alive\r\n";
		outStream.write(alive.getBytes());
		// session
		final String session = "Cookie: " + SimpleCookieSingleton.getInstance().getCookieString();
		outStream.write(session.getBytes());
		outStream.write("\r\n".getBytes());
		// Host: 192.168.56.1:8080
		final String host = "Host: " + hostString + ":" + portInt + "\r\n";
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议要再写一个回车换行符
		outStream.write("\r\n".getBytes());
	}
	
	private void sendHttpBodyProtocalToServer(final OutputStream outStream, final String textEntityString, final HttpFormFileBean fileParameter,
			final INetFileProgressCallback uploadProgressCallback) throws Exception {
		
		if (outStream == null) {
			return;
		}
		
		if (!TextUtils.isEmpty(textEntityString)) {
			outStream.write(textEntityString.getBytes());
		}
		
		// 再把所有文件类型的实体数据发送出来
		if (fileParameter != null) {
			final String fileEntityDataHttpProcotolHeadText = getFileEntityDataHttpProcotolHeadText(fileParameter);
			if (!TextUtils.isEmpty(fileEntityDataHttpProcotolHeadText)) {
				outStream.write(fileEntityDataHttpProcotolHeadText.toString().getBytes());
			}
			
			/**
			 * 从这里开始是 文件数据 部分
			 */
			if (fileParameter.getInStream() != null) {
				// 如果这个文件是通过输入流的方式构造的, 就一边读一边写(这是发送大文件的情况)
				final byte[] buffer = new byte[1024];
				int count = 0;
				final long fileTotalSize = fileParameter.getFile().length();
				long completedSize = 0;// 已下载完成的文件size
				while ((count = fileParameter.getInStream().read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, count);
					
					// 因为下载过程需要跟UI线程通信频繁,
					// 所以这里不使用handle桥接给controller了,而是直接调用controller层的回调方法
					if (uploadProgressCallback != null) {
						uploadProgressCallback.netFileProgressCallbackInNonUIThread(fileTotalSize, completedSize);
					}
					
					// ----------------------------------------------------------------------------
					if (isInterrupted()) {
						// DebugLog.i(TAG, "--> this file upload thread(" + threadID + ") is canceled");
						break;
					}
					// ----------------------------------------------------------------------------
				}
				fileParameter.getInStream().close();
			} else {
				int completedSize = 0;
				final int fileTotalSize = (int) fileParameter.getData().length;
				int count = fileTotalSize < 1024 ? fileTotalSize : 1024;// 已下载完成的文件size
				int offset = 0;
				while (count > 0) {
					
					outStream.write(fileParameter.getData(), offset, count);
					completedSize += count;
					
					// 因为下载过程需要跟UI线程通信频繁,
					// 所以这里不使用handle桥接给controller了,而是直接调用controller层的回调方法
					if (uploadProgressCallback != null) {
						uploadProgressCallback.netFileProgressCallbackInNonUIThread(fileTotalSize, completedSize);
					}
					
					// ----------------------------------------------------------------------------
					if (isInterrupted()) {
						// DebugLog.i(TAG, "--> this file upload thread(" + threadID + ") is canceled");
						break;
					}
					// ----------------------------------------------------------------------------
					
					offset += count;
					count = offset + 1024 > fileTotalSize ? (fileTotalSize - offset) : 1024;
				}
			}
			
			outStream.write("\r\n".getBytes());
		}
	}
	
	private void sendHttpProtocalToServer(final OutputStream outStream, final String pathString, final String hostString, final int portInt, final long dataTotalLength,
			final String textEntityString, final HttpFormFileBean fileParameter, final INetFileProgressCallback uploadProgressCallback) throws Exception {
		do {
			// ----------------------------------------------------------------------------
			if (isInterrupted()) {
				break;
			}
			// ----------------------------------------------------------------------------
			sendHttpHeadProtocolToServer(outStream, pathString, hostString, portInt, dataTotalLength);
			
			// ----------------------------------------------------------------------------
			if (isInterrupted()) {
				break;
			}
			// ----------------------------------------------------------------------------
			sendHttpBodyProtocalToServer(outStream, textEntityString, fileParameter, uploadProgressCallback);
			
			// 下面发送数据结束标志，表示数据已经结束
			outStream.write(endline.getBytes());
		} while (false);
	}
	
	@Override
	public void run() {
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     download file thread begin (" + requestEvent.getThreadID() + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		final NetErrorBean netErrorForOUT = new NetErrorBean();
		
		Socket socket = null;
		OutputStream outStream = null;
		BufferedReader reader = null;
		
		// 文件上传的路径
		final String uploadPath = requestEvent.getNetUrlPath();
		// 文本参数
		final Map<String, String> textParameterMap = requestEvent.getTextParameterMap();
		// 文件参数
		final HttpFormFileBean fileParameter = requestEvent.getFileParameter();
		// 上传进度回调方法
		final INetFileProgressCallback uploadProgressCallback = requestEvent.getProgressCallback();
		
		try {
			do {
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					DebugLog.i(TAG, "--> this file upload thread(" + requestEvent.getThreadID() + ") is canceled");
					break;
				}
				// ----------------------------------------------------------------------------
				
				// 文件HTTP协议文本总长度
				final long fileDataTotalLength = getFileDataProtocolTextTotalLength(fileParameter);
				
				// 文本数据实体字符串
				final String textDataEntityString = getTextDataProtocolEntity(textParameterMap);
				final int textDataLength = textDataEntityString.getBytes().length;
				
				// 计算传输给服务器的实体数据总长度
				final long dataTotalLength = textDataLength + fileDataTotalLength + endline.getBytes().length;
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					DebugLog.i(TAG, "--> this file upload thread(" + requestEvent.getThreadID() + ") is canceled");
					break;
				}
				// ----------------------------------------------------------------------------
				
				/**
				 * 开始编写HTTP协议部分, 并且输出到网络
				 */
				final URL url = new URL(uploadPath);
				/**
				 * 首先获取端口号, 如果采用的是默认端口(80), getPort()方法返回的是-1,否则就是具体端口号 http://www.ba.com/xxx.do 这样的就是采用默认端口号的URL
				 */
				final int port = url.getPort() == -1 ? 80 : url.getPort();
				/**
				 * 首先建立一个socket连接 InetAddress dstAddress : 主机名 int dstPort : 端口号
				 */
				socket = new Socket(InetAddress.getByName(url.getHost()), port);
				// 取出输出流(此时是要从客户端发送数据到服务器)
				outStream = socket.getOutputStream();
				
				sendHttpProtocalToServer(outStream, url.getPath(), url.getHost(), port, dataTotalLength, textDataEntityString, fileParameter, uploadProgressCallback);
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					DebugLog.i(TAG, "--> this file upload thread(" + requestEvent.getThreadID() + ") is canceled");
					break;
				}
				// ----------------------------------------------------------------------------
				
				/**
				 * 下面是服务器响应客户端的请求 我们首先建立一个 缓冲读写器(通过socket的输入流构造, 此时是服务器向客户端发送反馈数据)
				 */
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					// HTTP/1.1 200 OK
					String lineString = reader.readLine();
					int httpCode = 0;
					if (!TextUtils.isEmpty(lineString)) {
						int httpCodeStartIndex = lineString.indexOf(" ") + 1;
						int httpCodeEndIndex = lineString.lastIndexOf(" ");
						if (httpCodeStartIndex != -1 && httpCodeEndIndex != -1) {
							httpCode = Integer.valueOf(lineString.substring(httpCodeStartIndex, httpCodeEndIndex)).intValue();
						}
					}
					
					String messageString = "";
					// {"message":"\u4FEE\u6539\u7528\u6237\u4FE1\u606F\u6210\u529F"}
					while ((lineString = reader.readLine()) != null) {
						DebugLog.i(TAG, lineString);
						
						if (lineString.contains("message")) {
							int messageStartIndex = lineString.indexOf(":") + 1;
							int messageEndIndex = lineString.lastIndexOf("\"");
							if (messageStartIndex != -1 && messageEndIndex != -1) {
								messageString = lineString.substring(messageStartIndex + 1, messageEndIndex);
								break;
							}
						}
					}
					
					netErrorForOUT.setErrorCode(httpCode);
					if (!TextUtils.isEmpty(messageString)) {
						netErrorForOUT.setErrorMessage(StreamTools.toUTF8String(messageString));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS);
			} while (false);
			
		} catch (Exception e) {
			e.printStackTrace();
			netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
		} finally {
			
			if (isInterrupted()) {
				// 外部取消了当前线程
				DebugLog.i(TAG, "--> this file upload thread(" + requestEvent.getThreadID() + ") is canceled");
				netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_THREAD_IS_CANCELED);
			}
			
			// 根据错误类型, 错误代码, 获取对应的详细错误信息
			netErrorForOUT.setErrorMessage(FileDownloadErrorHandle.getErrorMessage(netErrorForOUT.getErrorType(), netErrorForOUT.getErrorCode()));
			DebugLog.i(TAG, "netErr-->" + netErrorForOUT.toString());
			
			if (fileParameter != null) {
				fileParameter.closeFileStream();
			}
			
			// 释放相关资源
			if (outStream != null) {
				try {
					outStream.flush();
					outStream.close();
					outStream = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (socket != null) {
				try {
					socket.close();
					socket = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			final Message msg = new Message();
			msg.what = NetFileHelper.HandlerMsgTypeEnum.FINISH_CALLBACK.ordinal();
			msg.getData().putInt(NetFileHelper.HandlerMsgExtraDataTypeEnum.FINISHED_THREAD_ID.name(), requestEvent.getThreadID());
			msg.getData().putSerializable(NetFileHelper.HandlerMsgExtraDataTypeEnum.FINISHED_ERROR_OBJECT.name(), netErrorForOUT);
			netFacadeSingletonHandler.sendMessage(msg);
			
			//
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, "         ----- download file thread end(" + requestEvent.getThreadID() + ") -----          ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			
			/**
			 * 这两个外部传进来的对象引用, 必须设成null, 否则会照成死锁.
			 */
			requestEvent = null;
			netFacadeSingletonHandler = null;
		}
		
	}
}
