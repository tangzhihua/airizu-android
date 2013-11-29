package cn.airizu.domain.protocol.room_search;

public final class RoomSearchDatabaseFieldsConstant {
	private RoomSearchDatabaseFieldsConstant() {
		
	}
	
	public static enum DefaultValueForRequestBeanFieldEnum {
		//
		distance("3000"),
		//
		nearby("1");
		
		private String defaultValue;
		
		public String getDefaultValueString() {
			return defaultValue;
		}
		
		private DefaultValueForRequestBeanFieldEnum(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		
	}
	
	public static enum RequestBean {
		// 城市id
		cityId,
		// 城市名称
		cityName,
		// 地标名 (从 2.4 房间推荐 接口 可以获取, 另外可以从搜索界面中获取用户手动输入)
		streetName,
		// 入住时间(2012-01-01)
		checkinDate,
		// 退房时间(2012-01-02)
		checkoutDate,
		// 入住人数(1~10, 10人以上传10)
		occupancyCount,
		// 房间编号
		roomNumber,
		// 查询从哪行开始
		offset,
		// 查询的数据条数
		max,
		// 价格区间 (0-100, 100-200, 200-300, 300 :300以上传300)
		priceDifference,
		// 区名称
		districtName,
		// 区ID
		districtId,
		// 房屋类型(可在 2.8 初始化字典 接口获取)
		roomType,
		// 排序方式(爱日租推荐 "tja", 价格从高到低"jgd", 价格从低到高"jga", 评论从高到低"pjd", 距离由近到远"jla")
		order,
		// 出租方式(可在 2.8 初始化字典接口获取)
		rentType,
		// 设施设备(可在 2.8 初始化字典接口获取)
		tamenities,
		// 距离筛选( 500 , 1000,3000)
		distance,
		// 纬度
		locationLat,
		// 经度
		locationLng,
		// 是否是查询 "附近" 的房源(是就 传数字1)
		nearby
		
	}
	
	public static enum RespondBean {
		//
		data,
		// 搜索房间总数
		roomCount,

		// 房间编号
		roomId,
		// 房间标题
		roomTitle,
		// 租住方式Id
		rentalWay,
		// 租住方式名称
		rentalWayName,
		// 可住人数
		occupancyCount,
		// 评论总数
		reviewCount,
		// 已预定晚数
		scheduled,
		// 价格
		price,
		// 房间图片url
		image,
		// 是否是验证的房间
		verify,
		// 百度经度
		len,
		// 百度纬度
		lat,
		// 距离
		distance
	}
}
