package cn.airizu.net_file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.net_file.download.DownloadFileNetRequestEvent;
import cn.airizu.net_file.download.DownloadFileThread;
import cn.airizu.net_file.upload.HttpFormFileBean;
import cn.airizu.net_file.upload.UploadFileNetRequestEvent;
import cn.airizu.net_file.upload.UploadFileThread;
import cn.airizu.toolutils.DebugLog;

public final class NetFileHelper {
	private final String TAG = this.getClass().getSimpleName();
	
	private NetFileHelper() {
	}
	
	private static NetFileHelper myInstance = null;
	
	public static NetFileHelper getInstance() {
		if (null == myInstance) {
			myInstance = new NetFileHelper();
		}
		return myInstance;
	}
	
	/**
	 * 网络请求索引计数器
	 */
	private int netRequestIndexCounter = 0;
	/**
	 * 无效的请求索引, 证明downloadFile方法启动失败
	 */
	public static final int IDLE_NETWORK_REQUEST_ID = -2012;
	
	public static enum HandlerMsgTypeEnum {
		// 完成时的回调
		FINISH_CALLBACK
	};
	
	public static enum HandlerMsgExtraDataTypeEnum {
		//
		FINISHED_THREAD_ID,
		//
		FINISHED_ERROR_OBJECT
	};
	
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HandlerMsgTypeEnum.FINISH_CALLBACK.ordinal()) {
				DebugLog.e(TAG, "--> FINISH_CALLBACK");
				DebugLog.i(TAG, " ");
				NetFileNetRequestEventAbstract requestEvent = null;
				String netRequestIndex = "";
				
				do {
					if (!msg.getData().containsKey(HandlerMsgExtraDataTypeEnum.FINISHED_THREAD_ID.name())) {
						DebugLog.e(TAG, "--> FINISHED_THREAD_ID Delivery Failure !");
						break;
					}
					netRequestIndex = Integer.toString(msg.getData().getInt(HandlerMsgExtraDataTypeEnum.FINISHED_THREAD_ID.name()));
					requestEvent = (NetFileNetRequestEventAbstract) synchronousNetRequestEventBuf.get(netRequestIndex);
					if (requestEvent == null) {
						DebugLog.e(TAG, "requestEvent == null");
						break;
					}
					
					if (!msg.getData().containsKey(HandlerMsgExtraDataTypeEnum.FINISHED_ERROR_OBJECT.name())) {
						DebugLog.e(TAG, "--> FINISHED_ERROR_OBJECT Delivery Failure !");
						break;
					}
					NetErrorBean netError = (NetErrorBean) msg.getData().get(HandlerMsgExtraDataTypeEnum.FINISHED_ERROR_OBJECT.name());
					if (netError == null) {
						DebugLog.e(TAG, "--> FINISHED_ERROR_OBJECT Get Failure !");
						break;
					}
					if (netError.getErrorType() == NetErrorTypeEnum.NET_ERROR_TYPE_THREAD_IS_CANCELED) {
						// 外部取消了本次网络线程
						break;
					}
					
					// 通知外部网络回调
					INetFileRespondCallback netFileRespondCallback = requestEvent.getRespondCallback();
					if (netFileRespondCallback == null) {
						break;
					}
					// TODO : 这里的设计还没想好
					String fullFilePathName = "";
					if (requestEvent instanceof DownloadFileNetRequestEvent) {
						fullFilePathName = ((DownloadFileNetRequestEvent) requestEvent).getSavePtah() + "/" + ((DownloadFileNetRequestEvent) requestEvent).getSaveName();
					} else {
						if (((UploadFileNetRequestEvent) requestEvent).getFileParameter() != null) {
							fullFilePathName = ((UploadFileNetRequestEvent) requestEvent).getFileParameter().getFileFullName();
						}
					}
					netFileRespondCallback.netFileRespondCallback(requestEvent.getRequestEvent(), fullFilePathName, netError.getErrorType(), netError.getErrorMessage());
				} while (false);
				
				if (!TextUtils.isEmpty(netRequestIndex)) {
					synchronousUrlDuplicationCheckList.remove(requestEvent.getNetUrlPath());
					synchronousNetRequestEventBuf.remove(netRequestIndex);
					synchronousThreadBuf.remove(netRequestIndex);
				}
			}
		}
	};
	
	private Set<String> synchronousUrlDuplicationCheckList = new HashSet<String>();
	private Map<String, Object> synchronousNetRequestEventBuf = new HashMap<String, Object>();
	private Map<String, Thread> synchronousThreadBuf = new HashMap<String, Thread>();
	
	/**
	 * 开始下载一个文件
	 * 
	 * @param fileUrl 文件URL
	 * @param savePtah 文件下载完成后的保存路径
	 * @param saveName 文件下载完要被保存成的名字
	 * @param downloadProgressCallback 下载进度回调代理
	 * @param downloadFileRespondCallback 下载结果回调代理(失败成功,都要告知Controller)
	 * @return 本次请求的索引
	 */
	public synchronized int downloadFile(final Enum<?> requestEvent, final String fileUrl, final String savePtah, final String saveName,
			final INetFileProgressCallback downloadProgressCallback, final INetFileRespondCallback downloadFileRespondCallback) {
		
		int netRequestIndex = ++netRequestIndexCounter;
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     download file request begin (" + netRequestIndex + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		try {
			if (requestEvent == null || TextUtils.isEmpty(fileUrl) || TextUtils.isEmpty(savePtah) || TextUtils.isEmpty(saveName) || downloadFileRespondCallback == null) {
				throw new IllegalArgumentException("入参为空, requestEvent/threadID/fileUrl/savePtah/downloadFileRespondCallback 不能为空 ! ");
			}
			
			if (synchronousUrlDuplicationCheckList.contains(fileUrl)) {
				throw new IllegalArgumentException("不能同时下载 同一个文件, 判断标准就是文件的URL ! ");
			}
			synchronousUrlDuplicationCheckList.add(fileUrl);
			
			DownloadFileNetRequestEvent downloadFileNetRequestEvent = new DownloadFileNetRequestEvent(requestEvent, netRequestIndex, fileUrl, savePtah, saveName, downloadProgressCallback,
					downloadFileRespondCallback);
			synchronousNetRequestEventBuf.put(Integer.toString(netRequestIndex), downloadFileNetRequestEvent);
			
			/**
			 * 创建一个全新的文件下载请求线程
			 */
			DownloadFileThread httpRequestThread = new DownloadFileThread(downloadFileNetRequestEvent, myHandler);
			httpRequestThread.setPriority(7);// 设置优先级
			httpRequestThread.start();// 启动线程
			synchronousThreadBuf.put(Integer.toString(netRequestIndex), httpRequestThread);
		} catch (Exception e) {
			e.printStackTrace();
			
			netRequestIndex = IDLE_NETWORK_REQUEST_ID;
		}
		
		//
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "         ----- download file request end (" + netRequestIndex + ") -----          ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		
		return netRequestIndex;
	}
	
	/**
	 * 开始上传一个文件
	 * 
	 * @param uploadPath
	 * @param textParameterMap
	 * @param fileParameter
	 * @param uploadProgressCallback
	 * @param uploadFileRespondCallback
	 * @return
	 */
	public synchronized int uploadFile(final Enum<?> requestEvent, final String uploadPath, final Map<String, String> textParameterMap, final HttpFormFileBean fileParameter,
			final INetFileProgressCallback uploadProgressCallback, final INetFileRespondCallback uploadFileRespondCallback) {
		
		int netRequestIndex = ++netRequestIndexCounter;
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     upload file request begin (" + netRequestIndex + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		try {
			if (requestEvent == null || TextUtils.isEmpty(uploadPath) || uploadFileRespondCallback == null) {
				throw new IllegalArgumentException("入参为空, requestEvent/uploadPath/uploadFileRespondCallback 不能为空 ! ");
			}
			
			UploadFileNetRequestEvent uploadFileNetRequestEvent = new UploadFileNetRequestEvent(requestEvent, netRequestIndex, uploadPath, textParameterMap, fileParameter, uploadProgressCallback,
					uploadFileRespondCallback);
			synchronousNetRequestEventBuf.put(Integer.toString(netRequestIndex), uploadFileNetRequestEvent);
			
			/**
			 * 创建一个全新的文件下载请求线程
			 */
			UploadFileThread httpRequestThread = new UploadFileThread(uploadFileNetRequestEvent, myHandler);
			httpRequestThread.setPriority(7);// 设置优先级
			httpRequestThread.start();// 启动线程
			synchronousThreadBuf.put(Integer.toString(netRequestIndex), httpRequestThread);
		} catch (Exception e) {
			e.printStackTrace();
			
			netRequestIndex = IDLE_NETWORK_REQUEST_ID;
		}
		
		//
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "         ----- upload file request end (" + netRequestIndex + ") -----          ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		
		return netRequestIndex;
	}
	
	/**
	 * 取消一个文件下载操作, 根据请求索引
	 * 
	 * @param netRequestIndex : 网络请求命令对应的索引
	 */
	public synchronized void cancelNetFileRequestByRequestIndex(final int netRequestIndex) {
		if (netRequestIndex == IDLE_NETWORK_REQUEST_ID) {
			return;
		}
		Thread thread = (Thread) synchronousThreadBuf.get(Integer.toString(netRequestIndex));
		if (thread != null) {
			thread.interrupt();
		}
	}
}
