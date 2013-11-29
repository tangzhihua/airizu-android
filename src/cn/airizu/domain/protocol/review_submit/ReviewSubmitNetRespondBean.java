package cn.airizu.domain.protocol.review_submit;

public class ReviewSubmitNetRespondBean {
	
	private String message;
	
	public ReviewSubmitNetRespondBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "ReviewSubmitNetRespondBean [message=" + message + "]";
	}
}
