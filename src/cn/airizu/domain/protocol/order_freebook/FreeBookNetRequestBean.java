package cn.airizu.domain.protocol.order_freebook;

import java.io.Serializable;

import cn.airizu.domain.domainbean_tools.DomainBeanNullValueDefine;

@SuppressWarnings("serial")
public final class FreeBookNetRequestBean implements Serializable {
	// 房间id
	private final String roomId;
	// 入住时间
	private final String checkInDate;
	// 退房时间
	private final String checkOutDate;
	// 优惠劵码
	private final String iVoucherCode;
	// 优惠卷类型
	// 0：不使用优惠
	// 1：VIP优惠
	// 2：普通优惠卷
	// 3：现金卷
	private final int voucherMethod;
	// 积分
	private final String pointNum;
	// 入住人数
	private final String guestNum;
	
	private FreeBookNetRequestBean(Builder builder) {
		this.roomId = builder.roomId;
		this.checkInDate = builder.checkInDate;
		this.checkOutDate = builder.checkOutDate;
		this.guestNum = builder.guestNum;
		this.iVoucherCode = builder.iVoucherCode;
		this.voucherMethod = builder.voucherMethod;
		this.pointNum = builder.pointNum;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	public String getCheckInDate() {
		return checkInDate;
	}
	
	public String getCheckOutDate() {
		return checkOutDate;
	}
	
	public String getVoucherCode() {
		return iVoucherCode;
	}
	
	public int getVoucherMethod() {
		return voucherMethod;
	}
	
	public String getPointNum() {
		return pointNum;
	}
	
	public String getGuestNum() {
		return guestNum;
	}
	
	@Override
	public String toString() {
		return "FreeBookNetRequestBean [roomId=" + roomId + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate + ", iVoucherCode=" + iVoucherCode + ", voucherMethod="
				+ voucherMethod + ", pointNum=" + pointNum + ", guestNum=" + guestNum + "]";
	}
	
	public static class Builder {
		// 必选参数
		private final String roomId;
		private final String checkInDate;
		private final String checkOutDate;
		private final String guestNum;
		// 这个字段比较特殊
		private int voucherMethod;
		
		// 可选参数
		private String iVoucherCode = DomainBeanNullValueDefine.STRING_NULL_VALUE;
		private String pointNum = DomainBeanNullValueDefine.STRING_NULL_VALUE;
		
		public Builder(FreeBookNetRequestBean freeBookNetRequestBean) {
			this.roomId = freeBookNetRequestBean.getRoomId();
			this.checkInDate = freeBookNetRequestBean.getCheckInDate();
			this.checkOutDate = freeBookNetRequestBean.getCheckOutDate();
			this.voucherMethod = freeBookNetRequestBean.getVoucherMethod();
			this.guestNum = freeBookNetRequestBean.getGuestNum();
		}
		
		public Builder(String roomId, String checkInDate, String checkOutDate, int voucherMethod, String guestNum) {
			this.roomId = roomId;
			this.checkInDate = checkInDate;
			this.checkOutDate = checkOutDate;
			this.voucherMethod = voucherMethod;
			this.guestNum = guestNum;
		}
		
		public Builder voucherMethod(int voucherMethod) {
			this.voucherMethod = voucherMethod;
			return this;
		}
		
		public Builder iVoucherCode(String iVoucherCode) {
			this.iVoucherCode = iVoucherCode;
			return this;
		}
		
		public Builder pointNum(String pointNum) {
			this.pointNum = pointNum;
			return this;
		}
		
		public FreeBookNetRequestBean builder() {
			return new FreeBookNetRequestBean(this);
		}
	}
}
