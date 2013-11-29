package cn.airizu.domain.protocol.room_recommend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class RecommendParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("入参 netRespondString 为空! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, RecommendCityDatabaseFieldsConstant.RespondBean.data.name())) {
			throw new IllegalArgumentException("not find 'data' field ! ");
		}
		
		List<RecommendCity> recommendCityList = new ArrayList<RecommendCity>();
		
		JSONArray jsonArrayForRecommendCityList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RecommendCityDatabaseFieldsConstant.RespondBean.data.name());
		if (jsonArrayForRecommendCityList != null) {
			for (int i = 0; i < jsonArrayForRecommendCityList.length(); i++) {
				JSONObject jsonObjectForOneRecommendCity = (JSONObject) jsonArrayForRecommendCityList.get(i);
				
				if (JSONTools.isEmpty(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.cityId.name())) {
					throw new IllegalArgumentException("not find 'cityId' field ! ");
				}
				if (JSONTools.isEmpty(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.cityName.name())) {
					throw new IllegalArgumentException("not find 'cityName' field ! ");
				}
				String id = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.id.name(), "");
				String cityName = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.cityName.name(), "");
				String cityId = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.cityId.name(), "");
				String image = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.image.name(), "");
				String sort = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.sort.name(), "");
				String street1Name = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.street1Name.name(), "");
				String street1Id = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.street1Id.name(), "");
				String street2Name = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.street2Name.name(), "");
				String street2Id = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRecommendCity, RecommendCityDatabaseFieldsConstant.RespondBean.street2Id.name(), "");
				
				RecommendCity city = new RecommendCity(id, cityName, cityId, image, sort, street1Name, street1Id, street2Name, street2Id);
				recommendCityList.add(city);
			}
		}
		
		return new RecommendNetRespondBean(recommendCityList);
	}
}
