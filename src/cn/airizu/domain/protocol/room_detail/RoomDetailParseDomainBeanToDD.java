package cn.airizu.domain.protocol.room_detail;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class RoomDetailParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof RoomDetailNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		RoomDetailNetRequestBean requestBean = (RoomDetailNetRequestBean) netRequestDomainBean;
		String roomId = requestBean.getRoomId();
		if (TextUtils.isEmpty(roomId)) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(RoomDetailDatabaseFieldsConstant.RequestBean.roomId.name(), roomId);
		return params;
	}
}
