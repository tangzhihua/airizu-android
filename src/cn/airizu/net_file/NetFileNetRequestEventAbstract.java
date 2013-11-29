package cn.airizu.net_file;

import android.text.TextUtils;

public abstract class NetFileNetRequestEventAbstract {
	//
	private final Enum<?> requestEvent;
	// 线程ID
	private final int threadID;
	// 文件上传的路径
	private final String netUrlPath;
	// 上传或者下载的实时进度
	private final INetFileProgressCallback progressCallback;
	// 上传或者下载响应回调方法(上传完成, 或者出现问题时, 通过这个告知调用方)
	private final INetFileRespondCallback respondCallback;
	
	protected NetFileNetRequestEventAbstract(final Enum<?> requestEvent, 
			                                     final int threadID, 
			                                     final String netUrlPath, 
			                                     final INetFileProgressCallback progressCallback,
			                                     final INetFileRespondCallback respondCallback) throws Exception {
		
		if (TextUtils.isEmpty(netUrlPath) || respondCallback == null || requestEvent == null) {
			throw new IllegalArgumentException("入参为空, requestEvent/netUrlPath/respondCallback 不能为空 ! ");
		}
		this.requestEvent = requestEvent;
		this.threadID = threadID;
		this.netUrlPath = netUrlPath;
		this.progressCallback = progressCallback;
		this.respondCallback = respondCallback;
	}
	
	public Enum<?> getRequestEvent() {
		return requestEvent;
	}
	
	public int getThreadID() {
		return threadID;
	}
	
	public String getNetUrlPath() {
		return netUrlPath;
	}
	
	public INetFileProgressCallback getProgressCallback() {
		return progressCallback;
	}
	
	public INetFileRespondCallback getRespondCallback() {
		return respondCallback;
	}
}
