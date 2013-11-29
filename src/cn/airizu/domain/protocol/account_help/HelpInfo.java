package cn.airizu.domain.protocol.account_help;

public class HelpInfo {
	// 类型
	private final String type;
	// 标题
	private final String title;
	// 内容
	private final String content;
	
	public HelpInfo(String type, String title, String content) {
		this.type = type;
		this.title = title;
		this.content = content;
	}
	
	public String getType() {
		return type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		return "HelpInfo [type=" + type + ", title=" + title + ", content=" + content + "]";
	}
}
