package cn.airizu.domain.nethelper.network_engine_for_http;

import java.util.Map;

import cn.airizu.domain.net_error_handle.NetErrorBean;

public interface IHttpNetworkEngine {
	public byte[] requestNetByHttp(final Map<String, String> httpRequestParameterMap, final NetErrorBean netErrorForOUT) throws Exception;
}
