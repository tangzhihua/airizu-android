package cn.airizu.domain.protocol.room_detail;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class RoomDetailNetRespondBean implements Serializable {
	private final List<String> imageM;// 大图地址列表
	private final String userId;// 用户编号
	private final String size;// 建筑面积
	private final List<String> imageS;// 缩略图地址列表
	private final String image;// 房间默认图片
	private final String price;// 每晚价钱
	private final String title;// 房间标题
	private final String address;// 房间地址
	private final String len;// 百度经度
	private final String lat;// 百度纬度
	private final String number;// 房间编号
	private final String scheduled;// 曾被预定
	private final String bedRoom;// 卧室数
	private final String ruleContent;// 交易规则
	private final String clean;// 清洁服务类型
	private final String description;// 房间概括
	private final String accommodates;// 可入住人数
	private final String roomRule;// 使用规则
	private final String restRoom;// 卫浴类型
	private final String tickets;// 是否提供发票1为是,2为否
	private final String cancellation;// 退订条款
	private final String minNights;// 最少天数
	private final String privacy;// 租住方式
	private final String checkOutTime;// 退房时间
	private final String maxNights;// 最多天数
	private final String beds;// 床数
	private final String propertyType;// 房屋类型
	private final String bedType;// 床型
	private final String bathRoomNum;// 卫生间数
	private final List<String> equipmentList;// 配套设施列表
	
	private final String review;// 租客点评总分
	private final String reviewCount;// 租客点评总条数
	private final String reviewContent;// 租客点评列表，这里只显示1条记录
	
	private final String isVerify;// 是否是100%验证房间，如果不是不显示
	private final String verifyContent;// 验100%字符串
	private final String specials;// 是否是特价房间，如果不是不显示
	private final String speed;// 是否是速订房间，如果不是不显示，如果既不是100%、特价、速订，删除房间特色栏目
	private final String speedContent;// 速订字符串
	private final String introduction;// 介绍字符串
	
	public RoomDetailNetRespondBean(List<String> imageM, String userId, String size, List<String> imageS, String image, String price, String title, String address, String len, String lat,
			String number, String scheduled, String bedRoom, String ruleContent, String clean, String description, String accommodates, String roomRule, String restRoom, String tickets,
			String cancellation, String minNights, String privacy, String checkOutTime, String maxNights, String beds, String propertyType, String bedType, String bathRoomNum,
			List<String> equipmentList, String review, String reviewCount, String reviewContent, String isVerify, String verifyContent, String specials, String speed, String speedContent,
			String introduction) {
		this.imageM = imageM;
		this.userId = userId;
		this.size = size;
		this.imageS = imageS;
		this.image = image;
		this.price = price;
		this.title = title;
		this.address = address;
		this.len = len;
		this.lat = lat;
		this.number = number;
		this.scheduled = scheduled;
		this.bedRoom = bedRoom;
		this.ruleContent = ruleContent;
		this.clean = clean;
		this.description = description;
		this.accommodates = accommodates;
		this.roomRule = roomRule;
		this.restRoom = restRoom;
		this.tickets = tickets;
		this.cancellation = cancellation;
		this.minNights = minNights;
		this.privacy = privacy;
		this.checkOutTime = checkOutTime;
		this.maxNights = maxNights;
		this.beds = beds;
		this.propertyType = propertyType;
		this.bedType = bedType;
		this.bathRoomNum = bathRoomNum;
		this.equipmentList = equipmentList;
		this.review = review;
		this.reviewCount = reviewCount;
		this.reviewContent = reviewContent;
		this.isVerify = isVerify;
		this.verifyContent = verifyContent;
		this.specials = specials;
		this.speed = speed;
		this.speedContent = speedContent;
		this.introduction = introduction;
	}
	
	public List<String> getImageM() {
		return imageM;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getSize() {
		return size;
	}
	
	public List<String> getImageS() {
		return imageS;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getPrice() {
		return price;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLen() {
		return len;
	}
	
	public String getLat() {
		return lat;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getScheduled() {
		return scheduled;
	}
	
	public String getBedRoom() {
		return bedRoom;
	}
	
	public String getRuleContent() {
		return ruleContent;
	}
	
	public String getClean() {
		return clean;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAccommodates() {
		return accommodates;
	}
	
	public String getRoomRule() {
		return roomRule;
	}
	
	public String getRestRoom() {
		return restRoom;
	}
	
	public String getTickets() {
		return tickets;
	}
	
	public String getCancellation() {
		return cancellation;
	}
	
	public String getMinNights() {
		return minNights;
	}
	
	public String getPrivacy() {
		return privacy;
	}
	
	public String getCheckOutTime() {
		return checkOutTime;
	}
	
	public String getMaxNights() {
		return maxNights;
	}
	
	public String getBeds() {
		return beds;
	}
	
	public String getPropertyType() {
		return propertyType;
	}
	
	public String getBedType() {
		return bedType;
	}
	
	public String getBathRoomNum() {
		return bathRoomNum;
	}
	
	public List<String> getEquipmentList() {
		return equipmentList;
	}
	
	public String getReview() {
		return review;
	}
	
	public String getReviewCount() {
		return reviewCount;
	}
	
	public String getReviewContent() {
		return reviewContent;
	}
	
	public String getIsVerify() {
		return isVerify;
	}
	
	public String getVerifyContent() {
		return verifyContent;
	}
	
	public String getSpecials() {
		return specials;
	}
	
	public String getSpeed() {
		return speed;
	}
	
	public String getSpeedContent() {
		return speedContent;
	}
	
	public String getIntroduction() {
		return introduction;
	}
	
	@Override
	public String toString() {
		return "RoomDetailNetRespondBean [imageM=" + imageM + ", userId=" + userId + ", size=" + size + ", imageS=" + imageS + ", image=" + image + ", price=" + price + ", title=" + title
				+ ", address=" + address + ", len=" + len + ", lat=" + lat + ", number=" + number + ", scheduled=" + scheduled + ", bedRoom=" + bedRoom + ", ruleContent=" + ruleContent + ", clean="
				+ clean + ", description=" + description + ", accommodates=" + accommodates + ", roomRule=" + roomRule + ", restRoom=" + restRoom + ", tickets=" + tickets + ", cancellation="
				+ cancellation + ", minNights=" + minNights + ", privacy=" + privacy + ", checkOutTime=" + checkOutTime + ", maxNights=" + maxNights + ", beds=" + beds + ", propertyType="
				+ propertyType + ", bedType=" + bedType + ", bathRoomNum=" + bathRoomNum + ", equipmentList=" + equipmentList + ", review=" + review + ", reviewCount=" + reviewCount
				+ ", reviewContent=" + reviewContent + ", isVerify=" + isVerify + ", verifyContent=" + verifyContent + ", specials=" + specials + ", speed=" + speed + ", speedContent="
				+ speedContent + ", introduction=" + introduction + "]";
	}
	
}
