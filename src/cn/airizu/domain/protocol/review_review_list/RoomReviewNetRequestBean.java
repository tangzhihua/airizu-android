package cn.airizu.domain.protocol.review_review_list;

public final class RoomReviewNetRequestBean {
	// 房间ID
	private final String roomId;
	// 页数
	private final int pageNum;
	
	public RoomReviewNetRequestBean(String roomId, int pageNum) {
		this.roomId = roomId;
		this.pageNum = pageNum;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	
	@Override
	public String toString() {
		return "RoomReviewNetRequestBean [roomId=" + roomId + ", pageNum=" + pageNum + "]";
	}
}
