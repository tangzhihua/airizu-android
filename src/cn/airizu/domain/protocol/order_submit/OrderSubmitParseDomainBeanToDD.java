package cn.airizu.domain.protocol.order_submit;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class OrderSubmitParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof OrderSubmitNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		OrderSubmitNetRequestBean requestBean = (OrderSubmitNetRequestBean) netRequestDomainBean;
		// 房间id
		String roomIdString = requestBean.getRoomId();
		// 入住日期
		String checkInDateString = requestBean.getCheckInDate();
		// 退房日期
		String checkOutDateString = requestBean.getCheckOutDate();
		// 入住人数
		String guestNumString = requestBean.getGuestNum();
		// 租客姓名
		String checkinNameString = requestBean.getCheckinName();
		// 租客手机
		String checkinPhoneString = requestBean.getCheckinPhone();
		if (TextUtils.isEmpty(roomIdString) || TextUtils.isEmpty(checkInDateString) || TextUtils.isEmpty(checkOutDateString) || TextUtils.isEmpty(guestNumString)
				|| TextUtils.isEmpty(checkinNameString) || TextUtils.isEmpty(checkinPhoneString)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		Map<String, String> params = new HashMap<String, String>();
		
		// 必须参数
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.roomId.name(), roomIdString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.checkInDate.name(), checkInDateString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.checkOutDate.name(), checkOutDateString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.guestNum.name(), guestNumString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.checkinName.name(), checkinNameString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.checkinPhone.name(), checkinPhoneString);
		params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.voucherMethod.name(), Integer.toString(requestBean.getVoucherMethod()));
		
		// 可选参数
		if (!TextUtils.isEmpty(requestBean.getPointNum())) {
			params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.pointNum.name(), requestBean.getPointNum());
		}
		if (!TextUtils.isEmpty(requestBean.getiVoucherCode())) {
			params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.iVoucherCode.name(), requestBean.getiVoucherCode());
		}
		if (!TextUtils.isEmpty(requestBean.getKeyword())) {
			params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.semKeyword.name(), requestBean.getKeyword());
		}
		if (!TextUtils.isEmpty(requestBean.getSource())) {
			params.put(OrderSubmitDatabaseFieldsConstant.RequestBean.semSource.name(), requestBean.getSource());
		}
		return params;
	}
}
