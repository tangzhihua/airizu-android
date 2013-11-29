package cn.airizu.domain.protocol.account_system_message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SystemMessage implements Serializable{
	// 时间
	private final String date;
	// 内容
	private final String message;
	
	public String getDate() {
		return date;
	}
	
	public String getMessage() {
		return message;
	}
	
	public SystemMessage(String date, String message) {
		this.date = date;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "SystemMessage [date=" + date + ", message=" + message + "]";
	}
}
