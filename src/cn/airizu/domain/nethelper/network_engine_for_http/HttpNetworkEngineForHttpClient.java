package cn.airizu.domain.nethelper.network_engine_for_http;

import java.util.Map;
import cn.airizu.domain.net_error_handle.NetErrorBean;

public class HttpNetworkEngineForHttpClient implements IHttpNetworkEngine {
	
	@Override
	public byte[] requestNetByHttp(Map<String, String> httpRequestParameterMap, NetErrorBean netErrorForOUT) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
//	if (TextUtils.isEmpty(url) || TextUtils.isEmpty(requestMethod) || null == netError) {
//		throw new NullPointerException("method parameter : url or requestMethod is empty !");
//	}
//	
//	InputStream httpInStream = null;
//	byte[] netRawData = null;
//	HttpParams params = new BasicHttpParams();
//	
//	try {
//		if (netConnectionUtils.isWapNetwork()) {
//			// 如果使用wap 网络，需要设置代理，没有的话直连.
//			HttpHost proxy = new HttpHost(netConnectionUtils.getProxy(), Integer.valueOf(netConnectionUtils.getProxyPort()));
//			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//		}
//		
//		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
//		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
//		HttpConnectionParams.setSocketBufferSize(params, 8192);
//		
//		DefaultHttpClient httpClient = new DefaultHttpClient(params);
//		
//		HttpResponse response = null;
//		if (requestMethod.equals("POST")) {
//			HttpPost post = new HttpPost();
//			// 构造字符串数据
//			StringEntity reqEntity = new StringEntity(entityData);
//			// 设置类型(必须设置类型, 否则后台解析不出来客户端传递的数据)
//			/*
//			 * 关于application/x-www-form- urlencoded等字符编码的解释说明 在Form元素的语法中，EncType表明提交数据的格式 用 Enctype 属性指定将数据回发到服务器时浏览器使用的编码类型。 下边是说明： application/x-www-form-urlencoded： 窗体数据被编码为名称/值对。这是标准的编码格式。
//			 * multipart/form-data： 窗体数据被编码为一条消息，页上的每个控件对应消息中的一个部分。 text/plain： 窗体数据以纯文本形式进行编码，其中不含任何控件或格式字符。 补充 form的enctype属性为编码方式，常用有两种：application /x- www-form-urlencoded和multipart/form-data ，
//			 * 默认为application/x-www-form-urlencoded。 当action为get时候， 浏览器用x-www-form- urlencoded的编码方式把form数据转换成一个字串 （name1=value1&name2=value2...）， 然后把这个字串append到url后面，用?分割，加载这个新的url。
//			 * 当action为post时候，浏览器把form数据封装到http body中， 然后发送到server。 如果没有type=file的控件，用默认的application /x-www-form-urlencoded就可以了。 但是如果有type=file的话 ，就要用到multipart/form-data了 。浏览器会把整个表单以控件为单位分割，
//			 * 并为每个部分加上Content-Disposition (form-data或者file ),Content-Type(默认为text/plain ),name(控件name)等信息， 并加上分割符(boundary)。
//			 */
//			reqEntity.setContentType("application/x-www-form-urlencoded");
//			post.setEntity(reqEntity);
//			post.setURI(new URI(url));
//			response = httpClient.execute(post);
//		} else {
//			HttpGet get = new HttpGet(url);
//			response = httpClient.execute(get);
//		}
//		
//		netError.setErrorCode(response.getStatusLine().getStatusCode());
//		netError.setErrorMessage(response.getStatusLine().getReasonPhrase());
//		
//		if (netError.getErrorCode() == 200) {
//			httpInStream = response.getEntity().getContent();
//			
//			// 从流中把数据读取出来
//			netRawData = StreamTools.readInputStream(httpInStream);
//		} else {
//			// 客户端网络访问失败
//			netError.setErrorType(NetErrorType.NET_ERROR_TYPE_CLIENT_NET_ERROR);
//		}
//		
//		return netRawData;
//		
//	} catch (Exception e) {
//		throw e;
//	} finally {
//		if (httpInStream != null) {
//			httpInStream.close();
//		}
//	}
}
