package cn.airizu.domain.protocol.account_version;

public final class VersionDatabaseFieldsConstant {
	private VersionDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 当前版本号
		currentVersion
	}
	
	public static enum RespondBean {
		// 最新版本号
		newVersion,
		// 文件大小
		fileSize,
		// 更新内容
		updateContent,
		// 下载地址
		downloadAddress
	}
}
