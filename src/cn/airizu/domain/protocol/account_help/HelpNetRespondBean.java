package cn.airizu.domain.protocol.account_help;

import java.util.List;

public class HelpNetRespondBean {
	
	private final List<HelpInfo> helpInfoList;
	
	public HelpNetRespondBean(List<HelpInfo> helpInfoList) {
		this.helpInfoList = helpInfoList;
	}
	
	public List<HelpInfo> getHelpInfoList() {
		return helpInfoList;
	}
	
	@Override
	public String toString() {
		return "HelpNetRespondBean [helpInfoList=" + helpInfoList + "]";
	}
}
