package cn.airizu.domain.protocol.order_freebook;

public final class FreeBookDatabaseFieldsConstant {
	private FreeBookDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 房间id
		roomId,
		// 入住时间
		checkInDate,
		// 退房时间
		checkOutDate,
		// 优惠劵码
		iVoucherCode,
		// 优惠卷类型
		// 0：不使用优惠
		// 1：vip优惠
		// 2：普通优惠卷
		// 3：现金卷
		voucherMethod,
		// 积分
		pointNum,
		// 人数
		guestNum
	}
	
	public static enum RespondBean {
		// 是否优惠（0优惠，1没优惠）
		isCheap,
		// 用户积分
		availablePoint,
		// 预付订金
		advancedDeposit,
		// 线下支付
		underLinePaid,
		// 订单总额
		totalPrice
	}
}
