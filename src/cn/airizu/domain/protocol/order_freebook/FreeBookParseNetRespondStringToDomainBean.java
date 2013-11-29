package cn.airizu.domain.protocol.order_freebook;

import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class FreeBookParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据完整性检测
		if (JSONTools.isEmpty(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.isCheap.name())) {
			throw new IllegalArgumentException("is not find 'isCheap' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.availablePoint.name())) {
			throw new IllegalArgumentException("is not find 'availablePoint' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.advancedDeposit.name())) {
			throw new IllegalArgumentException("is not find 'advancedDeposit' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.underLinePaid.name())) {
			throw new IllegalArgumentException("is not find 'underLinePaid' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.totalPrice.name())) {
			throw new IllegalArgumentException("is not find 'totalPrice' field ! ");
		}
		
		int isCheapInt = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.isCheap.name(), 0);
		boolean isCheap = (isCheapInt == 1) ? true : false;
		int availablePoint = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.availablePoint.name(), 0);
		double advancedDeposit = JSONTools.safeParseJSONObjectForValueIsDouble(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.advancedDeposit.name(), 0.0);
		double underLinePaid = JSONTools.safeParseJSONObjectForValueIsDouble(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.underLinePaid.name(), 0.0);
		double totalPrice = JSONTools.safeParseJSONObjectForValueIsDouble(jsonRootObject, FreeBookDatabaseFieldsConstant.RespondBean.totalPrice.name(), 0.0);
		
		return new FreeBookNetRespondBean(totalPrice, advancedDeposit, underLinePaid, availablePoint, isCheap);
	}
}
