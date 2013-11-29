package cn.airizu.domain.protocol.room_calendar;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class RoomCalendarParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, RoomCalendarDatabaseFieldsConstant.RespondBean.checkIn.name())) {
			throw new IllegalArgumentException("is not find 'checkIn' field ! ");
		}
		
		List<Integer> roomCalendarList = new ArrayList<Integer>();
		JSONArray jsonArrayForCheckinList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomCalendarDatabaseFieldsConstant.RespondBean.checkIn.name());
		if (jsonArrayForCheckinList != null) {
			for (int i = 0; i < jsonArrayForCheckinList.length(); i++) {
				
				Integer integer = new Integer(jsonArrayForCheckinList.getInt(i));
				
				roomCalendarList.add(integer);
			}
		}
		
		return new RoomCalendarNetRespondBean(roomCalendarList);
	}
	
}
