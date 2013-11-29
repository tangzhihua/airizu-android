package cn.airizu.domain.net_entitydata_tools;

import cn.airizu.domain.net_error_handle.NetErrorBean;

/**
 * 测试从服务器端返回的数据是否是有效的(数据要先解包, 然后再根据错误码做判断)
 * @author zhihua.tang
 *
 */
public interface IServerRespondDataTest {
	public NetErrorBean testServerRespondDataError(final String netUnpackedData) throws Exception;
}
