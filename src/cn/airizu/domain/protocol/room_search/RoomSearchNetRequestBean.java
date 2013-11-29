package cn.airizu.domain.protocol.room_search;

public class RoomSearchNetRequestBean {
	
	// 二者必须有其一
	private String cityId;// 城市id
	private String cityName;// 城市名称
	
	private String streetName;// 地标名 (从 2.4 房间推荐 接口 可以获取, 另外可以从搜索界面中获取用户手动输入的
	private String checkinDate;// 入住时间(2012-01-01)
	private String checkoutDate;// 退房时间(2012-01-02)
	private String occupancyCount;// 入住人数(1~10, 10人以上传10)
	private String roomNumber;// 房间编号
	private String offset;// 查询从哪行开始
	private String max;// 查询的数据条数
	private String priceDifference;// 价格区间 (0-100, 100-200, 200-300, 300 :300以上传300)
	private String districtName;// 区名称
	private String districtId;// 区ID
	private String roomType;// 房屋类型(可在 2.8 初始化字典 接口获取)
	private String order;// 排序方式(爱日租推荐 "tja", 价格从高到低"jgd", 价格从低到高"jga", 评论从高到低"pjd", 距离由近到远"jla")
	private String rentType;// 出租方式(可在 2.8 初始化字典接口获取)
	private String tamenities;// 设施设备(可在 2.8 初始化字典接口获取)
	private String distance;// 距离筛选( 500 , 1000,3000)
	
	private String locationLat;// 纬度
	private String locationLng;// 经度
	
	private String nearby;// 是否是查询 "附近" 的房源(是就 传数字1)
	
	public String getCityId() {
		return cityId;
	}
	
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getStreetName() {
		return streetName;
	}
	
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	
	public String getCheckinDate() {
		return checkinDate;
	}
	
	public void setCheckinDate(String checkinDate) {
		this.checkinDate = checkinDate;
	}
	
	public String getCheckoutDate() {
		return checkoutDate;
	}
	
	public void setCheckoutDate(String checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	
	public String getOccupancyCount() {
		return occupancyCount;
	}
	
	public void setOccupancyCount(String occupancyCount) {
		this.occupancyCount = occupancyCount;
	}
	
	public String getRoomNumber() {
		return roomNumber;
	}
	
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	
	public String getOffset() {
		return offset;
	}
	
	public void setOffset(String offset) {
		this.offset = offset;
	}
	
	public String getMax() {
		return max;
	}
	
	public void setMax(String max) {
		this.max = max;
	}
	
	public String getPriceDifference() {
		return priceDifference;
	}
	
	public void setPriceDifference(String priceDifference) {
		this.priceDifference = priceDifference;
	}
	
	public String getDistrictName() {
		return districtName;
	}
	
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	
	public String getDistrictId() {
		return districtId;
	}
	
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	
	public String getRoomType() {
		return roomType;
	}
	
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getRentType() {
		return rentType;
	}
	
	public void setRentType(String rentType) {
		this.rentType = rentType;
	}
	
	public String getTamenities() {
		return tamenities;
	}
	
	public void setTamenities(String tamenities) {
		this.tamenities = tamenities;
	}
	
	public String getDistance() {
		return distance;
	}
	
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getLocationLat() {
		return locationLat;
	}
	
	public void setLocationLat(String locationLat) {
		this.locationLat = locationLat;
	}
	
	public String getLocationLng() {
		return locationLng;
	}
	
	public void setLocationLng(String locationLng) {
		this.locationLng = locationLng;
	}
	
	public String getNearby() {
		return nearby;
	}
	
	public void setNearby(String nearby) {
		this.nearby = nearby;
	}
	
	@Override
	public String toString() {
		return "RoomSearchNetRequestBean [cityId=" + cityId + ", cityName=" + cityName + ", streetName=" + streetName + ", checkinDate=" + checkinDate + ", checkoutDate=" + checkoutDate
				+ ", occupancyCount=" + occupancyCount + ", roomNumber=" + roomNumber + ", offset=" + offset + ", max=" + max + ", priceDifference=" + priceDifference + ", districtName="
				+ districtName + ", districtId=" + districtId + ", roomType=" + roomType + ", order=" + order + ", rentType=" + rentType + ", tamenities=" + tamenities + ", distance=" + distance
				+ ", locationLat=" + locationLat + ", locationLng=" + locationLng + ", nearby=" + nearby + "]";
	}
	
}
