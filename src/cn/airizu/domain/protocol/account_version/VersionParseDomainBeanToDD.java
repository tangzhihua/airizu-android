package cn.airizu.domain.protocol.account_version;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class VersionParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof VersionNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		VersionNetRequestBean requestBean = (VersionNetRequestBean) netRequestDomainBean;
		String currentVersion = requestBean.getCurrentVersion();
		if (TextUtils.isEmpty(currentVersion)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(VersionDatabaseFieldsConstant.RequestBean.currentVersion.name(), currentVersion);
		
		return params;
	}
}
