package cn.airizu.domain.protocol.account_register;

public class RegisterNetResquestBean {
	// 用户名
	private final String userName;
	// 手机号
	private final String phoneNumber;
	// 电子邮件
	private final String email;
	// 密码
	private final String password;
	
	public RegisterNetResquestBean(String userName, String phoneNumber, String email, String password) {
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "RegisterNetResquestBean [userName=" + userName + ", phoneNumber=" + phoneNumber + ", email=" + email + ", password=" + password + "]";
	}
	
}
