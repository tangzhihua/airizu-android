package cn.airizu.domain.protocol.account_login;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class LogonParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据完整性检测
		if (JSONTools.isEmpty(jsonRootObject, LogonDatabaseFieldsConstant.RespondBean.JESSIONID.name())) {
			throw new IllegalArgumentException("is not find 'JESSIONID' field ! ");
		}
		
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, LogonDatabaseFieldsConstant.RespondBean.message.name(), "");
		String userId = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, LogonDatabaseFieldsConstant.RespondBean.userId.name(), "");
		String userName = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, LogonDatabaseFieldsConstant.RespondBean.userName.name(), "");
		String sessionId = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, LogonDatabaseFieldsConstant.RespondBean.JESSIONID.name(), "");
		
		return new LogonNetRespondBean(message, userId, userName, sessionId);
	}
}
