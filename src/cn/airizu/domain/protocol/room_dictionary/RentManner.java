package cn.airizu.domain.protocol.room_dictionary;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class RentManner implements Serializable {
	private String rentalWayName;
	private String rentalWayId;
	
	public String getRentalWayName() {
		return rentalWayName;
	}
	
	public String getRentalWayId() {
		return rentalWayId;
	}
	
	public RentManner(String rentalWayName, String rentalWayId) {
		this.rentalWayName = rentalWayName;
		this.rentalWayId = rentalWayId;
	}
	
	@Override
	public String toString() {
		return "RentManner [rentalWayName=" + rentalWayName + ", rentalWayId=" + rentalWayId + "]";
	}
}
