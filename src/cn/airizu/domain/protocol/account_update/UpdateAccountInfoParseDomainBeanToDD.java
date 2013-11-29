package cn.airizu.domain.protocol.account_update;

import java.util.HashMap;
import java.util.Map;

import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public class UpdateAccountInfoParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof UpdateAccountInfoNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		UpdateAccountInfoNetRequestBean requestBean = (UpdateAccountInfoNetRequestBean) netRequestDomainBean;
		
		Map<String, String> params = new HashMap<String, String>();
		
		// 必须参数
		params.put(UpdateAccountInfoDatabaseFieldsConstant.RequestBean.userName.name(), requestBean.getUserName());
		params.put(UpdateAccountInfoDatabaseFieldsConstant.RequestBean.sex.name(), requestBean.getSex());
		
		return params;
	}
	
}
