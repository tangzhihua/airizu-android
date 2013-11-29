package cn.airizu.domain.protocol.review_item;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.26 获取评论项
 * 
 * @author zhihua.tang
 */
public final class ReviewItemDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return null;
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new ReviewItemParseNetRespondStringToDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/review/item";
	}
}
