package cn.airizu.net_file.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import cn.airizu.domain.net_error_handle.FileDownloadErrorHandle;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.NetFileHelper;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SDCardFreeFileSpaceTest;

/**
 * @author zhihua.tang
 */
public final class DownloadFileThread extends Thread {
	private final String TAG = this.getClass().getSimpleName();
	
	private DownloadFileNetRequestEvent requestEvent = null;
	private Handler netFacadeSingletonHandler = null;
	
	public DownloadFileThread(DownloadFileNetRequestEvent requestEvent, Handler handler) throws Exception {
		
		if (requestEvent == null || handler == null) {
			throw new IllegalArgumentException("入参为空, requestEvent/handler 不能为空 ! ");
		}
		
		this.requestEvent = requestEvent;
		this.netFacadeSingletonHandler = handler;
	}
	
	@Override
	public void run() {
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     download file thread begin (" + requestEvent.getThreadID() + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		final NetErrorBean netErrorForOUT = new NetErrorBean();
		
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		long fileTotalSize = 0;
		InputStream httpInputStream = null;
		File file = null;
		FileOutputStream fileOutputStream = null;
		long completedSize = 0;// 当前进度
		
		// 文件下载的URL
		final String url = requestEvent.getNetUrlPath();
		// 下载进度回调方法
		final INetFileProgressCallback downloadProgressCallback = requestEvent.getProgressCallback();
		// 目标文件下载完成时, 要被保存到的路径
		final String savePtah = requestEvent.getSavePtah();
		// 目标文件下载完成时, 要保存成这个名字
		final String saveName = requestEvent.getSaveName();
		
		try {
			
			do {
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					break;
				}
				// ----------------------------------------------------------------------------
				
				httpClient = new DefaultHttpClient();
				httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
				if (httpResponse == null) {
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
					break;
				}
				httpEntity = httpResponse.getEntity();
				if (httpEntity == null) {
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
					break;
				}
				// 要下载的文件的总大小
				fileTotalSize = httpEntity.getContentLength();
				DebugLog.i(TAG, "fileTotalSize=" + fileTotalSize);
				if (fileTotalSize <= 0) {
					DebugLog.e(TAG, "fileTotalSize <= 0");
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_SERVER_NET_ERROR);
					break;
				}
				
				// TODO : 这里还没有确认是只判断SD卡的空间, 还是判断当前缓存目录所在设备的空间
				// 这里需要判断要下载的文件, 是否大于SD卡剩余的空间.
				if (!SDCardFreeFileSpaceTest.isEnoughForDownload(fileTotalSize)) {
					DebugLog.e(TAG, "SD Card free file space is not enough!");
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
					break;
				}
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					break;
				}
				// ----------------------------------------------------------------------------
				
				httpInputStream = httpEntity.getContent();
				if (httpInputStream == null) {
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
					break;
				}
				
				file = new File(savePtah, saveName);
				if (file.exists()) {
					file.delete();
				}
				
				fileOutputStream = new FileOutputStream(file);
				
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					break;
				}
				// ----------------------------------------------------------------------------
				
				byte[] buffer = new byte[1024];
				int byteCount = -1;
				completedSize = 0;// 已下载完成的文件size
				while ((byteCount = httpInputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, byteCount);
					completedSize += byteCount;
					
					// 因为下载过程需要跟UI线程通信频繁,
					// 所以这里不使用handle桥接给controller了,而是直接调用controller层的回调方法
					if (downloadProgressCallback != null) {
						downloadProgressCallback.netFileProgressCallbackInNonUIThread(fileTotalSize, completedSize);
					}
					
					// ----------------------------------------------------------------------------
					if (isInterrupted()) {
						break;
					}
					// ----------------------------------------------------------------------------
				}
				
				// 判断下载是否成功
				if (fileTotalSize != completedSize) {
					DebugLog.e(TAG, "文件下载不完整 ! total=" + fileTotalSize + ", completed=" + completedSize);
					
					netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
					// 文件下载失败, 文件下载不完整
					break;
				}
				
				// Flushes this stream. Implementations of this method should ensure that any
				// buffered data is written out. This implementation does nothing.
				// 刷新此流。此方法的实现应该确保任何缓冲的数据写出来。此实现不执行任何操作。
				fileOutputStream.flush();
				
			} while (false);
			
		} catch (Exception e) {
			e.printStackTrace();
			// 客户端代码发生了异常
			netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
		} finally {
			
			if (isInterrupted()) {
				// 外部取消了当前线程
				DebugLog.e(TAG, "用户取消了本次联网请求,ThreadID = [ " + requestEvent.getThreadID() + " ] ");
				netErrorForOUT.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_THREAD_IS_CANCELED);
			}
			
			// 根据错误类型, 错误代码, 获取对应的详细错误信息
			netErrorForOUT.setErrorMessage(FileDownloadErrorHandle.getErrorMessage(netErrorForOUT.getErrorType(), netErrorForOUT.getErrorCode()));
			DebugLog.e(TAG, "下载过程完成, 打印netErrorBean-->" + netErrorForOUT.toString());
			
			// 如果文件下载失败, 就删除临时文件
			if (netErrorForOUT.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
				DebugLog.e(TAG, "download file is failed, delete incomplete file!");
				
				// 文件下载失败, 要删除已经下载到本地的文件
				if (file != null) {
					file.delete();
				}
			}
			
			/**
			 * 释放相关资源
			 */
			try {
				if (httpInputStream != null) {
					httpInputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Message msg = new Message();
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
