package cn.airizu.domain.protocol.room_dictionary;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class DictionaryParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("入参 netRespondString 为空! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据完整性检测
		if (   JSONTools.isEmpty(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.roomType.name())
				|| JSONTools.isEmpty(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.rentManner.name())
				|| JSONTools.isEmpty(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.equipment.name())) {
			throw new IllegalArgumentException("网络返回的数据不完整!is not find roomType/equipment/rentManner ! ");
		}
		
		// 房间类型
		List<RootType> rootTypeList = new ArrayList<RootType>();
		JSONArray jsonArrayForRoomTypeList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.roomType.name());
		if (jsonArrayForRoomTypeList != null) {
			for (int i = 0; i < jsonArrayForRoomTypeList.length(); i++) {
				JSONObject jsonObjectForOneRoomType = jsonArrayForRoomTypeList.getJSONObject(i);
				
				String nameString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoomType, DictionaryDatabaseFieldsConstant.RespondBean.typeName.name(), "");
				String idString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneRoomType, DictionaryDatabaseFieldsConstant.RespondBean.typeId.name(), "");
				
				RootType rootType = new RootType(nameString, idString);
				rootTypeList.add(rootType);
			}
		}
		
		// 获取出租方式
		List<RentManner> rentMannerList = new ArrayList<RentManner>();
		JSONArray jsonArrayForRentMannerList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.rentManner.name());
		if (jsonArrayForRentMannerList != null) {
			for (int i = 0; i < jsonArrayForRentMannerList.length(); i++) {
				JSONObject jsonObjectForOneManner = jsonArrayForRentMannerList.getJSONObject(i);
				
				String nameString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneManner, DictionaryDatabaseFieldsConstant.RespondBean.rentalWayName.name(), "");
				String idString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneManner, DictionaryDatabaseFieldsConstant.RespondBean.rentalWayId.name(), "");
				
				RentManner rentManner = new RentManner(nameString, idString);
				rentMannerList.add(rentManner);
			}
		}
		
		// 获取设施设备
		List<Equipment> equipmentList = new ArrayList<Equipment>();
		JSONArray jsonArrayForEquipmentList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, DictionaryDatabaseFieldsConstant.RespondBean.equipment.name());
		if (jsonArrayForEquipmentList != null) {
			for (int i = 0; i < jsonArrayForEquipmentList.length(); i++) {
				JSONObject jsonObjectForOneEquipment = jsonArrayForEquipmentList.getJSONObject(i);
				
				String nameString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneEquipment, DictionaryDatabaseFieldsConstant.RespondBean.equipmentName.name(), "");
				String idString = JSONTools.safeParseJSONObjectForValueIsString(jsonObjectForOneEquipment, DictionaryDatabaseFieldsConstant.RespondBean.equipmentId.name(), "");
				
				Equipment equipment = new Equipment(nameString, idString);
				equipmentList.add(equipment);
			}
		}
		
		return new DictionaryNetRespondBean(rootTypeList, rentMannerList, equipmentList);
	}
}
