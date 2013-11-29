package cn.airizu.domain.nethelper;

import cn.airizu.domain.net_error_handle.NetErrorBean;

/**
 * 用于 "外观层 - DomainProtocolNetHelper" 和 "控制层 - Activity" 之间的回调
 * 
 * @author zhihua.tang
 */
public interface IDomainNetRespondCallback {
	
	/**
	 * 此方法处于非UI线程中
	 * 
	 * @param requestEvent
	 * @param errorBean
	 * @param respondDomainBean
	 */
	public void domainNetRespondHandleInNonUIThread(final Enum<?> requestEvent, final NetErrorBean errorBean, final Object respondDomainBean);
}
