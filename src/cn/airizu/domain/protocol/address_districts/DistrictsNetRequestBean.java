package cn.airizu.domain.protocol.address_districts;

public class DistrictsNetRequestBean {
	private String cityId;
	
	public String getCityId() {
		return cityId;
	}
	
	public DistrictsNetRequestBean(String cityId) {
		this.cityId = cityId;
	}
	
	@Override
	public String toString() {
		return "DistrictsNetRequestBean [cityId=" + cityId + "]";
	}
	
}
