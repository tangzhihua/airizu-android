package cn.airizu.toolutils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.json.JSONObject;
import android.text.TextUtils;

public abstract class HttpEntityDataProcessTools {
	/**
	 * 根据传入的 参数列表, 按照指定的编码方式, 拼接成 HTTP 请求的实体数据字符串(POST 和 GET不在这里做区分, 而是在外面设置)
	 * 
	 * @param params 业务协议参数
	 * @param enc 编码方式
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getHttpEntityDataStringForDefault(Map<String, String> params, String enc) throws UnsupportedEncodingException {
		// 如果入参为空, 就返回空字符串, 而不返回空指针.
		if (params.isEmpty() || TextUtils.isEmpty(enc)) {
			return "";
		}
		
		// 拼接请求参数, 构建HTTP POST请求发送的实体数据
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			
			/**
			 * 这里一定要考虑发往服务器端参数的编码问题, 否则中文就不能显示 中文显示乱码问题可能由两个原因照成 1.客户端:客户端需要转成 UTF-8格式 2.tomcat服务器默认的编码是ISO-8859-1, 所以在服务器要将客户端发送过来的数据转成UTF-8
			 */
			sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), enc)).append('&');
		}
		// 去掉结尾的'&'
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	public static String getHttpEntityDataStringForJSON(Map<String, String> params, String enc) throws UnsupportedEncodingException {
		// 如果入参为空, 就返回空字符串, 而不返回空指针.
		if (params.isEmpty() || TextUtils.isEmpty(enc)) {
			return "";
		}
		
		// 拼接请求参数, 构建HTTP POST请求发送的实体数据
		JSONObject jsonObject = new JSONObject(params);
		return jsonObject.toString();
	}
	
	public static String getHttpEntityDataStringForXML(Map<String, String> params, String enc) throws UnsupportedEncodingException {
		// 如果入参为空, 就返回空字符串, 而不返回空指针.
		if (params.isEmpty() || TextUtils.isEmpty(enc)) {
			return "";
		}
		
		// 拼接请求参数, 构建HTTP POST请求发送的实体数据
		return "";
	}
}
