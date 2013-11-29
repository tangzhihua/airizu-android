package cn.airizu.domain.protocol.review_review_list;

public final class RoomReviewDatabaseFieldsConstant {
	private RoomReviewDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 房间ID
		roomId,
		// 页数
		pageNum
	}
	
	public static enum RespondBean {
		// 评论列表
		reviews,
		// 用户名
		userName,
		// 用户发表评论的时间
		userReviewTime,
		// 评论内容
		userReview,
		// 房东回复评论的时间
		hostReviewTime,
		// 房东回复的内容
		hostReview,
		
		// 房间总的平均分
		avgScore,
		// 评论总条数
		reviewCount,
		// 当前房间的评论项
		item
	}
}
