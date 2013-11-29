package cn.airizu.net_file;

import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;

/**
 * @author zhihua.tang
 */
public interface INetFileRespondCallback {
	/**
	 * @param requestIndex
	 * @param fullFilePathName 完整的文件路径名
	 * @param errorType
	 * @param errorMessage
	 */
	public void netFileRespondCallback(final Enum<?> requestEvent, final String fullFilePathName, final NetErrorTypeEnum errorType, final String errorMessage);
}
