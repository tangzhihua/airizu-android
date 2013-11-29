package cn.airizu.domain.domainbean_strategy_mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhihua.tang
 * 
 */
public abstract class StrategyClassNameMappingBase {
	protected Map<String, String> strategyClassesNameMappingList = new HashMap<String, String>(100);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getTargetClassNameForKey(String key) {
		String className = null;

		do {
			if (null == key || key.equals("")) {
				break;
			}
			className = strategyClassesNameMappingList.get(key);
		} while (false);

		return className;
	}
}
