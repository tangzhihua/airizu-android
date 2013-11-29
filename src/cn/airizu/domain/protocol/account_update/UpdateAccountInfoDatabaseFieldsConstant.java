package cn.airizu.domain.protocol.account_update;

public final class UpdateAccountInfoDatabaseFieldsConstant {
	private UpdateAccountInfoDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 用户名
		userName,
		// 性别
		sex,
		// 上传图像信息
		image
	}
	
	public static enum RespondBean {
		// 修改成功提示
		message
	}
}
