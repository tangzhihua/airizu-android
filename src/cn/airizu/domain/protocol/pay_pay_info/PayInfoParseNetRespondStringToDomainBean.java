package cn.airizu.domain.protocol.pay_pay_info;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class PayInfoParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		
		String payInfo = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, PayInfoDatabaseFieldsConstant.RespondBean.payInfo.name(), "");
		return new PayInfoNetRespondBean(payInfo);
	}
}
