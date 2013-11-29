package cn.airizu.net_file.upload;

import java.util.Map;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.INetFileRespondCallback;
import cn.airizu.net_file.NetFileNetRequestEventAbstract;

public final class UploadFileNetRequestEvent extends NetFileNetRequestEventAbstract {
	// 文本参数
	private final Map<String, String> textParameterMap;
	// 文件参数
	private final HttpFormFileBean fileParameter;
	
	public UploadFileNetRequestEvent(final Enum<?> requestEvent,
			                             final int threadID, 
			                             final String netUrlPath, 
			                             final Map<String, String> textParameterMap, 
			                             final HttpFormFileBean fileParameter,
			                             final INetFileProgressCallback progressCallback, 
			                             final INetFileRespondCallback respondCallback) throws Exception{
		
		super(requestEvent, threadID, netUrlPath, progressCallback, respondCallback);
		
		this.textParameterMap = textParameterMap;
		this.fileParameter = fileParameter;
	}
	
	public Map<String, String> getTextParameterMap() {
		return textParameterMap;
	}
	
	public HttpFormFileBean getFileParameter() {
		return fileParameter;
	}
}
