package cn.airizu.domain.protocol.account_index;

public class AccountIndexNetRespondBean {
	
	// 总积分
	private final int totalPoint;
	// 手机号码
	private final String phoneNumber;
	// 等待房东确认的订单数量
	private final int waitConfirmCount;
	// 等待支付的订单数量
	private final int waitPayCount;
	// 等待入住的订单数量
	private final int waitLiveCount;
	// 等待评价的订单数量
	private final int waitReviewCount;
	// 用户名
	private final String userName;
	// 头像地址
	private final String userImageURL;
	// 性别
	private final String sex;
	// 邮箱
	private final String email;
	// 手机是否验证
	private final boolean validatePhone;
	// 邮箱是否验证
	private final boolean validateEmail;
	
	public AccountIndexNetRespondBean(int totalPoint, 
			                              String phoneNumber, 
			                              int waitConfirmCount, 
			                              int waitPayCount, 
			                              int waitLiveCount, 
			                              int waitReviewCount, 
			                              String userName, 
			                              String userImageURL, 
			                              String sex, 
			                              String email, 
			                              boolean validatePhone, 
			                              boolean validateEmail) {
		this.totalPoint = totalPoint;
		this.phoneNumber = phoneNumber;
		this.waitConfirmCount = waitConfirmCount;
		this.waitPayCount = waitPayCount;
		this.waitLiveCount = waitLiveCount;
		this.waitReviewCount = waitReviewCount;
		this.userName = userName;
		this.userImageURL = userImageURL;
		this.sex = sex;
		this.email = email;
		this.validatePhone = validatePhone;
		this.validateEmail = validateEmail;
	}
	
	public int getTotalPoint() {
		return totalPoint;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public int getWaitConfirmCount() {
		return waitConfirmCount;
	}
	
	public int getWaitPayCount() {
		return waitPayCount;
	}
	
	public int getWaitLiveCount() {
		return waitLiveCount;
	}
	
	public int getWaitReviewCount() {
		return waitReviewCount;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getUserImageURL() {
		return userImageURL;
	}
	
	public String getSex() {
		return sex;
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean isValidatePhone() {
		return validatePhone;
	}
	
	public boolean isValidateEmail() {
		return validateEmail;
	}
	
	@Override
	public String toString() {
		return "AccountIndexNetRespondBean [totalPoint=" + totalPoint + ", phoneNumber=" + phoneNumber + ", waitConfirmCount=" + waitConfirmCount + ", waitPayCount=" + waitPayCount
				+ ", waitLiveCount=" + waitLiveCount + ", waitReviewCount=" + waitReviewCount + ", userName=" + userName + ", userImageURL=" + userImageURL + ", sex=" + sex + ", email=" + email
				+ ", validatePhone=" + validatePhone + ", validateEmail=" + validateEmail + "]";
	}
}
