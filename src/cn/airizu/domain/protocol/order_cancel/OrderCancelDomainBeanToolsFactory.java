package cn.airizu.domain.protocol.order_cancel;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.24 取消订单
 * 
 * @author zhihua.tang
 */
public final class OrderCancelDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new OrderCancelParseDomainBeanToDD();
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new OrderCancelParseNetRespondStringToDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/order/cancel";
	}
}
