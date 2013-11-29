package cn.airizu.domain.net_error_handle;

public enum NetErrorTypeEnum {
	
	/**
	 * 网络访问成功
	 */
	NET_ERROR_TYPE_SUCCESS,

	/**
	 * 客户端 网络访问错误
	 */
	NET_ERROR_TYPE_CLIENT_NET_ERROR,
	/**
	 * 服务器端 网络访问错误
	 */
	NET_ERROR_TYPE_SERVER_NET_ERROR,
	/**
	 * 客户端发生了异常
	 */
	NET_ERROR_TYPE_CLIENT_EXCEPTION,
	/**
	 * 网络线程被取消
	 */
	NET_ERROR_TYPE_THREAD_IS_CANCELED,
	/**
	 * 网络未连接
	 */
	NET_ERROR_TYPE_NET_UNAVAILABLE;
}
