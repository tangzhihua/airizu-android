package cn.airizu.domain.protocol.order_submit;

import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class OrderSubmitParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据完整性检测
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.orderId.name())) {
			throw new IllegalArgumentException("is not find 'orderId' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.pricePerNight.name())) {
			throw new IllegalArgumentException("is not find 'pricePerNight' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.linePay.name())) {
			throw new IllegalArgumentException("is not find 'linePay' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.subPrice.name())) {
			throw new IllegalArgumentException("is not find 'subPrice' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.number.name())) {
			throw new IllegalArgumentException("is not find 'number' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.conversionState.name())) {
			throw new IllegalArgumentException("is not find 'conversionState' field ! ");
		}
		
		String orderId = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.orderId.name(), "");
		String orderState = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.orderState.name(), "");
		String message = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.message.name(), "");
		String chenckInDate = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.chenckInDate.name(), "");
		String chenckOutDate = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.chenckOutDate.name(), "");
		String guestNum = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.guestNum.name(), "");
		String pricePerNight = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.pricePerNight.name(), "");
		String linePay = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.linePay.name(), "");
		String subPrice = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.subPrice.name(), "");
		String orderType = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.orderType.name(), "");
		String statusContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.statusContent.name(), "");
		
		String number = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.number.name(), "");
		String image = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.image.name(), "");
		String title = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.title.name(), "");
		String address = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.address.name(), "");
		
		int conversionState = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, OrderSubmitDatabaseFieldsConstant.RespondBean.conversionState.name(), -1);
		
		return new OrderSubmitNetRespondBean(orderId, 
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
				                                 conversionState);
	}
}
