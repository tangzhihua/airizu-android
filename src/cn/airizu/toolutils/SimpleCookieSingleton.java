package cn.airizu.toolutils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleCookieSingleton {
	private static SimpleCookieSingleton instance = null;
	
	private SimpleCookieSingleton() {
		
	}
	
	public static synchronized SimpleCookieSingleton getInstance() {
		if (null == instance) {
			instance = new SimpleCookieSingleton();
		}
		return instance;
	}
	
	private Map<String, String> cookieCache = new HashMap<String, String>();
	
	public void clearCookie() {
		cookieCache.clear();
	}
	
	public void add(String key, String value) {
		cookieCache.put(key, value);
	}
	
	public void remove(String key) {
		cookieCache.remove(key);
	}
	
	public String getCookieString() {
		final StringBuilder cookieStringBuilder = new StringBuilder();
		final Set<Entry<String, String>> cookieEntries = cookieCache.entrySet();
		for (Entry<String, String> value : cookieEntries) {
			cookieStringBuilder.append(value.getKey() + "=" + value.getValue() + ";");
		}
		return cookieStringBuilder.toString();
	}
}
