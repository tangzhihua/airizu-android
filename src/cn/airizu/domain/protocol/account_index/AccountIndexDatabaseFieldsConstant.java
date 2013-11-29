package cn.airizu.domain.protocol.account_index;

public final class AccountIndexDatabaseFieldsConstant {
	private AccountIndexDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
	}
	
	public static enum RespondBean {
		// 总积分
		totalPoint,
		// 手机号码
		phoneNumber,
		// 等待房东确认的订单数量
		waitConfirmCount,
		// 等待支付的订单数量
		waitPayCount,
		// 等待入住的订单数量
		waitLiveCount,
		// 等待评价的订单数量
		waitReviewCount,
		// 用户名
		userName,
		// 头像地址
		userImageURL,
		// 性别
		sex,
		// 邮箱
		email,
		// 手机是否验证
		validatePhone,
		// 邮箱是否验证
		validateEmail
		
	}
}
