package cn.airizu.domain.protocol.order_cancel;

public final class OrderCancelNetRequestBean {
	// 必选
	private final String orderId;// 订单编号
	
	public OrderCancelNetRequestBean(String orderId) {
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
