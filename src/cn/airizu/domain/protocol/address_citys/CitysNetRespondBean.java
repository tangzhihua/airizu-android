package cn.airizu.domain.protocol.address_citys;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class CitysNetRespondBean implements Serializable {
	
	private List<CityInfo> cityInfoList;
	
	public CitysNetRespondBean(List<CityInfo> cityInfoList) {
		this.cityInfoList = cityInfoList;
	}
	
	public List<CityInfo> getCityInfoList() {
		return cityInfoList;
	}

	@Override
	public String toString() {
		return "CitysNetRespondBean [cityInfoList=" + cityInfoList + "]";
	}
}
