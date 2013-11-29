package cn.airizu.net_file;

public interface INetFileProgressCallback {
	/**
	 * 下载进度回调方法
	 * 
	 * @param totalSize
	 *          数据总size
	 * @param completedSize
	 *          已下载完成数据size
	 */
	public void netFileProgressCallbackInNonUIThread(final long totalSize, final long completedSize);
}
