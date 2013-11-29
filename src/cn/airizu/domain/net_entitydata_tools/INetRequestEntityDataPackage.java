package cn.airizu.domain.net_entitydata_tools;

import java.util.Map;

/**
 * 将数据字典集合, 打包成网络请求字符串, (可以在这里完成数据的加密工作)
 * @author zhihua.tang
 *
 */
public interface INetRequestEntityDataPackage {
	public String packageNetRequestEntityData(Map<String, String> domainDD) throws Exception;
}
