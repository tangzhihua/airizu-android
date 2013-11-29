package cn.airizu.domain.protocol.order_detail;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class OrderDetailNetRespondBean implements Serializable {
	private final String orderId;// 订单编号
	private final String orderState;// 订单状态
	private final String message;// 消息
	private final String chenckInDate;// 开始时间
	private final String chenckOutDate;// 结束时间
	private final String guestNum;// 入住人数
	private final String pricePerNight;// 预付定金
	private final String linePay;// 线下支付
	private final String subPrice;// 订单总额
	private final String orderType;// 订单类型(0普通 1快速)
	private final String statusContent;// 订单状态内容
	
	// 房间详情相关接口
	// 房间编号
	private final String number;
	// 房间图片
	private final String image;
	// 房间标题
	private final String title;
	// 房间地址
	private final String address;
	
	// 房东信息相关
	// 是否显示房东信息boolean（true：显示，false：不显示）
	private final boolean ifShowHost;
	// 房东姓名
	private final String hostName;
	// 房东电话
	private final String hostPhone;
	// 房东备用电话
	private final String hostBackupPhone;
	
	// 订单状态与客户端互相转换的状态订单状态
	// 1待确定
	// 2待支付
	// 3待入住
	// 4待评价
	// 5已完成
	private final int conversionState;
	
	public OrderDetailNetRespondBean(String orderId, String orderState, String message, String chenckInDate, String chenckOutDate, String guestNum, String pricePerNight, String linePay,
			String subPrice, String orderType, String statusContent, String number, String image, String title, String address, boolean ifShowHost, String hostName, String hostPhone,
			String hostBackupPhone, int conversionState) {
		this.orderId = orderId;
		this.orderState = orderState;
		this.message = message;
		this.chenckInDate = chenckInDate;
		this.chenckOutDate = chenckOutDate;
		this.guestNum = guestNum;
		this.pricePerNight = pricePerNight;
		this.linePay = linePay;
		this.subPrice = subPrice;
		this.orderType = orderType;
		this.statusContent = statusContent;
		this.number = number;
		this.image = image;
		this.title = title;
		this.address = address;
		this.ifShowHost = ifShowHost;
		this.hostName = hostName;
		this.hostPhone = hostPhone;
		this.hostBackupPhone = hostBackupPhone;
		this.conversionState = conversionState;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	public String getOrderState() {
		return orderState;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getChenckInDate() {
		return chenckInDate;
	}
	
	public String getChenckOutDate() {
		return chenckOutDate;
	}
	
	public String getGuestNum() {
		return guestNum;
	}
	
	public String getPricePerNight() {
		return pricePerNight;
	}
	
	public String getLinePay() {
		return linePay;
	}
	
	public String getSubPrice() {
		return subPrice;
	}
	
	public String getOrderType() {
		return orderType;
	}
	
	public String getStatusContent() {
		return statusContent;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAddress() {
		return address;
	}
	
	public boolean isIfShowHost() {
		return ifShowHost;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public String getHostPhone() {
		return hostPhone;
	}
	
	public String getHostBackupPhone() {
		return hostBackupPhone;
	}
	
	public int getConversionState() {
		return conversionState;
	}
	
	@Override
	public String toString() {
		return "OrderDetailNetRespondBean [orderId=" + orderId + ", orderState=" + orderState + ", message=" + message + ", chenckInDate=" + chenckInDate + ", chenckOutDate=" + chenckOutDate
				+ ", guestNum=" + guestNum + ", pricePerNight=" + pricePerNight + ", linePay=" + linePay + ", subPrice=" + subPrice + ", orderType=" + orderType + ", statusContent=" + statusContent
				+ ", number=" + number + ", image=" + image + ", title=" + title + ", address=" + address + ", ifShowHost=" + ifShowHost + ", hostName=" + hostName + ", hostPhone=" + hostPhone
				+ ", hostBackupPhone=" + hostBackupPhone + ", conversionState=" + conversionState + "]";
	}
}
