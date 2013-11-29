package cn.airizu.domain.protocol.review_item;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class ReviewItemParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		
		if (JSONTools.isEmpty(jsonRootObject, ReviewItemDatabaseFieldsConstant.RespondBean.item.name())) {
			throw new IllegalArgumentException("is not find 'item' field ! ");
		}
		
		Map<String, String> itemMap = new HashMap<String, String>(10);
		JSONObject jsonObjectForItem = JSONTools.safeParseJSONObjectForValueIsJSONObject(jsonRootObject, ReviewItemDatabaseFieldsConstant.RespondBean.item.name());
		if (jsonObjectForItem != null) {
			JSONArray jsonArrayForItemNames = jsonObjectForItem.names();
			if (jsonArrayForItemNames != null) {
				for (int i = 0; i < jsonArrayForItemNames.length(); i++) {
					String key = jsonArrayForItemNames.getString(i);
					String value = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForItem, key, "");
					itemMap.put(key, value);
				}
			}
		}
		
		return new ReviewItemNetRespondBean(itemMap);
	}
}
