package cn.airizu.domain.protocol.account_system_message;

public final class SystemMessageDatabaseFieldsConstant {
	private SystemMessageDatabaseFieldsConstant() {
		
	}
	
	public static enum RequestBean {
		// 页数
		pageNum
	}
	
	public static enum RespondBean {
		//
		data,
		
		// 时间
		date,
		// 内容
		message
	}
}
