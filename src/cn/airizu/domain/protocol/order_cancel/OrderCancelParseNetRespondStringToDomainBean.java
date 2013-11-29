package cn.airizu.domain.protocol.order_cancel;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class OrderCancelParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderCancelDatabaseFieldsConstant.RespondBean.message.name(), "");
		return new OrderCancelNetRespondBean(message);
	}
}
