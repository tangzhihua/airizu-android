package cn.airizu.domain.protocol.pay_pay_info;

/**
 * 这是一个 网络请求业务Bean的DEMO
 * 
 * 这里使用了 "建造器" 模式, 为了灵活区别 "必选项" 和 "可选项" 的参数设置.
 * 
 * @author zhihua.tang
 * 
 */
public final class PayInfoNetRequestBean {
	// 必选
	private final String orderId;// 订单编号
	
	public PayInfoNetRequestBean(String orderId) {
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
