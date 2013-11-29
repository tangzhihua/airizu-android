package cn.airizu.domain.nethelper.netthread;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import cn.airizu.domain.net_error_handle.DomainBeanNetRequestErrorBeanHandle;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.domain.nethelper.network_engine_for_http.HttpNetworkEngineFactoryMethodSingleton;
import cn.airizu.domain.nethelper.network_engine_for_http.IHttpNetworkEngine;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.NetConnectionManageTools;

/**
 * @author zhihua.tang
 */
public class DomainBeanNetThread extends Thread {
	private final String TAG = this.getClass().getSimpleName();
	
	private NetRequestEvent requestEvent = null;
	private INetThreadToNetHelperCallback networkCallback = null;
	
	public DomainBeanNetThread(NetRequestEvent requestEvent, INetThreadToNetHelperCallback networkCallback) {
		if (null == requestEvent || null == networkCallback) {
			throw new IllegalArgumentException("method parameter : requestEvent or networkCallback is null.");
		}
		this.requestEvent = requestEvent;
		this.networkCallback = networkCallback;
	}
	
	@Override
	public void run() {
		
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, " ");
		DebugLog.i(TAG, "<<<<<<<<<<     http request thread begin (" + requestEvent.getThreadID() + ")     >>>>>>>>>>");
		DebugLog.i(TAG, " ");
		
		byte[] netRespondRawEntityData = null;
		NetErrorBean netErrorBean = new NetErrorBean();
		
		try {
			
			do {
				
				// 1) 判断当前网络是否可用
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					break;
				}
				final NetConnectionManageTools netConnectionTest = new NetConnectionManageTools();
				if (!netConnectionTest.isNetAvailable()) {
					DebugLog.e(TAG, "当前网络不可用(可能是当前没有网络, 或者用户在飞行模式.)");
					netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_NET_UNAVAILABLE);
					break;
				}
				// ----------------------------------------------------------------------------
				
				// 2) 调用系统的网络引擎, 发起一个网络请求(系统的网络引擎, 可以是 HttpConnection 或者 HttpClient
				// ----------------------------------------------------------------------------
				if (isInterrupted()) {
					break;
				}
				// 调用系统的网络引擎(比如 HttpClient/HttpURLConnection), 发起网络请求
				// note : 获取网络响应输入流(网络响应输入流是可能没有的, 此时服务器只是给客户端一个响应, 而不给任何数据,
				// 所以要以netError.getErrorType()作为此次网络访问的判断条件, 而 netRespondRawEntityData 是可能为null的)
				final IHttpNetworkEngine httpNetworkEngine = HttpNetworkEngineFactoryMethodSingleton.getInstance().getHttpNetworkEngine();
				netRespondRawEntityData = httpNetworkEngine.requestNetByHttp(requestEvent.getHttpRequestParameterMap(), netErrorBean);
				if (netErrorBean.getErrorType() != NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS) {
					DebugLog.e(TAG, "调用具体的网络引擎发起网络请求执行失败(具体的网络引擎可能是HttpClient/HttpURLConnection).");
					break;
				}
			} while (false);
			
		} catch (ConnectException e) {
			e.printStackTrace();
			// 客户端网络连接异常
			netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
			netErrorBean.setErrorMessage("网络访问失败");
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			// 客户端网络访问超时
			netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
			netErrorBean.setErrorMessage("网络访问超时");
		} catch (Exception e) {
			e.printStackTrace();
			// 客户端代码发生了异常
			netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
			netErrorBean.setErrorMessage("客户端发生未知错误");
		} finally {
			
			if (isInterrupted()) {
				// 外部取消了当前线程
				DebugLog.i(TAG, "--> this thread(" + requestEvent.getThreadID() + ") is canceled !");
				netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_THREAD_IS_CANCELED);
			}
			
			// 根据错误类型, 错误代码, 获取对应的详细错误信息(因为错误信息可能分为 debug 版本和 release 版本.
			DomainBeanNetRequestErrorBeanHandle.handleNetRequestBean(netErrorBean);
			DebugLog.i(TAG, "netErr-->" + netErrorBean.toString());
			
			final NetRespondEvent respondEvent = new NetRespondEvent(requestEvent.getThreadID(), netRespondRawEntityData, netErrorBean);
			// 这里之前会直接切换回UI线程(主线程), 但是发现现在切回去, 还有很多初始化UI的工作要做,
			// 所以这里不切回UI线程, 让Activity来自己通过Handler切换回UI线程
			networkCallback.netThreadToNetHelperCallback(respondEvent, this);
			
			//
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, "         ----- http request thread end(" + requestEvent.getThreadID() + ") -----          ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			DebugLog.i(TAG, " ");
			
			/**
			 * 这两个外部传进来的对象引用, 必须设成null, 否则会照成死锁.
			 */
			requestEvent = null;
			networkCallback = null;
		}
	}
	
}
