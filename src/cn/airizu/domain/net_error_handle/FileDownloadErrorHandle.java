package cn.airizu.domain.net_error_handle;

public final class FileDownloadErrorHandle {
	private FileDownloadErrorHandle() {
		
	}
	
	public static String getErrorMessage(NetErrorTypeEnum errorType, int errorCode) {
		String errorMessage = "";
		
		switch (errorType)
		{
			// 网络访问成功
			case NET_ERROR_TYPE_SUCCESS: {
				errorMessage = "下载成功";
			}
			break;
			
			// 客户端 网络访问错误
			case NET_ERROR_TYPE_CLIENT_NET_ERROR: {
				switch (errorCode)
				{
					case FileDownloadErrorCode.SD_CARD_FREE_FILE_SPACE_NOT_ENOUGH: {
						errorMessage = "SD卡空间不足!";
					}
					break;
					
					default:
					break;
				}
			}
			break;
			
			// 服务器端 网络访问错误
			case NET_ERROR_TYPE_SERVER_NET_ERROR: {
				switch (errorCode)
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
			
			// 用户取消了本次联网请求
			case NET_ERROR_TYPE_THREAD_IS_CANCELED: {
				errorMessage = "当前网络请求被取消了";
			}
			break;
			
			default: {
				
			}
			break;
		}
		
		return errorMessage;
	}
}
