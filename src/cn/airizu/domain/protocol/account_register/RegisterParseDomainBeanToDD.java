package cn.airizu.domain.protocol.account_register;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public class RegisterParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof RegisterNetResquestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		RegisterNetResquestBean requestBean = (RegisterNetResquestBean) netRequestDomainBean;
		
		String userName = requestBean.getUserName();
		String phoneNumber = requestBean.getPhoneNumber();
		String email = requestBean.getEmail();
		String password = requestBean.getPassword();
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(RegisterDatabaseFieldsConstant.RequestBean.userName.name(), userName);
		params.put(RegisterDatabaseFieldsConstant.RequestBean.phoneNumber.name(), phoneNumber);
		params.put(RegisterDatabaseFieldsConstant.RequestBean.email.name(), email);
		params.put(RegisterDatabaseFieldsConstant.RequestBean.password.name(), password);
		
		return params;
	}
}
