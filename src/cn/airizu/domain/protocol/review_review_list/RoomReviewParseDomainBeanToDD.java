package cn.airizu.domain.protocol.review_review_list;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class RoomReviewParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		final boolean isRightObjectType = netRequestDomainBean instanceof RoomReviewNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		final RoomReviewNetRequestBean requestBean = (RoomReviewNetRequestBean) netRequestDomainBean;
		final int pageNum = requestBean.getPageNum();
		if (pageNum < 0) {
			throw new IllegalArgumentException("pageNum 参数 < 0 ");
		}
		final String roomId = requestBean.getRoomId();
		if (TextUtils.isEmpty(roomId)) {
			throw new IllegalArgumentException("roomId 参数为空 ");
		}
		
		final Map<String, String> params = new HashMap<String, String>();
		// 必须参数
		params.put(RoomReviewDatabaseFieldsConstant.RequestBean.pageNum.name(), Integer.toString(pageNum));
		params.put(RoomReviewDatabaseFieldsConstant.RequestBean.roomId.name(), roomId);
		
		return params;
	}
}
