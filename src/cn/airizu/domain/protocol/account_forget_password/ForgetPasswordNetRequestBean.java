package cn.airizu.domain.protocol.account_forget_password;

public final class ForgetPasswordNetRequestBean {
	private final String phoneNumber;
	
	public ForgetPasswordNetRequestBean(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	@Override
	public String toString() {
		return "ForgetPasswordNetRequestBean [phoneNumber=" + phoneNumber + "]";
	}
}
