package cn.airizu.domain.protocol.order_detail;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class OrderDetailParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据丢失测试
		if (JSONTools.isEmpty(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.orderId.name())) {
			throw new IllegalArgumentException("is not find 'orderId' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.conversionState.name())) {
			throw new IllegalArgumentException("is not find 'conversionState' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.number.name())) {
			throw new IllegalArgumentException("is not find 'number' field ! ");
		}
		
		String orderId = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.orderId.name(), "");
		String orderState = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.orderState.name(), "");
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.message.name(), "");
		String chenckInDate = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.chenckInDate.name(), "");
		String chenckOutDate = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.chenckOutDate.name(), "");
		String guestNum = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.guestNum.name(), "");
		String pricePerNight = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.pricePerNight.name(), "");
		String linePay = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.linePay.name(), "");
		String subPrice = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.subPrice.name(), "");
		String orderType = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.orderType.name(), "");
		String statusContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.statusContent.name(), "");
		
		String number = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.number.name(), "");
		String image = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.image.name(), "");
		String title = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.title.name(), "");
		String address = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.address.name(), "");
		
		boolean ifShowHost = JSONTools.safeParseJSONObjectForValueIsBoolean(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.ifShowHost.name(), false);
		String hostName = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.hostName.name(), "");
		String hostPhone = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.hostPhone.name(), "");
		String hostBackupPhone = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.hostBackupPhone.name(), "");
		int conversionState = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, OrderDetailDatabaseFieldsConstant.RespondBean.conversionState.name(), -1);
		
		return new OrderDetailNetRespondBean(orderId, 
				                                 orderState, 
				                                 message, 
				                                 chenckInDate, 
				                                 chenckOutDate, 
				                                 guestNum, 
				                                 pricePerNight, 
				                                 linePay, 
				                                 subPrice, 
				                                 orderType, 
				                                 statusContent, 
				                                 number, 
				                                 image, 
				                                 title, 
				                                 address, 
				                                 ifShowHost, 
				                                 hostName, 
				                                 hostPhone, 
				                                 hostBackupPhone, 
				                                 conversionState);
	}
}
