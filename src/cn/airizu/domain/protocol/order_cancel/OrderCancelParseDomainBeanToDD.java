package cn.airizu.domain.protocol.order_cancel;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class OrderCancelParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof OrderCancelNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		OrderCancelNetRequestBean requestBean = (OrderCancelNetRequestBean) netRequestDomainBean;
		String orderId = requestBean.getOrderId();
		if (TextUtils.isEmpty(orderId)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(OrderCancelDatabaseFieldsConstant.RequestBean.orderId.name(), orderId);
		return params;
	}
}
