package cn.airizu.domain.net_entitydata_tools.airizu;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import cn.airizu.domain.net_entitydata_tools.IServerRespondDataTest;
import cn.airizu.domain.net_error_handle.NetErrorBean;
import cn.airizu.domain.net_error_handle.NetErrorTypeEnum;

public final class ServerRespondDataTestAirizu implements IServerRespondDataTest {
	
	@Override
	public NetErrorBean testServerRespondDataError(final String netUnpackedData) throws Exception {
		
		int errorCode = HttpStatus.SC_OK;
		NetErrorTypeEnum errorType = NetErrorTypeEnum.NET_ERROR_TYPE_SUCCESS;
		String errorMessage = "OK";
		
		JSONObject respondJSON = new JSONObject(netUnpackedData);
		if (respondJSON.has("errorcode")) {
			errorCode = Integer.parseInt(respondJSON.getString("errorcode"));
			if (respondJSON.has("errordes")) {
				errorMessage = respondJSON.getString("errordes");
			}
			
			// 服务器端返回了错误码
			errorType = NetErrorTypeEnum.NET_ERROR_TYPE_SERVER_NET_ERROR;
		}
		
		NetErrorBean netError = new NetErrorBean();
		netError.setErrorCode(errorCode);
		netError.setErrorType(errorType);
		netError.setErrorMessage(errorMessage);
		return netError;
	}
	
}
