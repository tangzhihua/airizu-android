package cn.airizu.net_file.download;

import android.text.TextUtils;
import cn.airizu.net_file.INetFileProgressCallback;
import cn.airizu.net_file.INetFileRespondCallback;
import cn.airizu.net_file.NetFileNetRequestEventAbstract;

/**
 * @author zhihua.tang
 */
public final class DownloadFileNetRequestEvent extends NetFileNetRequestEventAbstract {
	// 目标文件下载完成时, 要被保存到的路径
	private final String savePtah;
	// 目标文件下载完成时, 要保存成这个名字
	private final String saveName;
	
	public DownloadFileNetRequestEvent(final Enum<?> requestEvent,
			                               final int threadID, 
			                               final String netUrlPath, 
			                               final String savePtah, 
			                               final String saveName, 
			                               final INetFileProgressCallback progressCallback,
			                               final INetFileRespondCallback respondCallback) throws Exception {
		
		super(requestEvent, threadID, netUrlPath, progressCallback, respondCallback);
		
		if (TextUtils.isEmpty(savePtah) || TextUtils.isEmpty(saveName)) {
			throw new IllegalArgumentException("入参为空, requestEvent/handler 不能为空 ! ");
		}
		
		this.savePtah = savePtah;
		this.saveName = saveName;
	}
	
	public String getSavePtah() {
		return savePtah;
	}
	
	public String getSaveName() {
		return saveName;
	}
	
}
