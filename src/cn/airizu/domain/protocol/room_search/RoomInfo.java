package cn.airizu.domain.protocol.room_search;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * 新的序列化方式： android提供了一种新的类型：Parcel。本类被用作封装数据的容器， 封装后的数据可以通过Intent或IPC传递。 除了基本类型以外，只有实现了Parcelable接口的类才能被放入Parcel中。 Parcelable实现要点：需要实现三个东西 1.writeToParcel 方法。该方法将类的数据写入外部提供的Parcel中.声明如下：
 * writeToParcel (Parcel dest, int flags) 具体参数含义见javadoc 2.describeContents方法。没搞懂有什么用，反正直接返回0也可以 3.静态的Parcelable.Creator接口，本接口有两个方法： createFromParcel(Parcel in) 实现从in中创建出类的实例的功能 newArray(int size)
 * 创建一个类型为T，长度为size的数组，仅一句话（return new T[size])即可。估计本方法是供外部类反序列化本类数组使用。
 */
public class RoomInfo implements Parcelable {
	// 房间编号
	private static final String roomId = RoomSearchDatabaseFieldsConstant.RespondBean.roomId.name();
	// 房间标题
	private static final String roomTitle = RoomSearchDatabaseFieldsConstant.RespondBean.roomTitle.name();
	// 租住方式Id
	private static final String rentalWay = RoomSearchDatabaseFieldsConstant.RespondBean.rentalWay.name();
	// 租住方式名称
	private static final String rentalWayName = RoomSearchDatabaseFieldsConstant.RespondBean.rentalWayName.name();
	// 可住人数
	private static final String occupancyCount = RoomSearchDatabaseFieldsConstant.RespondBean.occupancyCount.name();
	// 评论总数
	private static final String reviewCount = RoomSearchDatabaseFieldsConstant.RespondBean.reviewCount.name();
	// 已预定晚数
	private static final String scheduled = RoomSearchDatabaseFieldsConstant.RespondBean.scheduled.name();
	// 价格
	private static final String price = RoomSearchDatabaseFieldsConstant.RespondBean.price.name();
	// 房间图片URL
	private static final String image = RoomSearchDatabaseFieldsConstant.RespondBean.image.name();
	// 是否是验证的房间
	private static final String verify = RoomSearchDatabaseFieldsConstant.RespondBean.verify.name();
	// 百度经度
	private static final String len = RoomSearchDatabaseFieldsConstant.RespondBean.len.name();
	// 百度纬度
	private static final String lat = RoomSearchDatabaseFieldsConstant.RespondBean.lat.name();
	// 距离
	private static final String distance = RoomSearchDatabaseFieldsConstant.RespondBean.distance.name();
	
	private RoomInfo() {
		
	}
	
	public RoomInfo(String roomIdString, String roomTitleString, String rentalWayString, String rentalWayNameString, String occupancyCountString, String reviewCountString,
			String scheduledString, String priceString, String imageString, String verifyString, String lenString, String latString, String distanceString) {
		bundle.putString(roomId, roomIdString);
		bundle.putString(roomTitle, roomTitleString);
		bundle.putString(rentalWay, rentalWayString);
		bundle.putString(rentalWayName, rentalWayNameString);
		bundle.putString(occupancyCount, occupancyCountString);
		bundle.putString(reviewCount, reviewCountString);
		bundle.putString(scheduled, scheduledString);
		bundle.putString(price, priceString);
		bundle.putString(image, imageString);
		bundle.putString(verify, verifyString);
		bundle.putString(len, lenString);
		bundle.putString(lat, latString);
		bundle.putString(distance, distanceString);
	}
	
	public String getRoomId() {
		return bundle.getString(roomId);
	}
	
	public String getRoomTitle() {
		return bundle.getString(roomTitle);
	}
	
	public String getRentalWay() {
		return bundle.getString(rentalWay);
	}
	
	public String getRentalWayName() {
		return bundle.getString(rentalWayName);
	}
	
	public String getOccupancyCount() {
		return bundle.getString(occupancyCount);
	}
	
	public String getReviewCount() {
		return bundle.getString(reviewCount);
	}
	
	public String getScheduled() {
		return bundle.getString(scheduled);
	}
	
	public String getPrice() {
		return bundle.getString(price);
	}
	
	public String getImage() {
		return bundle.getString(image);
	}
	
	public String getVerify() {
		return bundle.getString(verify);
	}
	
	public String getLen() {
		return bundle.getString(len);
	}
	
	public String getLat() {
		return bundle.getString(lat);
	}
	
	public String getDistance() {
		return bundle.getString(distance);
	}
	
	@Override
	public String toString() {
		return "RoomInfo [roomId=" + bundle.getString(roomId) + ", roomTitle=" + bundle.getString(roomTitle) + ", rentalWay=" + bundle.getString(rentalWay) + ", rentalWayName="
				+ bundle.getString(rentalWayName) + ", occupancyCount=" + bundle.getString(occupancyCount) + ", reviewCount=" + bundle.getString(reviewCount) + ", scheduled="
				+ bundle.getString(scheduled) + ", price=" + bundle.getString(price) + ", image=" + bundle.getString(image) + ", verify=" + bundle.getString(verify) + ", len="
				+ bundle.getString(len) + ", lat=" + bundle.getString(lat) + ", distance=" + bundle.getString(distance) + "]";
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// 这里采用Bundle是因为在Parcel中并没有key的概念存在，而Bundle相当于Map。
	private Bundle bundle = new Bundle();
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<RoomInfo> CREATOR = new Parcelable.Creator<RoomInfo>() {
		public RoomInfo createFromParcel(Parcel in) {
			RoomInfo ti = new RoomInfo();
			ti.bundle = in.readBundle();
			return ti;
		}
		
		public RoomInfo[] newArray(int size) {
			return new RoomInfo[size];
		}
	};
	
}
