package cn.airizu.domain.protocol.pay_pay_info;

import java.util.HashMap;
import java.util.Map;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

/**
 * 一个具体的 "网络请求业务Bean", 对应的 "业务协议字段字典" 封装方法
 * 
 * @author zhihua.tang
 * 
 */
public final class PayInfoParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof PayInfoNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		PayInfoNetRequestBean requestBean = (PayInfoNetRequestBean) netRequestDomainBean;
		
		Map<String, String> params = new HashMap<String, String>();
		
		// 必须参数
		params.put(PayInfoDatabaseFieldsConstant.RequestBean.orderId.name(), requestBean.getOrderId());
		return params;
	}
}
