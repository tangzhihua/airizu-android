package cn.airizu.domain.protocol.account_forget_password;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class ForgetPasswordParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof ForgetPasswordNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		ForgetPasswordNetRequestBean requestBean = (ForgetPasswordNetRequestBean) netRequestDomainBean;
		
		String phoneNumber = requestBean.getPhoneNumber();
		if (TextUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("入参  phoneNumber 为空.");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(ForgetPasswordDatabaseFieldsConstant.RequestBean.phoneNumber.name(), phoneNumber);
		
		return params;
	}
}
