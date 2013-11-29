package cn.airizu.domain.protocol.room_calendar;

public class RoomCalendarNetRequestBean {
	// 房间id
	private String roomId;
	
	public RoomCalendarNetRequestBean(String roomId) {
		this.roomId = roomId;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	@Override
	public String toString() {
		return "RoomCalendarNetRequestBean [roomId=" + roomId + "]";
	}
}
