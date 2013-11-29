package cn.airizu.domain.protocol.order_freebook;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.DomainBeanNullValueDefine;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class FreeBookParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof FreeBookNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		FreeBookNetRequestBean requestBean = (FreeBookNetRequestBean) netRequestDomainBean;
		String roomId = requestBean.getRoomId();
		String checkInDate = requestBean.getCheckInDate();
		String checkOutDate = requestBean.getCheckOutDate();
		String voucherMethod = Integer.toString(requestBean.getVoucherMethod());
		String guestNum = requestBean.getGuestNum();
		if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkOutDate) || TextUtils.isEmpty(voucherMethod) || TextUtils.isEmpty(guestNum)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(FreeBookDatabaseFieldsConstant.RequestBean.roomId.name(), roomId);
		params.put(FreeBookDatabaseFieldsConstant.RequestBean.checkInDate.name(), checkInDate);
		params.put(FreeBookDatabaseFieldsConstant.RequestBean.checkOutDate.name(), checkOutDate);
		params.put(FreeBookDatabaseFieldsConstant.RequestBean.voucherMethod.name(), voucherMethod);
		params.put(FreeBookDatabaseFieldsConstant.RequestBean.guestNum.name(), guestNum);
		
		// 可选参数
		if (requestBean.getVoucherCode() != DomainBeanNullValueDefine.STRING_NULL_VALUE) {
			params.put(FreeBookDatabaseFieldsConstant.RequestBean.iVoucherCode.name(), requestBean.getVoucherCode());
		}
		if (requestBean.getPointNum() != DomainBeanNullValueDefine.STRING_NULL_VALUE) {
			params.put(FreeBookDatabaseFieldsConstant.RequestBean.pointNum.name(), requestBean.getPointNum());
		}
		
		return params;
	}
}
