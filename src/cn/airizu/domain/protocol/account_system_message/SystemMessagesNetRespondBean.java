package cn.airizu.domain.protocol.account_system_message;

import java.util.List;

public class SystemMessagesNetRespondBean {
	
	private List<SystemMessage> systemMessageList;
	
	public SystemMessagesNetRespondBean(List<SystemMessage> systemMessageList) {
		this.systemMessageList = systemMessageList;
	}
	
	public List<SystemMessage> getSystemMessageList() {
		return systemMessageList;
	}
	
	@Override
	public String toString() {
		return "SystemMessagesNetRespondBean [systemMessageList=" + systemMessageList + "]";
	}
}
