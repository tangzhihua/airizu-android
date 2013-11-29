package cn.airizu.domain.protocol.account_register;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class RegisterParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is null!");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RegisterDatabaseFieldsConstant.RespondBean.message.name(), "");
		
		return new RegisterNetRespondBean(message);
	}
}
