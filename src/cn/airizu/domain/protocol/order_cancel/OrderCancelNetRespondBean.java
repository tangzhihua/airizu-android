package cn.airizu.domain.protocol.order_cancel;

public final class OrderCancelNetRespondBean {
	private final String message;// 消息
	
	public OrderCancelNetRespondBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "OrderCancelNetRespondBean [message=" + message + "]";
	}
}
