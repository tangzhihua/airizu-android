package cn.airizu.domain.protocol.room_dictionary;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class Equipment implements Serializable {
	private String equipmentName;
	private String equipmentId;
	
	public String getEquipmentName() {
		return equipmentName;
	}
	
	public String getEquipmentId() {
		return equipmentId;
	}
	
	public Equipment(String equipmentName, String equipmentId) {
		this.equipmentName = equipmentName;
		this.equipmentId = equipmentId;
	}
	
	@Override
	public String toString() {
		return "Equipment [equipmentName=" + equipmentName + ", equipmentId=" + equipmentId + "]";
	}
}
