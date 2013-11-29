package cn.airizu.domain.nethelper.netthread;

import java.util.Map;

import android.content.Context;
import cn.airizu.domain.nethelper.IDomainNetRespondCallback;

/**
 * "DomainProtocolNetHelper" 向 "DomainBeanNetThread" 进行数据通信的载体
 * 
 * @author zhihua.tang
 */
public final class NetRequestEvent {
	private final Context context;
	// 当前业务网络请求事件对应的 下载线程的线程ID
	private final int threadID;
	// 将 "网络请求业务Bean" 的 getClassName() 作为和这个业务Bean对应的抽象工厂产品的唯一识别Key
	private final String abstractFactoryMappingKey;
	// 控制层 对此次网络请求的标识
	private final Enum<?> requestEvent;
	// 控制层 的代理对象
	private final IDomainNetRespondCallback netRespondDelegate;
	// Http请求参数集合
	private final Map<String, String> httpRequestParameterMap;
	
	public NetRequestEvent(final Context context, final int threadID, final String abstractFactoryMappingKey, final Enum<?> requestEvent, final IDomainNetRespondCallback netRespondDelegate,
			final Map<String, String> httpRequestParameterMap) {
		
		this.context = context;
		this.threadID = threadID;
		this.abstractFactoryMappingKey = abstractFactoryMappingKey;
		this.requestEvent = requestEvent;
		this.netRespondDelegate = netRespondDelegate;
		this.httpRequestParameterMap = httpRequestParameterMap;
	}
	
	public Context getContext() {
		return context;
	}
	
	public int getThreadID() {
		return threadID;
	}
	
	public String getAbstractFactoryMappingKey() {
		return abstractFactoryMappingKey;
	}
	
	public Enum<?> getRequestEvent() {
		return requestEvent;
	}
	
	public IDomainNetRespondCallback getNetRespondDelegate() {
		return netRespondDelegate;
	}
	
	public Map<String, String> getHttpRequestParameterMap() {
		return httpRequestParameterMap;
	}
	
	@Override
	public String toString() {
		return "NetRequestEvent [context=" + context + ", threadID=" + threadID + ", abstractFactoryMappingKey=" + abstractFactoryMappingKey + ", requestEvent=" + requestEvent
				+ ", netRespondDelegate=" + netRespondDelegate + ", httpRequestParameterMap=" + httpRequestParameterMap + "]";
	}
	
}
