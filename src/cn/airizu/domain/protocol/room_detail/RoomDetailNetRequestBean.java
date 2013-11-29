package cn.airizu.domain.protocol.room_detail;

public class RoomDetailNetRequestBean {
	private String roomId;// 房间编号
	
	public RoomDetailNetRequestBean(String roomId) {
		this.roomId = roomId;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	@Override
	public String toString() {
		return "RoomDetailNetRequestBean [roomId=" + roomId + "]";
	}
	
}
