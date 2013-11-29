package cn.airizu.domain.domainbean_tools;

import java.util.Map;

/**
 * 把一个 "网络请求业务Bean" 解析成其对应网络业务接口的 "数据字典"
 * 
 * @author zhihua.tang
 * 
 */
public interface IParseDomainBeanToDataDictionary {
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean);
}
