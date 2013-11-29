package cn.airizu.domain.protocol.account_update;

import cn.airizu.net_file.upload.HttpFormFileBean;

public class UpdateAccountInfoNetRequestBean {
	// 用户名
	private final String userName;
	// 性别
	private final String sex;
	// 上传图像信息
	private final HttpFormFileBean image;
	
	private UpdateAccountInfoNetRequestBean(Builder builder) {
		this.userName = builder.userName;
		this.sex = builder.sex;
		this.image = builder.image;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getSex() {
		return sex;
	}
	
	public HttpFormFileBean getImage() {
		return image;
	}
	
	@Override
	public String toString() {
		return "UpdateAccountInfoNetRequestBean [userName=" + userName + ", sex=" + sex + "]";
	}
	
	public static class Builder {
		// 必选参数
		private final String userName;// 用户名
		private final String sex;// 性别
		
		// 可选参数
		private HttpFormFileBean image;// 上传图像信息
		
		public Builder(String userName, String sex) {
			this.userName = userName;
			this.sex = sex;
		}
		
		public Builder image(HttpFormFileBean image) {
			this.image = image;
			return this;
		}
		
		public UpdateAccountInfoNetRequestBean builder() {
			return new UpdateAccountInfoNetRequestBean(this);
		}
	}
}
