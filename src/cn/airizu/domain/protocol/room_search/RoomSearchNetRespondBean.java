package cn.airizu.domain.protocol.room_search;

import java.util.List;

public class RoomSearchNetRespondBean {
	private final String roomCount;// 搜索房间总数 
	private List<RoomInfo> roomList;
	
	public RoomSearchNetRespondBean(String roomCount, List<RoomInfo> roomList) {
		this.roomCount = roomCount;
		this.roomList = roomList;
	}
	
	public String getRoomCount() {
		return roomCount;
	}
	
	public List<RoomInfo> getRoomList() {
		return roomList;
	}
	
	@Override
	public String toString() {
		return "RoomSearchNetRespondBean [roomCount=" + roomCount + ", roomList=" + roomList + "]";
	}
	
}
