package cn.airizu.domain.protocol.order_list;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class OrderOverviewListParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, OrderOverviewDatabaseFieldsConstant.RespondBean.data.name())) {
			throw new IllegalArgumentException("is not find 'districts' field ! ");
		}
		
		List<OrderOverview> orderOverviewList = new ArrayList<OrderOverview>();
		JSONArray jsonArrayForData = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, OrderOverviewDatabaseFieldsConstant.RespondBean.data.name());
		if (jsonArrayForData != null) {
			for (int i = 0; i < jsonArrayForData.length(); i++) {
				JSONObject jsonObjectForOneOrderOverview = jsonArrayForData.getJSONObject(i);
				
				// 关键数据丢失检测
				if (JSONTools.isEmpty(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.orderId.name())) {
					throw new IllegalArgumentException("is not find 'orderId' field ! ");
				}
				
				String roomTitle = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.roomTitle.name(), "");
				String checkInDate = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.checkInDate.name(), "");
				String checkOutDate = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.checkOutDate.name(), "");
				String statusCode = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.statusCode.name(), "");
				String orderTotalPrice = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.orderTotalPrice.name(), "");
				String roomImage = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.roomImage.name(), "");
				String orderId = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneOrderOverview, OrderOverviewDatabaseFieldsConstant.RespondBean.orderId.name(), "");
				
				OrderOverview orderOverview = new OrderOverview(roomTitle, checkInDate, checkOutDate, statusCode, orderTotalPrice, roomImage, orderId);
				orderOverviewList.add(orderOverview);
			}
		}
		
		return new OrderOverviewListNetRespondBean(orderOverviewList);
	}
	
}
