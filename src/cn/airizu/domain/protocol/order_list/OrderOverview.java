package cn.airizu.domain.protocol.order_list;

public class OrderOverview {
	private final String roomTitle;// 房间标题
	private final String checkInDate;// 入住时间
	private final String checkOutDate;// 退房时间
	private final String statusCode;// 订单状态代码
	private final String orderTotalPrice;// 订单总额
	private final String roomImage;// 房间图片地址
	private final String orderId;// 订单编号
	
	public OrderOverview(String roomTitle, String checkInDate, String checkOutDate, String statusCode, String orderTotalPrice, String roomImage, String orderId) {
		this.roomTitle = roomTitle;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.statusCode = statusCode;
		this.orderTotalPrice = orderTotalPrice;
		this.roomImage = roomImage;
		this.orderId = orderId;
	}
	
	public String getRoomTitle() {
		return roomTitle;
	}
	
	public String getCheckInDate() {
		return checkInDate;
	}
	
	public String getCheckOutDate() {
		return checkOutDate;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	
	public String getOrderTotalPrice() {
		return orderTotalPrice;
	}
	
	public String getRoomImage() {
		return roomImage;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	@Override
	public String toString() {
		return "OrderOverview [roomTitle=" + roomTitle + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate + ", statusCode=" + statusCode + ", orderTotalPrice="
				+ orderTotalPrice + ", roomImage=" + roomImage + ", orderId=" + orderId + "]";
	}
	
}
