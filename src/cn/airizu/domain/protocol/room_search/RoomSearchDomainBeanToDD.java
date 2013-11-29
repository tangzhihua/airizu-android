package cn.airizu.domain.protocol.room_search;

import java.util.HashMap;
import java.util.Map;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseDomainBeanToDataDictionary;

public class RoomSearchDomainBeanToDD implements IParseDomainBeanToDataDictionary {
	
	@Override
	public Map<String, String> parseDomainBeanToDataDictionary(Object netRequestDomainBean) {
		if (null == netRequestDomainBean) {
			throw new IllegalArgumentException("netRequestDomainBean is null!");
		}
		
		boolean isRightObjectType = netRequestDomainBean instanceof RoomSearchNetRequestBean;
		if (!isRightObjectType) {
			throw new IllegalArgumentException("传入的业务Bean的类型不符 !");
		}
		
		RoomSearchNetRequestBean roomSearchNetRequestBean = (RoomSearchNetRequestBean) netRequestDomainBean;
		
		Map<String, String> params = new HashMap<String, String>();
		
		String value = "";
		
		value = roomSearchNetRequestBean.getCityId();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.cityId.name(), value);
		}
		value = roomSearchNetRequestBean.getCityName();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.cityName.name(), value);
		}
		value = roomSearchNetRequestBean.getStreetName();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.streetName.name(), value);
		}
		value = roomSearchNetRequestBean.getCheckinDate();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.checkinDate.name(), value);
		}
		value = roomSearchNetRequestBean.getCheckoutDate();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.checkoutDate.name(), value);
		}
		value = roomSearchNetRequestBean.getOccupancyCount();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.occupancyCount.name(), value);
		}
		value = roomSearchNetRequestBean.getRoomNumber();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.roomNumber.name(), value);
		}
		value = roomSearchNetRequestBean.getOffset();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.offset.name(), value);
		}
		value = roomSearchNetRequestBean.getMax();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.max.name(), value);
		}
		value = roomSearchNetRequestBean.getPriceDifference();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.priceDifference.name(), value);
		}
		value = roomSearchNetRequestBean.getDistrictName();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.districtName.name(), value);
		}
		value = roomSearchNetRequestBean.getDistrictId();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.districtId.name(), value);
		}
		value = roomSearchNetRequestBean.getRoomType();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.roomType.name(), value);
		}
		value = roomSearchNetRequestBean.getOrder();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.order.name(), value);
		}
		value = roomSearchNetRequestBean.getRentType();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.rentType.name(), value);
		}
		value = roomSearchNetRequestBean.getTamenities();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.tamenities.name(), value);
		}
		value = roomSearchNetRequestBean.getDistance();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.distance.name(), value);
		}
		value = roomSearchNetRequestBean.getLocationLat();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.locationLat.name(), value);
		}
		value = roomSearchNetRequestBean.getLocationLng();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.locationLng.name(), value);
		}
		value = roomSearchNetRequestBean.getNearby();
		if (!TextUtils.isEmpty(value)) {
			params.put(RoomSearchDatabaseFieldsConstant.RequestBean.nearby.name(), value);
		}
		
		return params;
	}
}
