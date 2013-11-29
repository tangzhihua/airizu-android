package cn.airizu.domain.protocol.room_search;

import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;

/**
 * 2.5 房间搜索
 * 
 * @author zhihua.tang
 */
public class RoomSearchDomainBeanToolsFactory implements IDomainBeanAbstractFactory {
	
	@Override
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy() {
		return new RoomSearchDomainBeanToDD();
	}
	
	@Override
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy() {
		return new RoomSearchParseNetRespondStringDomainBean();
	}
	
	@Override
	public String getURL() {
		return "http://124.65.163.102:819/app/room/search";
	}
	
}
