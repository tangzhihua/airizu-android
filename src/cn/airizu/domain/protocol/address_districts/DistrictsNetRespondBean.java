package cn.airizu.domain.protocol.address_districts;

import java.util.List;

public class DistrictsNetRespondBean {
	
	private List<DistrictInfo> districtInfoList;
	
	public DistrictsNetRespondBean(List<DistrictInfo> districtInfoList) {
		this.districtInfoList = districtInfoList;
	}
	
	public List<DistrictInfo> getDistrictInfoList() {
		return districtInfoList;
	}
	
	@Override
	public String toString() {
		return "DistrictsNetRespondBean [districtInfoList=" + districtInfoList + "]";
	}
	
}
