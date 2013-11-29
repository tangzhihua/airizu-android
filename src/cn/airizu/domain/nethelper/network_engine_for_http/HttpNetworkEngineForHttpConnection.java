package cn.airizu.domain.nethelper.network_engine_for_http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import android.text.TextUtils;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;
import cn.airizu.toolutils.DebugLog;
import cn.airizu.toolutils.NetConnectionManageTools;
import cn.airizu.toolutils.StreamTools;

public final class HttpNetworkEngineForHttpConnection implements IHttpNetworkEngine {
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	public byte[] requestNetByHttp(final Map<String, String> httpRequestParameterMap, final NetErrorBean netErrorBeanForOUT) throws Exception {
		DebugLog.i(TAG, "--> requestNetByHttpConnection is start ! ");
		
		if (httpRequestParameterMap == null || httpRequestParameterMap.size() <= 0 || netErrorBeanForOUT == null) {
			throw new IllegalArgumentException("方法入参异常 ! httpRequestHeadMap/netErrorForOUT 都不能为空 ! ");
		}
		
		NetErrorBean netErrorBean = new NetErrorBean();
		
		final String urlString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.URL.name());
		final String requestMethodString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.REQUEST_METHOD.name());
		final String entityDataString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.ENTITY_DATA.name());
		final String readTimeoutString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.READ_TIMEOUT.name());
		final String cookieString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.COOKIE.name());
		final String contentTypeString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.CONTENT_TYPE.name());
		// final String contentEncodingString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.CONTENT_ENCODING.name());
		// final String userAgentString = httpRequestParameterMap.get(HttpNetworkEngineParameterEnum.USER_AGENT.name());
		if (TextUtils.isEmpty(urlString) || TextUtils.isEmpty(requestMethodString)) {
			throw new IllegalArgumentException("主要的Http请求参数不能为空(URL/REQUEST_METHOD) ! ");
		}
		
		URL urlObject = null;
		HttpURLConnection httpConnection = null;
		OutputStream httpOutStream = null;
		InputStream httpInStream = null;
		byte[] netRawData = null;
		NetConnectionManageTools netConnectionUtils = new NetConnectionManageTools();
		
		try {
			
			urlObject = new URL(urlString);
			
			// 建立连接
			if (netConnectionUtils.isWapNetwork()) {
				DebugLog.i(TAG, "isWapNetwork, and set proxy, and openConnection");
				
				// 如果使用wap 网络，需要设置代理，没有的话直连.
				SocketAddress sa = new InetSocketAddress(netConnectionUtils.getProxy(), Integer.valueOf(netConnectionUtils.getProxyPort()));
				// 定义代理，此处的Proxy是源自java.net
				Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, sa);
				httpConnection = (HttpURLConnection) urlObject.openConnection(proxy);
			} else {
				
				DebugLog.i(TAG, "isNetNetwork, and openConnection");
				httpConnection = (HttpURLConnection) urlObject.openConnection();
			}
			
			DebugLog.i(TAG, "openConnection is successful ! ");
			
			// 设置请求超时时间
			int timeoutInt = 10 * 1000;
			if (!TextUtils.isEmpty(readTimeoutString)) {
				try {
					timeoutInt = Integer.valueOf(readTimeoutString).intValue();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			httpConnection.setReadTimeout(timeoutInt);
			
			// 设置Cookie
			if (!TextUtils.isEmpty(cookieString)) {
				httpConnection.setRequestProperty("Cookie", cookieString);
			}
			
			// 设置请求类型
			if (requestMethodString.equals("GET")) {
				DebugLog.i(TAG, "requestMethod is GET");
				
				// GET的情况
				httpConnection.setRequestMethod("GET");// 必须是大写
				
			} else {
				
				DebugLog.i(TAG, "requestMethod is POST");
				
				// POST的情况
				httpConnection.setRequestMethod("POST");// 必须是大写
				
				// POST方式的相关设置 使用POST方式发送数据, 必须要面向HTTP协议
				httpConnection.setDoOutput(true);// 如果通过POST提交数据,必须设置允许对外输出数据
				
				if (!TextUtils.isEmpty(contentTypeString)) {
					httpConnection.setRequestProperty("Content-Type", contentTypeString);
				} else {
					httpConnection.setRequestProperty("Content-Type", HTTP.DEFAULT_CONTENT_TYPE);
				}
				
				if (!TextUtils.isEmpty(entityDataString)) {
					// 必须将实体数据转换成二进制格式的byte[]
					byte[] entityDataForByteFormat = entityDataString.getBytes();
					// 内容长度
					httpConnection.setRequestProperty("Content-Length", String.valueOf(entityDataForByteFormat.length));
					
					// 从客户端发送数据到服务器, 就需要从连接中获取一个输出流
					DebugLog.i(TAG, "start getOutputStream from httpConnection");
					httpOutStream = httpConnection.getOutputStream();
					DebugLog.i(TAG, "getOutputStream is successful");
					
					DebugLog.i(TAG, "start write to server");
					httpOutStream.write(entityDataForByteFormat);
					httpOutStream.flush();// 本方法表示立刻将内存中的数据刷给对方
					httpOutStream.close();
					httpOutStream = null;
					DebugLog.i(TAG, "write to server is successful");
				}
			}
			
			// ----------------------------------------------------------------------------
			// 20121016 发现这里有时候会报如下异常, 原因未知
			// java.io.EOFException at libcore.io.Streams.readAsciiLine
			
			DebugLog.i(TAG, "start getResponseCode() ");
			netErrorBean.setErrorCode(httpConnection.getResponseCode());
			DebugLog.i(TAG, "start getResponseMessage() ");
			netErrorBean.setErrorMessage(httpConnection.getResponseMessage());
			
			// ----------------------------------------------------------------------------
			if (netErrorBean.getErrorCode() == HttpStatus.SC_OK) {
				DebugLog.i(TAG, "ErrorCode is HttpStatus.SC_OK ");
				
				/**
				 * 网络访问成功, 获取服务器传回的数据
				 */
				httpInStream = httpConnection.getInputStream();
				// 从流中把数据读取出来
				netRawData = StreamTools.readInputStream(httpInStream);
				
			} else {
				// 客户端网络访问失败
				netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_NET_ERROR);
				
				DebugLog.e(TAG, "net error code is not HttpStatus.SC_OK ! is=" + netErrorBean.getErrorCode());
			}
			
			DebugLog.i(TAG, "--> requestNetByHttpConnection is end ! ");
			// note : 会先执行 finally 中的代码, 然后才走这里的 return
			return netRawData;
		} catch (Exception e) {
			DebugLog.e(TAG, "has exception in requestNetByHttpConnection ! ");
			netErrorBean.setErrorType(NetErrorTypeEnum.NET_ERROR_TYPE_CLIENT_EXCEPTION);
			throw e;
		} finally {
			
			// 告知外部, 网络引擎的调用情况是否正常
			netErrorBeanForOUT.reinitialize(netErrorBean);
			
			if (httpConnection != null) {
				try {
					httpConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (httpOutStream != null) {
				try {
					httpOutStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (httpInStream != null) {
				try {
					httpInStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
