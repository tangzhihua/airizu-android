package cn.airizu.domain.protocol.account_forget_password;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class ForgetPasswordParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty!");
		}
		
		JSONObject respondJSON = new JSONObject(netRespondString);
		String message = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, ForgetPasswordDatabaseFieldsConstant.RespondBean.message.name(), "");
		
		return new ForgetPasswordNetRespondBean(message);
	}
}
