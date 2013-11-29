package cn.airizu.domain.protocol.address_districts;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class DistrictsParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, DistrictsDatabaseFieldsConstant.RespondBean.districts.name())) {
			throw new IllegalArgumentException("is not find 'districts' field ! ");
		}
		
		List<DistrictInfo> districtInfoList = new ArrayList<DistrictInfo>();
		JSONArray jsonArrayForDistrictList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, DistrictsDatabaseFieldsConstant.RespondBean.districts.name());
		if (jsonArrayForDistrictList != null) {
			for (int i = 0; i < jsonArrayForDistrictList.length(); i++) {
				JSONObject respondJSON = (JSONObject) jsonArrayForDistrictList.get(i);
				
				String id = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.id.name(), "");
				String name = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.name.name(), "");
				String code = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.code.name(), "");
				String minLng = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.minLng.name(), "");
				String maxLng = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.maxLng.name(), "");
				String minLat = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.minLat.name(), "");
				String maxLat = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.maxLat.name(), "");
				String locationLat = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.locationLat.name(), "");
				String locationLng = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.locationLng.name(), "");
				String hot = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.hot.name(), "");
				String sort = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.sort.name(), "");
				String locationLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.locationLatBaidu.name(), "");
				String locationLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.locationLngBaidu.name(), "");
				String minLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.minLngBaidu.name(), "");
				String minLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.minLatBaidu.name(), "");
				String maxLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.maxLatBaidu.name(), "");
				String maxLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.maxLngBaidu.name(), "");
				String cityId = JSONTools.safeParseJSONObjectForValueIsString(respondJSON, DistrictsDatabaseFieldsConstant.RespondBean.cityId.name(), "");
				
				DistrictInfo districtInfo = new DistrictInfo(id, 
						                                         name, 
						                                         code, 
						                                         minLng, 
						                                         maxLng, 
						                                         minLat, 
						                                         maxLat, 
						                                         locationLat, 
						                                         locationLng, 
						                                         hot, 
						                                         sort, 
						                                         locationLatBaidu, 
						                                         locationLngBaidu, 
						                                         minLngBaidu,
						                                         minLatBaidu, 
						                                         maxLatBaidu, 
						                                         maxLngBaidu, 
						                                         cityId);
				districtInfoList.add(districtInfo);
			}
		}
		
		return new DistrictsNetRespondBean(districtInfoList);
	}
	
}
