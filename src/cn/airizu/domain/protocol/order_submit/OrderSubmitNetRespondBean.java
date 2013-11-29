package cn.airizu.domain.protocol.order_submit;

import java.io.Serializable;

import cn.airizu.domain.protocol.order_detail.OrderDetailNetRespondBean;

@SuppressWarnings("serial")
public final class OrderSubmitNetRespondBean implements Serializable {
	
	// 订单详情
	private final OrderDetailNetRespondBean orderDetailNetRespondBean;
	
	public OrderSubmitNetRespondBean(String orderId, String orderState, String message, String chenckInDate, String chenckOutDate, String guestNum, String pricePerNight, String linePay,
			String subPrice, String orderType, String statusContent, String number, String image, String title, String address, int conversionState) {
		orderDetailNetRespondBean = new OrderDetailNetRespondBean(orderId, orderState, message, chenckInDate, chenckOutDate, guestNum, pricePerNight, linePay, subPrice, orderType,
				statusContent, number, image, title, address, false, "", "", "", conversionState);
	}
	
	public OrderDetailNetRespondBean getOrderDetailNetRespondBean() {
		return orderDetailNetRespondBean;
	}
	
	@Override
	public String toString() {
		return "OrderSubmitNetRespondBean [orderDetailNetRespondBean=" + orderDetailNetRespondBean + "]";
	}
	
}
