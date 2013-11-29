package cn.airizu.domain.protocol.account_update;

public class UpdateAccountInfoNetRespondBean {
	private final String message;
	
	public UpdateAccountInfoNetRespondBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
