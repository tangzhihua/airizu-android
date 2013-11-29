package cn.airizu.domain.protocol.address_districts;

public class DistrictInfo {
	/**
	 * 设置内层详情的bean
	 */
	
	private final String id;
	private final String name;
	private final String code;
	private final String minLng;
	private final String maxLng;
	private final String minLat;
	private final String maxLat;
	private final String locationLat;
	private final String locationLng;
	private final String hot;
	private final String sort;
	private final String locationLatBaidu;
	private final String locationLngBaidu;
	private final String minLngBaidu;
	private final String minLatBaidu;
	private final String maxLatBaidu;
	private final String maxLngBaidu;
	private final String cityId;
	
	public DistrictInfo(String id, String name, String code, String minLng, String maxLng, String minLat, String maxLat, String locationLat, String locationLng, String hot, String sort,
			String locationLatBaidu, String locationLngBaidu, String minLngBaidu, String minLatBaidu, String maxLatBaidu, String maxLngBaidu, String cityId) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.minLng = minLng;
		this.maxLng = maxLng;
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.locationLat = locationLat;
		this.locationLng = locationLng;
		this.hot = hot;
		this.sort = sort;
		this.locationLatBaidu = locationLatBaidu;
		this.locationLngBaidu = locationLngBaidu;
		this.minLngBaidu = minLngBaidu;
		this.minLatBaidu = minLatBaidu;
		this.maxLatBaidu = maxLatBaidu;
		this.maxLngBaidu = maxLngBaidu;
		this.cityId = cityId;
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
	
	public String getHot() {
		return hot;
	}
	
	public String getSort() {
		return sort;
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
	
	public String getCityId() {
		return cityId;
	}
	
	@Override
	public String toString() {
		return "DistrictInfo [id=" + id + ", name=" + name + ", code=" + code + ", minLng=" + minLng + ", maxLng=" + maxLng + ", minLat=" + minLat + ", maxLat=" + maxLat + ", locationLat="
				+ locationLat + ", locationLng=" + locationLng + ", hot=" + hot + ", sort=" + sort + ", locationLatBaidu=" + locationLatBaidu + ", locationLngBaidu=" + locationLngBaidu
				+ ", minLngBaidu=" + minLngBaidu + ", minLatBaidu=" + minLatBaidu + ", maxLatBaidu=" + maxLatBaidu + ", maxLngBaidu=" + maxLngBaidu + ", cityId=" + cityId + "]";
	}
	
}
