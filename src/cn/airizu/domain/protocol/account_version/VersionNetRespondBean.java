package cn.airizu.domain.protocol.account_version;

public final class VersionNetRespondBean {
	// 最新版本号
	private final String newVersion;
	// 文件大小
	private final String fileSize;
	// 更新内容
	private final String updateContent;
	// 下载地址
	private final String downloadURL;
	
	public VersionNetRespondBean(String newVersion, String fileSize, String updateContent, String downloadURL) {
		this.newVersion = newVersion;
		this.fileSize = fileSize;
		this.updateContent = updateContent;
		this.downloadURL = downloadURL;
	}
	
	public String getNewVersion() {
		return newVersion;
	}
	
	public String getFileSize() {
		return fileSize;
	}
	
	public String getUpdateContent() {
		return updateContent;
	}
	
	public String getDownloadURL() {
		return downloadURL;
	}
	
	@Override
	public String toString() {
		return "VersionNetRespondBean [newVersion=" + newVersion + ", fileSize=" + fileSize + ", updateContent=" + updateContent + ", downloadURL=" + downloadURL + "]";
	}
}
