package cn.airizu.domain.protocol.order_submit;

public final class OrderSubmitDatabaseFieldsConstant {
	private OrderSubmitDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 房间id
		roomId,
		// 入住日期
		checkInDate,
		// 退房日子
		checkOutDate,
		// 积分
		pointNum,
		// 入住人数
		guestNum,
		// 优惠方式(0：不使用优惠卷；1：vip优惠；2：普通优惠卷；3：现金卷)
		voucherMethod,
		// 优惠码
		iVoucherCode,
		// 来源关键字
		semKeyword,
		// 来源
		semSource,
		// 租客姓名
		checkinName,
		// 租客电话
		checkinPhone
	}
	
	public static enum RespondBean {
		// 订单编号
		orderId,
		// 订单状态
		orderState,
		// 消息
		message,
		// 入住时间
		chenckInDate,
		// 退房时间
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

		// 订单状态与客户端互相转换的状态订单状态
		// 1待确定
		// 2待支付
		// 3待入住
		// 4待评价
		// 5已完成
		conversionState
		
	}
}
