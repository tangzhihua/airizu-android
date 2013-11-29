package cn.airizu.domain.protocol.room_detail;

public final class RoomDetailDatabaseFieldsConstant {
	private RoomDetailDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		//
		roomId
	}
	
	public static enum RespondBean {
		// 大图地址列表
		imageM,
		// 用户编号
		userId,
		// 建筑面积
		size,
		// 缩略图地址列表
		imageS,
		// 房间默认图片
		image,
		// 每晚价钱
		price,
		// 房间标题
		title,
		// 房间地址
		address,
		// 百度经度
		len,
		// 百度纬度
		lat,
		// 房间编号
		number,
		// 曾被预定
		scheduled,
		// 卧室数量
		bedRoom,
		// 规则内容
		ruleContent,
		// 清洁服务类型
		clean,
		// 房间概括
		description,
		// 可住人数
		accommodates,
		// 使用规则
		roomRule,
		// 卫浴类型
		restRoom,
		// 提供发票 (1 : 提供, 2 : 不提供)
		tickets,
		// 退订条款
		cancellation,
		// 最少天数
		minNights,
		// 租住方式
		privacy,
		// 退房时间
		checkOutTime,
		// 最多天数
		maxNights,
		// 房间床数
		beds,
		// 房屋类型
		propertyType,
		// 房间床型
		bedType,
		// 卫生间数
		bathRoomNum,
		// 配套设施列表
		equipmentList,
		// 租客点评总分
		review,
		// 租客点评总条数
		reviewCount,
		// 租客点评列表，这里只显示1条记录
		reviewContent,
		// 是否是100%验证房间，如果不是不显示
		isVerify,
		// 验100%字符串
		verifyContent,
		// 是否是特价房间，如果不是不显示
		specials,
		// 是否是速订房间，如果不是不显示，如果既不是100%、特价、速订，删除房间特色栏目
		speed,
		// 速订字符串
		speedContent,
		// 介绍字符串
		introduction
	}
}
