package cn.airizu.domain.protocol.room_calendar;

public final class RoomCalendarDatabaseFieldsConstant {
	private RoomCalendarDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		//  
		roomId
	}
	
	public static enum RespondBean {
		// 入住时间包括当前月内，三个月之内不能订房的日期
		checkIn
	}
}
