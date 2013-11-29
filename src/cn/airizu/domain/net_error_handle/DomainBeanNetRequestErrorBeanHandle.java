package cn.airizu.domain.net_error_handle;

/**
 * @author zhihua.tang
 */
public final class DomainBeanNetRequestErrorBeanHandle {
	private DomainBeanNetRequestErrorBeanHandle() {
		
	}
	
	public static void handleNetRequestBean(final NetErrorBean netErrorBean) {
		if (netErrorBean == null) {
			return;
		}
		
		String errorMessage = "";
		
		switch (netErrorBean.getErrorType())
		{
			// 网络访问成功
			case NET_ERROR_TYPE_SUCCESS: {
				errorMessage = "访问成功";
			}
			break;
			
			// 客户端 网络访问错误
			case NET_ERROR_TYPE_CLIENT_NET_ERROR: {
				switch (netErrorBean.getErrorCode())
				{
					case 404:

					break;
					
					default:
					break;
				}
				errorMessage = netErrorBean.getErrorMessage();
			}
			break;
			
			// 服务器端 网络访问错误
			case NET_ERROR_TYPE_SERVER_NET_ERROR: {
				switch (netErrorBean.getErrorCode())
				{
					case 1000:
						// errorMessage = "操作失败";
					break;
					case 2000:
						// errorMessage = "处理异常";
					break;
					case 3000:
						// errorMessage = "无结果返回";
					break;
					case 4000:
						// errorMessage = "需要登录";
					break;
					
					default:
					break;
				}
				
				errorMessage = netErrorBean.getErrorMessage();
			}
			break;
			
			// 客户端发生了异常
			case NET_ERROR_TYPE_CLIENT_EXCEPTION: {
				errorMessage = "客户端发生了异常";
			}
			break;
			
			// 网络未连接
			case NET_ERROR_TYPE_NET_UNAVAILABLE: {
				errorMessage = "当前网络不可用";
			}
			break;
			default: {
				
			}
			break;
		}
		
		netErrorBean.setErrorMessage(errorMessage);
	}
}