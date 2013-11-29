package cn.airizu.domain.protocol.review_submit;

import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class ReviewSubmitParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, ReviewSubmitDatabaseFieldsConstant.RespondBean.message.name(), "");
		
		return new ReviewSubmitNetRespondBean(message);
	}
}
