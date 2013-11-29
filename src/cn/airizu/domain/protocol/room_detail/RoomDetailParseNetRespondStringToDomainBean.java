package cn.airizu.domain.protocol.room_detail;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public class RoomDetailParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		final JSONObject jsonRootObject = new JSONObject(netRespondString);
		
		// 大图地址列表
		final List<String> imageMList = new ArrayList<String>();
		final JSONArray jsonArrayForImageMList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.imageM.name());
		if (jsonArrayForImageMList != null) {
			for (int i = 0; i < jsonArrayForImageMList.length(); i++) {
				imageMList.add(jsonArrayForImageMList.getString(i));
			}
		}
		
		// 用户编号
		final String userId = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.userId.name(), "");
		
		// 建筑面积
		final String size = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.size.name(), "");
		
		// 缩略图地址列表
		final List<String> imageSList = new ArrayList<String>();
		final JSONArray jsonArrayForImageSList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.imageS.name());
		if (jsonArrayForImageSList != null) {
			for (int i = 0; i < jsonArrayForImageSList.length(); i++) {
				imageSList.add(jsonArrayForImageSList.getString(i));
			}
		}
		
		// 房间默认图片
		final String image = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.image.name(), "");
		// 每晚价钱
		final String price = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.price.name(), "");
		// 房间标题
		final String title = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.title.name(), "");
		// 房间地址
		final String address = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.address.name(), "");
		// 百度经度
		final String len = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.len.name(), "");
		// 百度纬度
		final String lat = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.lat.name(), "");
		// 房间编号
		final String number = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.number.name(), "");
		// 曾被预定
		final String scheduled = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.scheduled.name(), "");
		// 卧室数
		final String bedRoom = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.bedRoom.name(), "");
		// 规则内容
		final String ruleContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.ruleContent.name(), "");
		// 清洁服务类型
		final String clean = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.clean.name(), "");
		// 房间概括
		final String description = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.description.name(), "");
		// 可入住人数
		final String accommodates = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.accommodates.name(), "");
		// 使用规则
		final String roomRule = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.roomRule.name(), "");
		// 卫浴类型
		final String restRoom = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.restRoom.name(), "");
		// 是否提供发票1为是,2为否
		final String tickets = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.tickets.name(), "");
		// 退订条款
		final String cancellation = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.cancellation.name(), "");
		// 最少天数
		final String minNights = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.minNights.name(), "");
		// 租住方式
		final String privacy = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.privacy.name(), "");
		// 退房时间
		final String checkOutTime = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.checkOutTime.name(), "");
		// 最多天数
		final String maxNights = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.maxNights.name(), "");
		// 床数
		final String beds = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.beds.name(), "");
		// 房屋类型
		final String propertyType = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.propertyType.name(), "");
		// 床型
		final String bedType = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.bedType.name(), "");
		// 卫生间数
		final String bathRoomNum = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.bathRoomNum.name(), "");
		// 配套设施列表
		final List<String> equipmentList = new ArrayList<String>();
		final JSONArray jsonArrayForEquipmentList = JSONTools.safeParseJSONObjectForValueIsJSONArray(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.equipmentList.name());
		if (jsonArrayForEquipmentList != null) {
			for (int i = 0; i < jsonArrayForEquipmentList.length(); i++) {
				equipmentList.add(jsonArrayForEquipmentList.getString(i));
			}
		}
		// 租客点评总分
		final String review = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.review.name(), "");
		// 租客点评总条数
		final String reviewCount = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.reviewCount.name(), "");
		// 租客点评列表，这里只显示1条记录
		final String reviewContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.reviewContent.name(), "");
		// 是否是100%验证房间，如果不是不显示
		final String isVerify = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.isVerify.name(), "");
		// 验100%字符串
		final String verifyContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.verifyContent.name(), "");
		// 是否是特价房间，如果不是不显示
		final String specials = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.specials.name(), "");
		// 是否是速订房间，如果不是不显示，如果既不是100%、特价、速订，删除房间特色栏目
		final String speed = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.speed.name(), "");
		// 速订字符串
		final String speedContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.speedContent.name(), "");
		// 介绍字符串
		final String introduction = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, RoomDetailDatabaseFieldsConstant.RespondBean.introduction.name(), "");
		
		return new RoomDetailNetRespondBean(imageMList, userId, size, imageSList, image, price, title, address, len, lat, number, scheduled, bedRoom, ruleContent, clean, description,
				accommodates, roomRule, restRoom, tickets, cancellation, minNights, privacy, checkOutTime, maxNights, beds, propertyType, bedType, bathRoomNum, equipmentList, review, reviewCount,
				reviewContent, isVerify, verifyContent, specials, speed, speedContent, introduction);
		
	}
	
}
