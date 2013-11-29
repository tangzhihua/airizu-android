package cn.airizu.domain.protocol.review_item;

import java.util.Map;

public final class ReviewItemNetRespondBean {
	
	private Map<String, String> itemMap;
	
	public ReviewItemNetRespondBean(Map<String, String> itemMap) {
		this.itemMap = itemMap;
	}
	
	public Map<String, String> getItemMap() {
		return itemMap;
	}
	
	@Override
	public String toString() {
		return "ReviewItemNetRespondBean [itemMap=" + itemMap + "]";
	}
}
