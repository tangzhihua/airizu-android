package cn.airizu.domain.protocol.order_submit;

import cn.airizu.domain.domainbean_tools.DomainBeanNullValueDefine;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRequestBean;

public final class OrderSubmitNetRequestBean {
	// 必选参数
	// 房间id
	private final String roomId;
	// 开始日期
	private final String checkInDate;
	// 结束日期
	private final String checkOutDate;
	// 入住人数
	private final String guestNum;
	// 优惠方式(0：不使用优惠卷；1：VIP优惠；2：普通优惠卷；3：现金卷)
	private final int voucherMethod;
	// 入住人姓名
	private final String checkinName;
	// 入住人电话
	private final String checkinPhone;
	
	// 可选参数
	private final String pointNum;// 积分
	private final String iVoucherCode;// 优惠码
	private final String keyword;// 来源关键字
	private final String source;// 来源
	
	private OrderSubmitNetRequestBean(Builder builder) {
		this.roomId = builder.roomId;
		this.checkInDate = builder.checkInDate;
		this.checkOutDate = builder.checkOutDate;
		this.guestNum = builder.guestNum;
		this.voucherMethod = builder.voucherMethod;
		this.checkinName = builder.checkinName;
		this.checkinPhone = builder.checkinPhone;
		
		this.pointNum = builder.pointNum;
		this.iVoucherCode = builder.iVoucherCode;
		this.keyword = builder.keyword;
		this.source = builder.source;
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
	
	public String getPointNum() {
		return pointNum;
	}
	
	public String getGuestNum() {
		return guestNum;
	}
	
	public int getVoucherMethod() {
		return voucherMethod;
	}
	
	public String getiVoucherCode() {
		return iVoucherCode;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getCheckinName() {
		return checkinName;
	}
	
	public String getCheckinPhone() {
		return checkinPhone;
	}
	
	@Override
	public String toString() {
		return "OrderSubmitNetRequestBean [roomId=" + roomId + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate + ", guestNum=" + guestNum + ", checkinName=" + checkinName
				+ ", checkinPhone=" + checkinPhone + ", pointNum=" + pointNum + ", voucherMethod=" + voucherMethod + ", iVoucherCode=" + iVoucherCode + ", keyword=" + keyword + ", source=" + source
				+ "]";
	}
	
	public static class Builder {
		// 必选参数
		private final String roomId;// 房间id
		private final String checkInDate;// 开始日期
		private final String checkOutDate;// 结束日期
		private final String guestNum;// 入住人数
		private final int voucherMethod;
		private final String checkinName;// 入住人姓名
		private final String checkinPhone;// 入住人电话
		
		// 可选参数
		private String pointNum = DomainBeanNullValueDefine.STRING_NULL_VALUE;// 积分
		private String iVoucherCode = DomainBeanNullValueDefine.STRING_NULL_VALUE;// 优惠码
		private String keyword = DomainBeanNullValueDefine.STRING_NULL_VALUE;// 来源关键字
		private String source = DomainBeanNullValueDefine.STRING_NULL_VALUE;// 来源
		
		public Builder(FreeBookNetRequestBean freeBookNetRequestBean, String checkinName, String checkinPhone) {
			this.roomId = freeBookNetRequestBean.getRoomId();
			this.checkInDate = freeBookNetRequestBean.getCheckInDate();
			this.checkOutDate = freeBookNetRequestBean.getCheckOutDate();
			this.guestNum = freeBookNetRequestBean.getGuestNum();
			this.voucherMethod = freeBookNetRequestBean.getVoucherMethod();
			this.checkinName = checkinName;
			this.checkinPhone = checkinPhone;
		}
		
		public Builder(String roomId, String checkInDate, String checkOutDate, String guestNum, int voucherMethod, String checkinName, String checkinPhone) {
			this.roomId = roomId;
			this.checkInDate = checkInDate;
			this.checkOutDate = checkOutDate;
			this.guestNum = guestNum;
			this.voucherMethod = voucherMethod;
			this.checkinName = checkinName;
			this.checkinPhone = checkinPhone;
		}
		
		public Builder pointNum(String pointNum) {
			this.pointNum = pointNum;
			return this;
		}
		
		public Builder voucherCode(String voucherCode) {
			this.iVoucherCode = voucherCode;
			return this;
		}
		
		public Builder keyword(String keyword) {
			this.keyword = keyword;
			return this;
		}
		
		public Builder source(String source) {
			this.source = source;
			return this;
		}
		
		public OrderSubmitNetRequestBean builder() {
			return new OrderSubmitNetRequestBean(this);
		}
	}
}
