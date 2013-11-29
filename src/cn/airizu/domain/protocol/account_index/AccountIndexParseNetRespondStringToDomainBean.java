package cn.airizu.domain.protocol.account_index;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class AccountIndexParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		int totalPoint = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.totalPoint.name(), 0);
		String phoneNumber = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.phoneNumber.name(), "");
		int waitConfirmCount = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.waitConfirmCount.name(), 0);
		int waitPayCount = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.waitPayCount.name(), 0);
		int waitLiveCount = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.waitLiveCount.name(), 0);
		int waitReviewCount = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.waitReviewCount.name(), 0);
		String userImageURL = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.userImageURL.name(), "");
		String sex = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.sex.name(), "");
		String email = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.email.name(), "");
		String userName = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.userName.name(), "");
		boolean validatePhone = JSONTools.safeParseJSONObjectForValueIsBoolean(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.validatePhone.name(), false);
		boolean validateEmail = JSONTools.safeParseJSONObjectForValueIsBoolean(jsonRootObject, AccountIndexDatabaseFieldsConstant.RespondBean.validateEmail.name(), false);
		
		return new AccountIndexNetRespondBean(totalPoint, 
				                                  phoneNumber, 
				                                  waitConfirmCount, 
				                                  waitPayCount, 
				                                  waitLiveCount, 
				                                  waitReviewCount, 
				                                  userName, 
				                                  userImageURL, 
				                                  sex, 
				                                  email, 
				                                  validatePhone, 
				                                  validateEmail);
	}
}
