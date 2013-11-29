package cn.airizu.domain.protocol.account_help;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class HelpParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据完整性检测
		if (JSONTools.isEmpty(jsonRootObject, HelpDatabaseFieldsConstant.RespondBean.data.name())) {
			throw new IllegalArgumentException("is not find 'data' field ! ");
		}
		
		List<HelpInfo> helpInfoList = new ArrayList<HelpInfo>();
		JSONArray jsonArrayForData = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, HelpDatabaseFieldsConstant.RespondBean.data.name());
		if (jsonArrayForData != null) {
			for (int i = 0; i < jsonArrayForData.length(); i++) {
				JSONObject respondJSON = (JSONObject) jsonArrayForData.get(i);
				
				String type = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, HelpDatabaseFieldsConstant.RespondBean.type.name(), "");
				String title = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, HelpDatabaseFieldsConstant.RespondBean.title.name(), "");
				String content = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, HelpDatabaseFieldsConstant.RespondBean.content.name(), "");
				
				HelpInfo helpInfo = new HelpInfo(type, title, content);
				helpInfoList.add(helpInfo);
			}
		}
		
		return new HelpNetRespondBean(helpInfoList);
	}
	
}
