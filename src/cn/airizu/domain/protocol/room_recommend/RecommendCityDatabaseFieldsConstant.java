package cn.airizu.domain.protocol.room_recommend;

public final class RecommendCityDatabaseFieldsConstant {
	private RecommendCityDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
	}
	
	public static enum RespondBean {
		//
		data,

		// 详细的数据
		// 编号
		id,
		// 城市名称
		cityName,
		// 城市id
		cityId,
		// 对应图片地址
		image,
		// 排序
		sort,
		// 地标1 名称
		street1Name,
		// 地标1 编号
		street1Id,
		// 地标2名称
		street2Name,
		// 地标2编号
		street2Id
	}
}
