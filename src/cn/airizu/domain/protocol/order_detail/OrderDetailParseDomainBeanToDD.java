package cn.airizu.domain.protocol.order_detail;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class OrderDetailParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof OrderDetailNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		OrderDetailNetRequestBean requestBean = (OrderDetailNetRequestBean) netRequestDomainBean;
		String orderId = requestBean.getOrderId();
		if (TextUtils.isEmpty(orderId)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(OrderDetailDatabaseFieldsConstant.RequestBean.orderId.name(), orderId);
		return params;
	}
}
