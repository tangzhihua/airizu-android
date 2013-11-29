package cn.airizu.domain.protocol.room_dictionary;

public final class DictionaryDatabaseFieldsConstant {
	private DictionaryDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
	}
	
	public static enum RespondBean {
		// 房间类型
		roomType,
		//
		typeName,
		//
		typeId,

		// 出租方式
		rentManner,
		//
		rentalWayName,
		//
		rentalWayId,

		// 设施设备
		equipment,
		//
		equipmentName,
		//
		equipmentId
	}
}
