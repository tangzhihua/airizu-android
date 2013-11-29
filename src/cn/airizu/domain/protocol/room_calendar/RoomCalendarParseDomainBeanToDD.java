package cn.airizu.domain.protocol.room_calendar;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class RoomCalendarParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof RoomCalendarNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		RoomCalendarNetRequestBean requestBean = (RoomCalendarNetRequestBean) netRequestDomainBean;
		String roomId = requestBean.getRoomId();
		if (TextUtils.isEmpty(roomId)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(RoomCalendarDatabaseFieldsConstant.RequestBean.roomId.name(), roomId);
		
		return params;
	}
}
