package cn.airizu.domain.protocol.account_register;

public class RegisterNetRespondBean {
	private final String message;
	
	public RegisterNetRespondBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "RegisterNetRespondBean [message=" + message + "]";
	}
}
