package cn.airizu.domain.net_error_handle;

import java.io.Serializable;

import org.apache.http.HttpStatus;

@SuppressWarnings("serial")
/**
 * 网络访问过程中出现错误时的数据Bean
 */
public final class NetErrorBean implements Serializable {
	// 错误类型
	private NetErrorTypeEnum errorType = NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS;
	// 错误代码
	private int errorCode = HttpStatus.SC_OK;
	// 错误描述信息
	private String errorMessage = "OK";
	
	public NetErrorBean() {
	}
	
	public void reinitialize(final NetErrorBean srcObject) {
		if (srcObject != null) {
			this.errorCode = srcObject.errorCode;
			this.errorMessage = srcObject.errorMessage;
			this.errorType = srcObject.errorType;
		} else {
			this.errorCode = HttpStatus.SC_OK;
			this.errorMessage = "OK";
			this.errorType = NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS;
		}
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public NetErrorTypeEnum getErrorType() {
		return errorType;
	}
	
	public void setErrorType(NetErrorTypeEnum errorType) {
		this.errorType = errorType;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "NetErrorBean [errorType=" + errorType + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}
	
}
