package cn.airizu.domain.protocol.pay_pay_info;

/**
 * 这是一个 "网络响应业务Bean" 的Demo 这里使用了 "静态工厂方法",
 * 因为返回的所有字段都是必须存在的
 * 
 * @author zhihua.tang
 * 
 */

public final class PayInfoNetRespondBean {
	private final String payInfo;// 消息
	
	public PayInfoNetRespondBean(String payInfo) {
		this.payInfo = payInfo;
	}
	
	public String getPayInfo() {
		return payInfo;
	}
	
	@Override
	public String toString() {
		return "PayInfoNetRespondBean [payInfo=" + payInfo + "]";
	}
	
}
