package cn.airizu.domain.protocol.account_update;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class UpdateAccountInfoParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is null!");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, UpdateAccountInfoDatabaseFieldsConstant.RespondBean.message.name(), "");
		
		return new UpdateAccountInfoNetRespondBean(message);
	}
	
}
