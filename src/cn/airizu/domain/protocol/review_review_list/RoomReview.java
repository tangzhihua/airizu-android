package cn.airizu.domain.protocol.review_review_list;

public class RoomReview {
	// 用户名
	private final String userName;
	// 用户发表评论时间
	private final String userReviewTime;
	// 用户评论内容
	private final String userReview;
	// 房东回复评论的时间
	private final String hostReviewTime;
	// 房东回复评论的内容
	private final String hostReview;
	
	public RoomReview(String userName, String userReviewTime, String userReview, String hostReviewTime, String hostReview) {
		this.userName = userName;
		this.userReviewTime = userReviewTime;
		this.userReview = userReview;
		this.hostReviewTime = hostReviewTime;
		this.hostReview = hostReview;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getUserReviewTime() {
		return userReviewTime;
	}
	
	public String getUserReview() {
		return userReview;
	}
	
	public String getHostReviewTime() {
		return hostReviewTime;
	}
	
	public String getHostReview() {
		return hostReview;
	}
	
	@Override
	public String toString() {
		return "RoomReview [userName=" + userName + ", userReviewTime=" + userReviewTime + ", userReview=" + userReview + ", hostReviewTime=" + hostReviewTime + ", hostReview=" + hostReview
				+ "]";
	}
	
}
