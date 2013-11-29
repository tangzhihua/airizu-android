package cn.airizu.domain.protocol.account_version;

public final class VersionNetRequestBean {
	// 当前版本号
	private final String currentVersion;
	
	public VersionNetRequestBean(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	
	@Override
	public String toString() {
		return "VersionNetRequestBean [currentVersion=" + currentVersion + "]";
	}
}
