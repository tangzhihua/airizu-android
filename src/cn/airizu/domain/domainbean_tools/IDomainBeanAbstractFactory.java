package cn.airizu.domain.domainbean_tools;


/**
 * 业务Bean相关的工具方法
 * 
 * 这里罗列的接口是每个业务Bean都需要实现的.
 * @author zhihua.tang
 *
 */
public interface IDomainBeanAbstractFactory {
	/**
	 * 将当前业务Bean, 解析成跟后台数据接口对应的数据字典
	 * @return
	 */
	public IParseDomainBeanToDataDictionary getParseDomainBeanToDDStrategy();

	/**
	 * 将网络返回的数据字符串, 解析成当前业务Bean
	 * @return
	 */
	public IParseNetRespondStringToDomainBean getParseNetRespondStringToDomainBeanStrategy();

	/**
	 * 当前业务Bean, 对应的URL地址.
	 * @return
	 */
	public String getURL();
}
