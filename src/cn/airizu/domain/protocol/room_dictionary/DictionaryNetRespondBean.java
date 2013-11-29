package cn.airizu.domain.protocol.room_dictionary;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public final class DictionaryNetRespondBean implements Serializable {
	private final List<RootType> rootTypes;
	private final List<RentManner> rentManners;
	private final List<Equipment> equipments;
	
	public List<RootType> getRootTypes() {
		return rootTypes;
	}
	
	public List<RentManner> getRentManners() {
		return rentManners;
	}
	
	public List<Equipment> getEquipments() {
		return equipments;
	}
	
	public DictionaryNetRespondBean(List<RootType> rootTypes, List<RentManner> rentManners, List<Equipment> equipments) {
		this.rootTypes = rootTypes;
		this.rentManners = rentManners;
		this.equipments = equipments;
	}
	
	@Override
	public String toString() {
		return "DictionaryNetRespondBean [rootTypes=" + rootTypes + ", rentManners=" + rentManners + ", equipments=" + equipments + "]";
	}
}
