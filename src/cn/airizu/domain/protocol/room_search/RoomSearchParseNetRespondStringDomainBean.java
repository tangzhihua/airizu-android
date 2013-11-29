package cn.airizu.domain.protocol.room_search;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class RoomSearchParseNetRespondStringDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("入参 netRespondString 为空! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, RoomSearchDatabaseFieldsConstant.RespondBean.roomCount.name())
				|| JSONTools.isEmpty(jsonRootObject, RoomSearchDatabaseFieldsConstant.RespondBean.data.name())) {
			throw new IllegalArgumentException("网络返回的数据不完整!is not find 'roomCount' or 'data' ! ");
		}
		
		List<RoomInfo> roomInfoList = new ArrayList<RoomInfo>();
		String roomCount = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomSearchDatabaseFieldsConstant.RespondBean.roomCount.name(), "");
		
		JSONArray jsonArrayForRoomInfoList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomSearchDatabaseFieldsConstant.RespondBean.data.name());
		if (jsonArrayForRoomInfoList != null) {
			for (int i = 0; i < jsonArrayForRoomInfoList.length(); i++) {
				JSONObject jsonObjectForOneRoom = jsonArrayForRoomInfoList.getJSONObject(i);
				
				if (JSONTools.isEmpty(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.roomId.name())) {
					throw new IllegalArgumentException("not find 'roomId' field ! ");
				}
				
				String roomId = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.roomId.name(), "");
				String roomTitle = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.roomTitle.name(), "");
				String rentalWay = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.rentalWay.name(), "");
				String rentalWayName = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.rentalWayName.name(), "");
				String occupancyCount = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.occupancyCount.name(), "");
				String reviewCount = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.reviewCount.name(), "");
				String scheduled = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.scheduled.name(), "");
				String price = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.price.name(), "");
				String image = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.image.name(), "");
				String verify = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.verify.name(), "");
				String len = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.len.name(), "");
				String lat = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.lat.name(), "");
				String distance = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoom, RoomSearchDatabaseFieldsConstant.RespondBean.distance.name(), "");
				
				RoomInfo roomInfo = new RoomInfo(roomId, roomTitle, rentalWay, rentalWayName, occupancyCount, reviewCount, scheduled, price, image, verify, len, lat, distance);
				
				roomInfoList.add(roomInfo);
			}
		}
		
		return new RoomSearchNetRespondBean(roomCount, roomInfoList);
	}
	
}
