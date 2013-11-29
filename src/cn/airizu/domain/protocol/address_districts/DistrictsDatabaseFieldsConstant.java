package cn.airizu.domain.protocol.address_districts;

public final class DistrictsDatabaseFieldsConstant {
	private DistrictsDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 城市id
		cityId
	}
	
	public static enum RespondBean {
		//
		districts,

		//
		id,
		//
		name,
		//
		code,
		//
		minLng,
		//
		maxLng,
		//
		minLat,
		//
		maxLat,
		//
		locationLat,
		//
		locationLng,
		//
		hot,
		//
		sort,
		//
		locationLatBaidu,
		//
		locationLngBaidu,
		//
		minLngBaidu,
		//
		minLatBaidu,
		//
		maxLatBaidu,
		//
		maxLngBaidu,
		//
		cityId
	}
}
