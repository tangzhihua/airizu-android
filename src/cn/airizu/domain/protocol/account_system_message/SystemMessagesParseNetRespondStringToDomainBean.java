package cn.airizu.domain.protocol.account_system_message;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class SystemMessagesParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, SystemMessageDatabaseFieldsConstant.RespondBean.data.name())) {
			throw new IllegalArgumentException("is not find 'data' field ! ");
		}
		
		List<SystemMessage> systemMessageList = new ArrayList<SystemMessage>();
		JSONArray jsonArrayForData = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, SystemMessageDatabaseFieldsConstant.RespondBean.data.name());
		if (jsonArrayForData != null) {
			for (int i = 0; i < jsonArrayForData.length(); i++) {
				JSONObject jsonObjectForOneSystemMessage = (JSONObject) jsonArrayForData.get(i);
				
				String date = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneSystemMessage, SystemMessageDatabaseFieldsConstant.RespondBean.date.name(), "");
				String message = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneSystemMessage, SystemMessageDatabaseFieldsConstant.RespondBean.message.name(), "");
				
				SystemMessage systemMessage = new SystemMessage(date, message);
				systemMessageList.add(systemMessage);
			}
		}
		
		return new SystemMessagesNetRespondBean(systemMessageList);
	}
}
