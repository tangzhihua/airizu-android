package cn.airizu.domain.protocol.account_login;

public final class LogonNetRespondBean {
	// 消息
	private final String message;
	// 用户Id
	private final String userId;
	// 用户名称
	private final String userName;
	// sessionId
	private final String sessionId;
	
	public LogonNetRespondBean(String message, String userId, String userName, String sessionId) {
		this.message = message;
		this.userId = userId;
		this.userName = userName;
		this.sessionId = sessionId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getJSESSIONID() {
		return sessionId;
	}
	
	@Override
	public String toString() {
		return "LogonNetRespondBean [message=" + message + ", userId=" + userId + ", userName=" + userName + ", JESSIONID=" + sessionId + "]";
	}
}
