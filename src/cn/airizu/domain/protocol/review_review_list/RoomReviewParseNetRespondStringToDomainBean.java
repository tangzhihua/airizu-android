package cn.airizu.domain.protocol.review_review_list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class RoomReviewParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		final JSONObject jsonRootObject = new JSONObject(netRespondString);
		
		// 评论总数
		final int reviewCount = JSONTools.safeParseJSONObjectForValueIsInteger(jsonRootObject, RoomReviewDatabaseFieldsConstant.RespondBean.reviewCount.name(), 0);
		// 房间总的平均分
		final String avgScore = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomReviewDatabaseFieldsConstant.RespondBean.avgScore.name(), "0.0");
		// 当前房间的评论项
		final Map<String, String> reviewItemMap = new HashMap<String, String>(10);
		final JSONObject jsonObjectForItem = JSONTools.safeParseJSONObjectForValueIsJSONObject(jsonRootObject, RoomReviewDatabaseFieldsConstant.RespondBean.item.name());
		if (jsonObjectForItem != null) {
			final JSONArray jsonArrayForItemNames = jsonObjectForItem.names();
			if (jsonArrayForItemNames != null) {
				for (int i = 0; i < jsonArrayForItemNames.length(); i++) {
					final String key = jsonArrayForItemNames.getString(i);
					final String value = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForItem, key, "");
					reviewItemMap.put(key, value);
				}
			}
		}
		// 评论列表
		final List<RoomReview> roomReviewList = new ArrayList<RoomReview>();
		final JSONArray jsonArrayForReviewList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomReviewDatabaseFieldsConstant.RespondBean.reviews.name());
		if (jsonArrayForReviewList != null) {
			for (int i = 0; i < jsonArrayForReviewList.length(); i++) {
				final JSONObject jsonObjectForReview = (JSONObject) jsonArrayForReviewList.get(i);
				// 用户名
				final String userName = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForReview, RoomReviewDatabaseFieldsConstant.RespondBean.userName.name(), "");
				// 用户发表评论的时间
				final String userReviewTime = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForReview, RoomReviewDatabaseFieldsConstant.RespondBean.userReviewTime.name(), "");
				// 评论内容
				final String userReview = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForReview, RoomReviewDatabaseFieldsConstant.RespondBean.userReview.name(), "");
				// 房东回复评论的时间
				final String hostReviewTime = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForReview, RoomReviewDatabaseFieldsConstant.RespondBean.hostReviewTime.name(), "");
				// 房东回复的内容
				final String hostReview = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForReview, RoomReviewDatabaseFieldsConstant.RespondBean.hostReview.name(), "");
				
				final RoomReview roomReview = new RoomReview(userName, userReviewTime, userReview, hostReviewTime, hostReview);
				roomReviewList.add(roomReview);
			}
		}
		
		return new RoomReviewNetRespondBean(reviewCount, avgScore, reviewItemMap, roomReviewList);
	}
}
