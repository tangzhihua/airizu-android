package cn.airizu.domain.protocol.account_login;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.DomainBeanNullValueDefine;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class LogonParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof LogonNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		LogonNetRequestBean requestBean = (LogonNetRequestBean) netRequestDomainBean;
		String loginName = requestBean.getLoginName();
		String password = requestBean.getPassword();
		if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(password)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(LogonDatabaseFieldsConstant.RequestBean.loginName.name(), loginName);
		params.put(LogonDatabaseFieldsConstant.RequestBean.password.name(), password);
		
		// 可选参数
		if (requestBean.getClientVersion() != DomainBeanNullValueDefine.STRING_NULL_VALUE) {
			params.put(LogonDatabaseFieldsConstant.RequestBean.clientVersion.name(), requestBean.getClientVersion());
		}
		if (requestBean.getClientAVersion() != DomainBeanNullValueDefine.STRING_NULL_VALUE) {
			params.put(LogonDatabaseFieldsConstant.RequestBean.clientAVersion.name(), requestBean.getClientAVersion());
		}
		if (requestBean.getScreenSize() != DomainBeanNullValueDefine.STRING_NULL_VALUE) {
			params.put(LogonDatabaseFieldsConstant.RequestBean.screenSize.name(), requestBean.getScreenSize());
		}
		
		return params;
	}
}
