package cn.airizu.domain.protocol.account_login;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.2 用户登录
 * 
 * @author zhihua.tang
 */
public final class LogonDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new LogonParseDomainBeanToDD();
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new LogonParseNetRespondStringToDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/account/login";
	}
}
