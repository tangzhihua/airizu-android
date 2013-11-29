package cn.airizu.domain.protocol.account_system_message;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.18 获得系统通知
 * 
 * @author zhihua.tang
 */
public class SystemMessagesDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new SystemMessageParseDomainBeanToDD();
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new SystemMessagesParseNetRespondStringToDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/account/systemMessage";
	}
}
