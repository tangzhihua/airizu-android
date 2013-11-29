package cn.airizu.domain.protocol.order_detail;

public final class OrderDetailDatabaseFieldsConstant {
	private OrderDetailDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		//
		orderId
	}
	
	public static enum RespondBean {
		// 订单编号
		orderId,
		// 订单状态
		orderState,
		// 消息
		message,
		// 开始时间
		chenckInDate,
		// 结束时间
		chenckOutDate,
		// 入住人数
		guestNum,
		// 预付定金
		pricePerNight,
		// 线下支付
		linePay,
		// 订单总额
		subPrice,
		// 订单类型
		orderType,
		// 订单状态内容
		statusContent,

		// 房间详情相关接口
		// 房间编号
		number,
		// 房间图片
		image,
		// 房间标题
		title,
		// 房间地址
		address,

		// 房东信息相关
		// 是否显示房东信息boolean（true：显示，false：不显示）
		ifShowHost,
		// 房东姓名
		hostName,
		// 房东电话
		hostPhone,
		// 房东备用电话
		hostBackupPhone,

		// 订单状态与客户端互相转换的状态订单状态
		// 1待确定
		// 2待支付
		// 3待入住
		// 4待评价
		// 5已完成
		conversionState
		
	}
}
