package cn.airizu.domain.protocol.order_freebook;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.20 订单预订
 * 
 * @author zhihua.tang
 */
public final class FreeBookDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new FreeBookParseDomainBeanToDD();
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new FreeBookParseNetRespondStringToDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/order/freeBook";
	}
}
