package cn.airizu.domain.net_entitydata_tools;

/**
 * 网络访问过程中, 请求和返回的 "实体数据" 相关的工具类
 * @author zhihua.tang
 *
 */
public interface INetEntityDataTools {
	public INetRequestEntityDataPackage getNetRequestEntityDataPackage();
	public INetRespondRawEntityDataUnpack getNetRespondEntityDataUnpack();
	public IServerRespondDataTest getServerRespondDataTest();
}
