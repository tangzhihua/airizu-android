package cn.airizu.domain.protocol.review_submit;

import java.util.Map;

public class ReviewSubmitNetRequestBean {
	// 订单编号
	private String orderId;
	// 评论内容
	private String reviewContent;
	private Map<String, String> reviewItemScoreList;
	
	public ReviewSubmitNetRequestBean(String orderId, String reviewContent, Map<String, String> reviewItemScoreList) {
		this.orderId = orderId;
		this.reviewContent = reviewContent;
		this.reviewItemScoreList = reviewItemScoreList;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	public String getReviewContent() {
		return reviewContent;
	}
	
	public Map<String, String> getReviewItemScoreList() {
		return reviewItemScoreList;
	}
	
	@Override
	public String toString() {
		return "ReviewSubmitNetRequestBean [orderId=" + orderId + ", reviewContent=" + reviewContent + ", reviewItemScoreList=" + reviewItemScoreList + "]";
	}
}
