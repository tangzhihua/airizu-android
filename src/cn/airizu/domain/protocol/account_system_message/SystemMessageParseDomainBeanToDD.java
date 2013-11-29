package cn.airizu.domain.protocol.account_system_message;

import java.util.HashMap;
import java.util.Map;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class SystemMessageParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof SystemMessagesNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		SystemMessagesNetRequestBean requestBean = (SystemMessagesNetRequestBean) netRequestDomainBean;
		int pageNum = requestBean.getPageNum();
		if (pageNum < 0) {
			throw new IllegalArgumentException("pageNum 参数 < 0 ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(SystemMessageDatabaseFieldsConstant.RequestBean.pageNum.name(), Integer.toString(pageNum));
		
		return params;
	}
}
