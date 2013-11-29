package cn.airizu.domain.protocol.account_login;

import cn.airizu.domain.domainbean_tools.DomainBeanNullValueDefine;

public final class LogonNetRequestBean {
	// 登录名
	private final String loginName;
	// 密码
	private final String password;
	// 客户端应用版本号
	private final String clientVersion;
	// 客户端android版本号
	private final String clientAVersion;
	// 屏幕大小
	private final String screenSize;
	
	private LogonNetRequestBean(Builder builder) {
		this.loginName = builder.loginName;
		this.password = builder.password;
		this.clientVersion = builder.clientVersion;
		this.clientAVersion = builder.clientAVersion;
		this.screenSize = builder.screenSize;
	}
	
	public String getLoginName() {
		return loginName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getClientVersion() {
		return clientVersion;
	}
	
	public String getClientAVersion() {
		return clientAVersion;
	}
	
	public String getScreenSize() {
		return screenSize;
	}
	
	@Override
	public String toString() {
		return "LogonNetRequestBean [loginName=" + loginName + ", password=" + password + ", clientVersion=" + clientVersion + ", clientAVersion=" + clientAVersion + ", screenSize="
				+ screenSize + "]";
	}
	
	public static class Builder {
		// 必选参数
		private final String loginName;
		private final String password;
		
		// 可选参数
		private String clientVersion = DomainBeanNullValueDefine.STRING_NULL_VALUE;
		private String clientAVersion = DomainBeanNullValueDefine.STRING_NULL_VALUE;
		private String screenSize = DomainBeanNullValueDefine.STRING_NULL_VALUE;
		
		public Builder(String loginName, String password) {
			this.loginName = loginName;
			this.password = password;
		}
		
		public Builder clientVersion(String clientVersion) {
			this.clientVersion = clientVersion;
			return this;
		}
		
		public Builder clientAVersion(String clientAVersion) {
			this.clientAVersion = clientAVersion;
			return this;
		}
		
		public Builder screenSize(String screenSize) {
			this.screenSize = screenSize;
			return this;
		}
		
		public LogonNetRequestBean builder() {
			return new LogonNetRequestBean(this);
		}
	}
}
