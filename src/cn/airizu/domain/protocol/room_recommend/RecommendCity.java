package cn.airizu.domain.protocol.room_recommend;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RecommendCity implements Serializable {
	
	// 编号
	private final String id;
	// 城市名称
	private final String cityName;
	// 城市id
	private final String cityId;
	// 对应图片地址
	private final String image;
	// 排序
	private final String sort;
	// 地标1 名称
	private final String street1Name;
	// 地标1 编号
	private final String street1Id;
	// 地标2名称
	private final String street2Name;
	// 地标2编号
	private final String street2Id;
	
	public RecommendCity(String id, String cityName, String cityId, String image, String sort, String street1Name, String street1Id, String street2Name, String street2Id) {
		this.id = id;
		this.cityName = cityName;
		this.cityId = cityId;
		this.image = image;
		this.sort = sort;
		this.street1Name = street1Name;
		this.street1Id = street1Id;
		this.street2Name = street2Name;
		this.street2Id = street2Id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public String getCityId() {
		return cityId;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getSort() {
		return sort;
	}
	
	public String getStreet1Name() {
		return street1Name;
	}
	
	public String getStreet1Id() {
		return street1Id;
	}
	
	public String getStreet2Name() {
		return street2Name;
	}
	
	public String getStreet2Id() {
		return street2Id;
	}
	
	@Override
	public String toString() {
		return "RecommendCity [id=" + id + ", cityName=" + cityName + ", cityId=" + cityId + ", image=" + image + ", sort=" + sort + ", street1Name=" + street1Name + ", street1Id=" + street1Id
				+ ", street2Name=" + street2Name + ", street2Id=" + street2Id + "]";
	}
}
