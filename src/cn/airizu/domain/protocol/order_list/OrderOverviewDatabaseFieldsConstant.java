package cn.airizu.domain.protocol.order_list;

public final class OrderOverviewDatabaseFieldsConstant {
	private OrderOverviewDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 订单状态(1,待确定，2待支付，3待入住，4待评价5已完成)
		orderState
	}
	
	public static enum RespondBean {
		//
		data,

		// 房间标题
		roomTitle,
		// 入住时间
		checkInDate,
		// 退房时间
		checkOutDate,
		// 订单状态代码
		statusCode,
		// 订单总额
		orderTotalPrice,
		// 房间图片地址
		roomImage,
		// 订单编号
		orderId
	}
}
