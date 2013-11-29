package cn.airizu.domain.protocol.address_districts;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

/**
 * 一个具体的 "网络请求业务Bean", 对应的 "业务协议字段字典" 封装方法
 * 
 * @author zhihua.tang
 */
public final class DistrictsParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof DistrictsNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		DistrictsNetRequestBean requestBean = (DistrictsNetRequestBean) netRequestDomainBean;
		String cityId = requestBean.getCityId();
		if (TextUtils.isEmpty(cityId)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(DistrictsDatabaseFieldsConstant.RequestBean.cityId.name(), cityId);
		
		return params;
	}
}
