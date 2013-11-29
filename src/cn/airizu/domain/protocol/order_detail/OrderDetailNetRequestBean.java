package cn.airizu.domain.protocol.order_detail;

public final class OrderDetailNetRequestBean {
	// 必选
	private final String orderId;// 订单编号
	
	public OrderDetailNetRequestBean(String orderId) {
		this.orderId = orderId;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	@Override
	public String toString() {
		return "OrderDetailNetRequestBean [orderId=" + orderId + "]";
	}
}
