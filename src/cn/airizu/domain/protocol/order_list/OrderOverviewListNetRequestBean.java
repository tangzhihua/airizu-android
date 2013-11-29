package cn.airizu.domain.protocol.order_list;

public class OrderOverviewListNetRequestBean {
	// 订单状态(1,待确定，2待支付，3待入住，4待评价5已完成)
	private String orderState;
	
	public OrderOverviewListNetRequestBean(String orderState) {
		this.orderState = orderState;
	}
	
	public String getOrderState() {
		return orderState;
	}
	
	@Override
	public String toString() {
		return "OrderOverviewListNetRequestBean [orderState=" + orderState + "]";
	}
}
