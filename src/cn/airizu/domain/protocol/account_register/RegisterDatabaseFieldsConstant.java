package cn.airizu.domain.protocol.account_register;

public final class RegisterDatabaseFieldsConstant {
	private RegisterDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 用户名
		userName,
		// 手机号
		phoneNumber,
		// 电子邮件
		email,
		// 密码
		password
	}
	
	public static enum RespondBean {
		// 消息
		message
	}
}
