package cn.airizu.domain.nethelper.netthread;


/**
 * 用于 "网络访问线程 - DomainBeanNetThread" 和 "外观层 - DomainProtocolNetHelper" 之间的回调
 * 
 * @author zhihua.tang
 */
public interface INetThreadToNetHelperCallback {
	public void netThreadToNetHelperCallback(final NetRespondEvent netRespondEvent, final Thread netThread);
}
