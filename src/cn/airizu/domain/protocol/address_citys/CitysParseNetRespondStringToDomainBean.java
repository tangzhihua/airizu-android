package cn.airizu.domain.protocol.address_citys;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class CitysParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		if (JSONTools.isEmpty(jsonRootObject, CitysDatabaseFieldsConstant.RespondBean.citys.name())) {
			throw new IllegalArgumentException("is not find 'citys' field ! ");
		}
		
		List<CityInfo> cityInfoList = new ArrayList<CityInfo>();
		JSONArray jsonArrayForCityList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, CitysDatabaseFieldsConstant.RespondBean.citys.name());
		if (jsonArrayForCityList != null) {
			for (int i = 0; i < jsonArrayForCityList.length(); i++) {
				JSONObject jsonObjectForOneCity = (JSONObject) jsonArrayForCityList.get(i);
				
				String id = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.id.name(), "");
				String name = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.name.name(), "");
				String code = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.code.name(), "");
				String minLng = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.minLng.name(), "");
				String maxLng = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.maxLng.name(), "");
				String minLat = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.minLat.name(), "");
				String maxLat = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.maxLat.name(), "");
				String locationLat = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.locationLat.name(), "");
				String locationLng = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.locationLng.name(), "");
				String locationLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.locationLatBaidu.name(), "");
				String locationLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.locationLngBaidu.name(), "");
				String minLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.minLngBaidu.name(), "");
				String minLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.minLatBaidu.name(), "");
				String maxLatBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.maxLatBaidu.name(), "");
				String maxLngBaidu = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.maxLngBaidu.name(), "");
				String provinceId = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneCity, CitysDatabaseFieldsConstant.RespondBean.provinceId.name(), "");
				
				CityInfo cityInfo = new CityInfo(id, 
						                             name, 
						                             code, 
						                             minLng, 
						                             maxLng, 
						                             minLat, 
						                             maxLat, 
						                             locationLat, 
						                             locationLng, 
						                             locationLatBaidu, 
						                             locationLngBaidu, 
						                             minLngBaidu, 
						                             minLatBaidu, 
						                             maxLatBaidu,
						                             maxLngBaidu, 
						                             provinceId);
				cityInfoList.add(cityInfo);
			}
		}
		
		return new CitysNetRespondBean(cityInfoList);
	}
	
}
