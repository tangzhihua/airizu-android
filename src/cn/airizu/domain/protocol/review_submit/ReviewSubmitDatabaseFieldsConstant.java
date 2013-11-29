package cn.airizu.domain.protocol.review_submit;

public final class ReviewSubmitDatabaseFieldsConstant {
	private ReviewSubmitDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 订单编号
		orderId,
		// 评论内容
		reviewContent,
		// 评论项的参数前缀
		score_
	}
	
	public static enum RespondBean {
		// 消息
		message
	}
}
