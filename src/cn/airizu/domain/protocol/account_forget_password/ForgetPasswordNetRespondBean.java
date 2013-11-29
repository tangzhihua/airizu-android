package cn.airizu.domain.protocol.account_forget_password;

public final class ForgetPasswordNetRespondBean {
	private final String message;
	
	public ForgetPasswordNetRespondBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "ForgetPasswordNetRespondBean [message=" + message + "]";
	}
	
}
