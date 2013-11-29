package cn.airizu.domain.nethelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.DomainBeanAbstractFactoryCacheSingleton;
import cn.airizu.domain.domainbean_tools.IDomainBeanAbstractFactory;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.domain.net_entitydata_tools.INetRespondRawEntityDataUnpack;
import cn.airizu.domain.net_entitydata_tools.IServerRespondDataTest;
import cn.airizu.domain.net_entitydata_tools.NetEntityDataToolsFactoryMethodSingleton;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.netthread.DomainBeanNetThread;
import cn.airizu.domain.nethelper.netthread.INetThreadToNetHelperCallback;
import cn.airizu.domain.nethelper.netthread.NetRequestEvent;
import cn.airizu.domain.nethelper.netthread.NetRespondEvent;
import cn.airizu.domain.nethelper.network_engine_for_http.HttpNetworkEngineParameterEnum;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.SimpleCookieSingleton;

/**
 * 封装整个业务协议网络访问相关的子系统的外观层
 * 
 * @author zhihua.tang
 */
public final class DomainProtocolNetHelperSingleton {
	
	private final String TAG = this.getClass().getSimpleName();
	
	// 这里不是UI线程(主线程), 所以不能在这里直接进行UI操作, 因为这里还会有很多UI初始化的工作,
	// 为了优化ANR问题 所以, 让Activity自己通过Handler来切回主线程.
	private INetThreadToNetHelperCallback networkCallback = new INetThreadToNetHelperCallback() {
		
		@Override
		public void netThreadToNetHelperCallback(final NetRespondEvent netRespondEvent, final Thread netThread) {
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, "<-------------  netThreadToNetHelperCallback --> start  --------------->");
			
			NetErrorBean netErrorBean = new NetErrorBean();
			NetRequestEvent requestEvent = null;
			Object netRespondDomainBean = null;
			String netRequestIndexString = "";
			
			try {
				do {
					if (netRespondEvent == null || netRespondEvent.getNetError() == null || netThread == null) {
						// 入参异常
						throw new IllegalArgumentException("method parameter : netRespondEvent or netRespondEvent.getNetError() or netThread or is null.");
					}
					
					if (netRespondEvent.getThreadID() < 0) {
						// 入参异常
						throw new IllegalArgumentException("method parameter : thread id is invalidate.");
					}
					
					netRequestIndexString = Integer.toString(netRespondEvent.getThreadID());
					if (TextUtils.isEmpty(netRequestIndexString)) {
						throw new IllegalArgumentException("将int类型的线程id, 转换成 string类型的失败.");
					}
					requestEvent = synchronousNetRequestEventBuf.get(netRequestIndexString);
					if (requestEvent == null) {
						throw new IllegalArgumentException("在 request event 缓存池中查找目标对象失败.");
					}
					
					// 获取 DomainBeanNetThread 层返回的 netErrorBean, 要先判断 DomainBeanNetThread 层执行是否正常
					netErrorBean.reinitialize(netRespondEvent.getNetError());
					if (netErrorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
						DebugLog.e(TAG, "网络访问错误 : 可能是->1)客户端网络不通; 2)服务器端网络连接超时; 3)本次网络请求被取消; 4)其他异常.");
						break;
					}
					
					// 获取从服务器得到的网络实体数据(此时是 "生数据")
					final byte[] netRawEntityData = netRespondEvent.getNetRespondRawEntityData();
					if (netRawEntityData == null || netRawEntityData.length <= 0) {
						DebugLog.e(TAG, "从服务器端获得的实体数据为空(EntityData), 这种情况有可能是正常的, 比如 退出登录 接口, 服务器就只是通知客户端访问成功, 而不发送任何实体数据. ");
						DebugLog.e(TAG, "出现这种情况的业务Bean是 --> " + requestEvent.getAbstractFactoryMappingKey());
						break;
					}
					if (netThread.isInterrupted()) {
						break;
					}
					
					// 将具体网络引擎层返回的 "原始未加工数据byte[]" 解包成 "可识别数据字符串(一般是utf-8)".
					// 这里要考虑网络传回的原生数据有加密的情况, 比如MD5加密的数据, 那么在这里先解密成可识别的字符串
					final INetRespondRawEntityDataUnpack netRespondRawEntityDataUnpack = NetEntityDataToolsFactoryMethodSingleton.getInstance().getNetRespondEntityDataUnpack();
					if (null == netRespondRawEntityDataUnpack) {
						throw new NullPointerException("解析服务器端返回的实体数据的 \"解码算法类(INetRespondRawEntityDataUnpack)\"是必须要实现的.");
					}
					final String netUnpackedData = netRespondRawEntityDataUnpack.unpackNetRespondRawEntityData(netRawEntityData);
					if (TextUtils.isEmpty(netUnpackedData)) {
						throw new NullPointerException("解析服务器端返回的实体数据失败.");
					}
					DebugLog.i(TAG, "--> net respond unpacked data(" + netUnpackedData + ")");
					if (netThread.isInterrupted()) {
						break;
					}
					
					// 检查服务器返回的数据是否有效, 如果无效, 要获取服务器返回的错误码和错误描述信息
					// (比如说某次网络请求成功了, 但是服务器那边没有有效的数据给客户端, 所以服务器会返回错误码和描述信息告知客户端访问结果)
					final IServerRespondDataTest serverRespondDataTest = NetEntityDataToolsFactoryMethodSingleton.getInstance().getServerRespondDataTest();
					if (serverRespondDataTest == null) {
						throw new NullPointerException("\"检查服务器返回是否有效(IServerRespondDataTest)\" 的算法类, 是必须实现的");
					}
					final NetErrorBean serverRespondDataError = serverRespondDataTest.testServerRespondDataError(netUnpackedData);
					if (serverRespondDataError.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
						// 如果服务器没有有效的数据到客户端, 那么就不需要创建 "网络响应业务Bean"
						DebugLog.e(TAG, "服务器端告知客户端, 本次网络访问未获取到有效数据(具体情况, 可以查看服务器端返回的错误代码和错误信息)");
						DebugLog.e(TAG, serverRespondDataError.toString());
						netErrorBean.reinitialize(serverRespondDataError);
						break;
					}
					if (netThread.isInterrupted()) {
						break;
					}
					
					// 将 "已经解包的可识别数据字符串" 解析成 "具体的业务响应数据Bean"
					// note : 将服务器返回的数据字符串(已经解密, 解码完成了), 解析成对应的 "网络响应业务Bean"
					final IDomainBeanAbstractFactory domainBeanAbstractFactoryObject = DomainBeanAbstractFactoryCacheSingleton.getInstance().getDomainBeanAbstractFactoryObjectByKey(
							requestEvent.getAbstractFactoryMappingKey());
					if (domainBeanAbstractFactoryObject != null) {
						final IParseNetRespondStringToDomainBean domainBeanParseAlgorithm = domainBeanAbstractFactoryObject.getParseNetRespondStringToDomainBeanStrategy();
						if (domainBeanParseAlgorithm != null) {
							netRespondDomainBean = domainBeanParseAlgorithm.parseNetRespondStringToDomainBean(netUnpackedData);
							if (null == netRespondDomainBean) {
								throw new NullPointerException("创建 网络响应业务Bean失败, 出现这种情况的业务Bean是 --> " + requestEvent.getAbstractFactoryMappingKey());
							}
							DebugLog.i(TAG, "netRespondDomainBean->" + netRespondDomainBean.toString());
						}
					}
					
					// ----------------------------------------------------------------------------
				} while (false);
			} catch (Exception e) {
				e.printStackTrace();
				netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
			} finally {
				
				//
				if (netErrorBean.getErrorType() == NetErrorTypeEnum.NET_ERROR_TYPE_SERVER_NET_ERROR) {
					if (netErrorBean.getErrorCode() == 4000) {
						// 当前用户的 SESSION 已经失效了,
						// 需要引擎帮助其自动登录
						// ////////////////////////////////////////////////////////////////////////////////////////////
						// 这里需要好好设计的, 目前没有想好怎么处理好
						// 思路1 : 用户请求一个需要SESSION的网络接口时,
						// 被告知SESSION已经失效,
						// 那么引擎需要帮助用户先重新登录,
						// 然后再次发起用户刚才提交的那个网络接口的访问.
						
						// 思路2 : 当发现用户SESSION失效时,
						// Activity自动跳转到 登录界面, 等登录成功后,
						// 在返回到目标Activity的上一层,
						// 然后让用户重新发起请求.
						
					}
				}
				
				if (netThread != null && !netThread.isInterrupted()) {
					if (requestEvent != null && requestEvent.getNetRespondDelegate() != null) {
						// 通知 "控制层" 本次网络访问的结果
						requestEvent.getNetRespondDelegate().domainNetRespondHandleInNonUIThread(requestEvent.getRequestEvent(), netErrorBean, netRespondDomainBean);
					}
				}
				
				if (!TextUtils.isEmpty(netRequestIndexString)) {
					// 清除缓存
					synchronousNetRequestEventBuf.remove(netRequestIndexString);
					synchronousNetThreadBuf.remove(netRequestIndexString);
				}
			}
			
			DebugLog.i(TAG, "<-------------  netThreadToNetHelperCallback --> end  --------------->");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
		}
	};
	
	/**
	 * 网络请求索引计数器
	 */
	private int netRequestIndexCounter = 0;
	/**
	 * 发起一个网络请求失败的标识
	 */
	public static final int IDLE_NETWORK_REQUEST_ID = -2012;
	
	/**
	 * 当前类要做成单例模式
	 */
	private DomainProtocolNetHelperSingleton() {
	}
	
	private static DomainProtocolNetHelperSingleton myInstance = null;
	
	public synchronized static DomainProtocolNetHelperSingleton getInstance() {
		if (null == myInstance) {
			myInstance = new DomainProtocolNetHelperSingleton();
		}
		return myInstance;
	}
	
	/**
	 * 当前正在并发执行的 "网络请求内部事件(DomainProtocolNetRequestEvent)" 缓存集合
	 */
	private Map<String, NetRequestEvent> synchronousNetRequestEventBuf = new HashMap<String, NetRequestEvent>();
	/**
	 * 当前正在并发的网络请求线程缓存集合
	 */
	private Map<String, DomainBeanNetThread> synchronousNetThreadBuf = new HashMap<String, DomainBeanNetThread>();
	
	/**
	 * 给控制层使用的, 发起一个网络接口请求的方法
	 * 
	 * @param netRequestDomainBean 请求目标业务协议网络接口, 所需要的 "网络请求业务Bean"
	 * @param requestEvent 具体控制层定义的, 对本次网络请求的抽象定义, 用于当网络接口返回给控制层的时候, 控制层是根据这个参数来区分是哪个网络请求返回了 (因为控制层具体类只会实现一个 INetRespondDelegate代理回调接口,用于处理当前控制层 所发起的所有类型的网络请求事件).
	 * @param netRespondDelegate 网络响应后, 通过此代理来跟控制层进行通讯
	 * @return 本次网络请求事件对应的 requestIndex, 控制层通过此索引来取消本次网络请求.如果失败, 返回 IDLE_NETWORK_REQUEST_ID
	 */
	public int requestDomainProtocol(final Context context, final Object netRequestDomainBean, final Enum<?> requestEvent, final IDomainNetRespondCallback netRespondDelegate) {
		return requestDomainProtocol(context, netRequestDomainBean, requestEvent, netRespondDelegate, null);
	}
	
	/**
	 * @param context
	 * @param netRequestDomainBean
	 * @param requestEvent
	 * @param netRespondDelegate
	 * @param extraHttpRequestParameterMap 此参数是为那种需要兼容不同HTTP参数的情况, 是不好的服务器设计
	 * @return
	 */
	public int requestDomainProtocol(final Context context, final Object netRequestDomainBean, final Enum<?> requestEvent, final IDomainNetRespondCallback netRespondDelegate,
			final Map<String, String> extraHttpRequestParameterMap) {
		
		int netRequestIndex = ++netRequestIndexCounter;
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     Request a domain protocol begin (" + netRequestIndex + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		try {
			// effective for java 38 检查参数有效性, 对于共有的方法,
			// 要使用异常机制来通知调用方发生了入参错误.
			if (context == null || netRequestDomainBean == null || netRespondDelegate == null || requestEvent == null) {
				throw new NullPointerException("context or netRequestDomainBean or netRespondDelegate is null!");
			}
			
			/**
			 * 将 "网络请求业务Bean" 的 完整class name 作为和这个业务Bean对应的"业务接口" 绑定的所有相关的处理算法的唯一识别Key
			 */
			final String abstractFactoryMappingKey = netRequestDomainBean.getClass().getName();
			DebugLog.i(TAG, "request index-->" + netRequestIndex);
			DebugLog.i(TAG, "abstractFactoryMappingKey--> " + abstractFactoryMappingKey);
			DebugLog.i(TAG, "request event--> " + requestEvent.name());
			DebugLog.i(TAG, "net callback delegate class--> " + netRespondDelegate.getClass().getName());
			
			do {
				// 这里的设计使用了 "抽象工厂" 设计模式
				final IDomainBeanAbstractFactory domainBeanAbstractFactoryObject = DomainBeanAbstractFactoryCacheSingleton.getInstance().getDomainBeanAbstractFactoryObjectByKey(
						abstractFactoryMappingKey);
				if (null == domainBeanAbstractFactoryObject) {
					throw new NullPointerException("not implements interface of IDomainBeanAbstractFactory! must be implements IDomainBeanAbstractFactory!");
				}
				
				// 获取当前业务网络接口, 对应的URL
				final String url = domainBeanAbstractFactoryObject.getURL();
				DebugLog.i(TAG, "url-->" + url);
				
				// HTTP 请求方法类型, 默认是GET
				String httpRequestMethod = "GET";
				
				/**
				 * 处理HTTP 请求实体数据, 如果有实体数据的话, 就设置 RequestMethod 为 "POST" 目前POST 和 GET的认定标准是, 有附加参数就使用POST, 没有就使用GET(这里要跟后台开发团队事先约定好)
				 */
				final IParseDomainBeanToDataDictionary parseDomainBeanToDataDictionary = domainBeanAbstractFactoryObject.getParseDomainBeanToDDStrategy();
				String httpEntityDataString = "";
				do {
					if (null == parseDomainBeanToDataDictionary) {
						// 没有额外的数据需要上传服务器
						break;
					}
					
					/**
					 * 首先获取目标 "网络请求业务Bean" 对应的 "业务协议参数字典 domainParams" (字典由K和V组成,K是"终端应用与后台通信接口协议.doc" 文档中的业务协议关键字, V就是具体的值.)
					 */
					final Map<String, String> domainDD = parseDomainBeanToDataDictionary.parseDomainBeanToDataDictionary(netRequestDomainBean);
					if (null == domainDD || domainDD.size() <= 0) {
						// 没有额外的数据需要上传服务器
						break;
					}
					DebugLog.i(TAG, "domainParams-->" + domainDD.toString());
					
					// 然后将业务参数字典, 拼装成HTTP请求实体字符串
					// 业务字典是 Map<String, String> 格式的, 在这里要完成对 Map<String, String>格式的数据再次加工,
					// 比如 "key1=value1, key2=value2" 或者 "JSON格式" 或者 "XML格式" 或者 "自定义格式"
					httpEntityDataString = NetEntityDataToolsFactoryMethodSingleton.getInstance().getNetRequestEntityDataPackage().packageNetRequestEntityData(domainDD);
					if (TextUtils.isEmpty(httpEntityDataString)) {
						throw new NullPointerException("packageNetRequestEntityData is fail ! ");
					}
					// DebugLog.i(TAG, "httpEntityDataString-->" + httpEntityDataString);
					
					// 最终确认确实需要使用POST方式发送数据
					httpRequestMethod = "POST";
				} while (false);
				DebugLog.i(TAG, "httpRequestMethod-->" + httpRequestMethod);
				
				// TODO : 这里将来要提出一个方法
				final Map<String, String> httpRequestParameterMap = new HashMap<String, String>();
				httpRequestParameterMap.put(HttpNetworkEngineParameterEnum.URL.name(), url);
				httpRequestParameterMap.put(HttpNetworkEngineParameterEnum.REQUEST_METHOD.name(), httpRequestMethod);
				httpRequestParameterMap.put(HttpNetworkEngineParameterEnum.ENTITY_DATA.name(), httpEntityDataString);
				httpRequestParameterMap.put(HttpNetworkEngineParameterEnum.COOKIE.name(), SimpleCookieSingleton.getInstance().getCookieString());
				httpRequestParameterMap.put(HttpNetworkEngineParameterEnum.CONTENT_TYPE.name(), "application/x-www-form-urlencoded");
				if (extraHttpRequestParameterMap != null && extraHttpRequestParameterMap.size() > 0) {
					httpRequestParameterMap.putAll(extraHttpRequestParameterMap);
				}
				// //////////////////////////////////////////////////////////////////////////////
				
				/**
				 * 创建一个在 "网络间接层" 内部进行流转的 "网络请求事件"对象. 本次进行的网络接口请求的全部数据都在这个 "事件对象" 中进行了保存.
				 */
				final NetRequestEvent netRequestEvent = new NetRequestEvent(context, netRequestIndex, abstractFactoryMappingKey, requestEvent, netRespondDelegate, httpRequestParameterMap);
				/**
				 * 将这个 "内部网络请求事件" 缓存到集合synchronousNetRequestEventBuf中
				 */
				synchronousNetRequestEventBuf.put(Integer.toString(netRequestIndex), netRequestEvent);
				
				/**
				 * 创建一个全新的网络请求线程
				 */
				final DomainBeanNetThread httpRequestThread = new DomainBeanNetThread(netRequestEvent, networkCallback);
				httpRequestThread.setPriority(7);// 设置优先级
				httpRequestThread.start();// 启动线程
				
				/**
				 * 将新建的网络线程, 缓存到集合中, 这样就可以随时关闭这个线程.
				 */
				synchronousNetThreadBuf.put(Integer.toString(netRequestIndex), httpRequestThread);
			} while (false);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// 清理缓存数据
			synchronousNetRequestEventBuf.remove(Integer.toString(netRequestIndex));
			synchronousNetThreadBuf.remove(Integer.toString(netRequestIndex));
			
			// 出现异常, 本次网络请求
			netRequestIndex = IDLE_NETWORK_REQUEST_ID;
		}
		
		//
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "         ----- Request a domain protocol end (" + netRequestIndex + ") -----          ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		
		return netRequestIndex;
	}
	
	/**
	 * 取消一个 "网络请求索引" 所对应的 "网络请求命令"
	 * 
	 * @param netRequestIndex : 网络请求命令对应的索引
	 */
	public void cancelNetRequestByRequestIndex(final int netRequestIndex) {
		if (netRequestIndex == IDLE_NETWORK_REQUEST_ID) {
			return;
		}
		
		final DomainBeanNetThread httpRequestThread = synchronousNetThreadBuf.get(Integer.toString(netRequestIndex));
		if (httpRequestThread != null) {
			httpRequestThread.interrupt();
		}
	}
	
	/**
	 * 批量取消网络请求
	 * 
	 * @param key key可能是 netRespondDelegate, 也可能是发起这个网络请求的控制层 context
	 */
	private void bulkCancelNetRequestByKey(final Object key) {
		
		do {
			if (null == key) {
				break;
			}
			
			if (null == synchronousNetRequestEventBuf || synchronousNetRequestEventBuf.size() <= 0) {
				break;
			}
			
			// .values() : a collection of the values contained in this map.
			final Collection<NetRequestEvent> netRequestEventCollection = synchronousNetRequestEventBuf.values();
			for (NetRequestEvent netRequestEvent : netRequestEventCollection) {
				if (netRequestEvent.getNetRespondDelegate().equals(key) || netRequestEvent.getContext().equals(key)) {
					final Thread httpRequestThread = synchronousNetThreadBuf.get(Integer.toString(netRequestEvent.getThreadID()));
					if (httpRequestThread != null) {
						// 不要在这里做删除动作, 而是通知相关线程自己结束其生命周期, 然后统一在 Handler中释放缓存的对象.
						httpRequestThread.interrupt();
					}
				}
			}
			
		} while (false);
		
	}
	
	/**
	 * 取消跟目标 "netRespondDelegate" 相关的所有网络请求
	 * 
	 * @param netRespondDelegate : 网络响应代理
	 */
	public void cancelAllNetRequestWithThisNetRespondDelegate(final IDomainNetRespondCallback netRespondDelegate) {
		bulkCancelNetRequestByKey(netRespondDelegate);
	}
	
	/**
	 * 取消跟目标 "context" 相关的所有网络请求
	 * 
	 * @param netRespondDelegate : 上下文
	 */
	public void cancelAllNetRequestWithThisContext(final Context context) {
		bulkCancelNetRequestByKey(context);
	}
}
