package cn.airizu.domain.protocol.account_system_message;

public class SystemMessagesNetRequestBean {
	// 页数
	private int pageNum;
	
	public int getPageNum() {
		return pageNum;
	}
	
	public SystemMessagesNetRequestBean(int pageNum) {
		this.pageNum = pageNum;
	}
	
	@Override
	public String toString() {
		return "SystemMessagesNetRequestBean [pageNum=" + pageNum + "]";
	}
}
