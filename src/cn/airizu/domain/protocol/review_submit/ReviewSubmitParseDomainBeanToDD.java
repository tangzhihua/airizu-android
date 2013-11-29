package cn.airizu.domain.protocol.review_submit;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public final class ReviewSubmitParseDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof ReviewSubmitNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		ReviewSubmitNetRequestBean requestBean = (ReviewSubmitNetRequestBean) netRequestDomainBean;
		String orderId = requestBean.getOrderId();
		String reviewContent = requestBean.getReviewContent();
		if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(reviewContent) || requestBean.getReviewItemScoreList() == null || requestBean.getReviewItemScoreList().size() <= 0) {
			throw new IllegalArgumentException("必须的数据字段不完整 ! ");
		}
		
		Map<String, String> params = new HashMap<String, String>();
		
		// 必选项
		params.put(ReviewSubmitDatabaseFieldsConstant.RequestBean.orderId.name(), orderId);
		params.put(ReviewSubmitDatabaseFieldsConstant.RequestBean.reviewContent.name(), reviewContent);
		
		Map<String, String> reviewItemScoreList = requestBean.getReviewItemScoreList();
		Set<Entry<String, String>> entries = reviewItemScoreList.entrySet();
		for (Entry<String, String> entry : entries) {
			params.put(ReviewSubmitDatabaseFieldsConstant.RequestBean.score_.name() + entry.getKey(), entry.getValue());
		}
		
		return params;
	}
}
