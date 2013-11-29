package cn.airizu.domain.domainbean_tools;

/**
 * 将目标网络业务接口返回的响应字符串, 解析该业务接口对应的 "网络响应业务Bean"
 * 
 * @author zhihua.tang
 * 
 */
public interface IParseNetRespondStringToDomainBean {
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception;
}
