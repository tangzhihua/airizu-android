package cn.airizu.domain.protocol.review_review_list;

import java.util.List;
import java.util.Map;

public final class RoomReviewNetRespondBean {
	// 评论总数
	private final int reviewCount;
	// 房间总的平均分(为浮点类型)
	private final String avgScore;
	// 当前房间的评论项
	private final Map<String, String> reviewItemMap;
	// 当前房间的评论信息
	private final List<RoomReview> roomReviewList;
	
	public RoomReviewNetRespondBean(int reviewCount, String avgScore, Map<String, String> reviewItemMap, List<RoomReview> roomReviewList) {
		this.reviewCount = reviewCount;
		this.avgScore = avgScore;
		this.reviewItemMap = reviewItemMap;
		this.roomReviewList = roomReviewList;
	}
	
	public int getReviewCount() {
		return reviewCount;
	}
	
	public String getAvgScore() {
		return avgScore;
	}
	
	public Map<String, String> getReviewItemMap() {
		return reviewItemMap;
	}
	
	public List<RoomReview> getRoomReviewList() {
		return roomReviewList;
	}
	
	@Override
	public String toString() {
		return "RoomReviewNetRespondBean [reviewCount=" + reviewCount + ", avgScore=" + avgScore + ", reviewItemMap=" + reviewItemMap + ", roomReviewList=" + roomReviewList + "]";
	}
}
