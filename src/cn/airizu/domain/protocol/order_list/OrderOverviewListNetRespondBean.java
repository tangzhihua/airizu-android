package cn.airizu.domain.protocol.order_list;

import java.util.List;

public class OrderOverviewListNetRespondBean {
	
	private List<OrderOverview> orderOverviewList;
	
	public OrderOverviewListNetRespondBean(List<OrderOverview> orderOverviewList) {
		this.orderOverviewList = orderOverviewList;
	}
	
	public List<OrderOverview> getOrderOverviewList() {
		return orderOverviewList;
	}
	
	@Override
	public String toString() {
		return "OrderOverviewListNetRespondBean [orderOverviewList=" + orderOverviewList + "]";
	}
}
