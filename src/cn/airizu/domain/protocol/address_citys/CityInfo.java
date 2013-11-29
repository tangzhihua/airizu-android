package cn.airizu.domain.protocol.address_citys;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CityInfo implements Serializable {
	private final String id;
	private final String name;
	// 城市中文名称的拼音
	private final String code;
	private final String minLng;
	private final String maxLng;
	private final String minLat;
	private final String maxLat;
	private final String locationLat;
	private final String locationLng;
	private final String locationLatBaidu;
	private final String locationLngBaidu;
	private final String minLngBaidu;
	private final String minLatBaidu;
	private final String maxLatBaidu;
	private final String maxLngBaidu;
	private final String provinceId;
	
	public CityInfo(String id, String name, String code, String minLng, String maxLng, String minLat, String maxLat, String locationLat, String locationLng, String locationLatBaidu,
			String locationLngBaidu, String minLngBaidu, String minLatBaidu, String maxLatBaidu, String maxLngBaidu, String provinceId) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.minLng = minLng;
		this.maxLng = maxLng;
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.locationLat = locationLat;
		this.locationLng = locationLng;
		this.locationLatBaidu = locationLatBaidu;
		this.locationLngBaidu = locationLngBaidu;
		this.minLngBaidu = minLngBaidu;
		this.minLatBaidu = minLatBaidu;
		this.maxLatBaidu = maxLatBaidu;
		this.maxLngBaidu = maxLngBaidu;
		this.provinceId = provinceId;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getMinLng() {
		return minLng;
	}
	
	public String getMaxLng() {
		return maxLng;
	}
	
	public String getMinLat() {
		return minLat;
	}
	
	public String getMaxLat() {
		return maxLat;
	}
	
	public String getLocationLat() {
		return locationLat;
	}
	
	public String getLocationLng() {
		return locationLng;
	}
	
	public String getLocationLatBaidu() {
		return locationLatBaidu;
	}
	
	public String getLocationLngBaidu() {
		return locationLngBaidu;
	}
	
	public String getMinLngBaidu() {
		return minLngBaidu;
	}
	
	public String getMinLatBaidu() {
		return minLatBaidu;
	}
	
	public String getMaxLatBaidu() {
		return maxLatBaidu;
	}
	
	public String getMaxLngBaidu() {
		return maxLngBaidu;
	}
	
	public String getProvinceId() {
		return provinceId;
	}
	
	@Override
	public String toString() {
		return "CityInfo [id=" + id + ", name=" + name + ", code=" + code + ", minLng=" + minLng + ", maxLng=" + maxLng + ", minLat=" + minLat + ", maxLat=" + maxLat + ", locationLat="
				+ locationLat + ", locationLng=" + locationLng + ", locationLatBaidu=" + locationLatBaidu + ", locationLngBaidu=" + locationLngBaidu + ", minLngBaidu=" + minLngBaidu
				+ ", minLatBaidu=" + minLatBaidu + ", maxLatBaidu=" + maxLatBaidu + ", maxLngBaidu=" + maxLngBaidu + ", provinceId=" + provinceId + "]";
	}
}
