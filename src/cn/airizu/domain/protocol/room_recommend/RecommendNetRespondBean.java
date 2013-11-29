package cn.airizu.domain.protocol.room_recommend;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class RecommendNetRespondBean implements Serializable {
	
	public RecommendNetRespondBean(List<RecommendCity> recommendCityList) {
		this.recommendCityList = recommendCityList;
	}
	
	private List<RecommendCity> recommendCityList;
	
	public List<RecommendCity> getRecommendCityList() {
		return recommendCityList;
	}
	
	@Override
	public String toString() {
		return "RecommendNetRespondBean [recommendCityList=" + recommendCityList + "]";
	}
}
