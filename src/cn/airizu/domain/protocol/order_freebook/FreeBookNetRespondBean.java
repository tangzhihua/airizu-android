package cn.airizu.domain.protocol.order_freebook;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class FreeBookNetRespondBean implements Serializable {
	// 订单总额
	private final double totalPrice;
	// 预付订金
	private final double advancedDeposit;
	// 线下支付
	private final double underLinePaid;
	// 用户积分
	private final int availablePoint;
	// 是否优惠（0优惠，1没优惠）
	private final boolean isCheap;
	
	public FreeBookNetRespondBean(double totalPrice, double advancedDeposit, double underLinePaid, int availablePoint, boolean isCheap) {
		this.totalPrice = totalPrice;
		this.advancedDeposit = advancedDeposit;
		this.underLinePaid = underLinePaid;
		this.availablePoint = availablePoint;
		this.isCheap = isCheap;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public double getAdvancedDeposit() {
		return advancedDeposit;
	}
	
	public double getUnderLinePaid() {
		return underLinePaid;
	}
	
	public int getAvailablePoint() {
		return availablePoint;
	}
	
	public boolean isCheap() {
		return isCheap;
	}
	
	@Override
	public String toString() {
		return "FreeBookNetRespondBean [totalPrice=" + totalPrice + ", advancedDeposit=" + advancedDeposit + ", underLinePaid=" + underLinePaid + ", availablePoint=" + availablePoint
				+ ", isCheap=" + isCheap + "]";
	}
}
