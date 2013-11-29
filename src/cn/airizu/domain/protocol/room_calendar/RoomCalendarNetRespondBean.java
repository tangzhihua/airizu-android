package cn.airizu.domain.protocol.room_calendar;

import java.util.List;

public class RoomCalendarNetRespondBean {
	
	// 入住时间包括当前月内，三个月之内不能订房的日期
	private List<Integer> roomCalendar;
	
	public RoomCalendarNetRespondBean(List<Integer> roomCalendar) {
		this.roomCalendar = roomCalendar;
	}
	
	public List<Integer> getRoomCalendar() {
		return roomCalendar;
	}
	
	@Override
	public String toString() {
		return "RoomCalendarNetRespondBean [roomCalendar=" + roomCalendar + "]";
	}
}
