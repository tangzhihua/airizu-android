package cn.airizu.global_data_cache;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GlobalConstant {
	private GlobalConstant() {
		
	}
	
	public static enum NewVersionDetectStateEnum {
		// 还未进行 新版本 检测
		NOT_YET_DETECTED,
		// 服务器端有新版本存在
		HAS_NEW_VERSION,
		// 本地已经是最新版本
		LOCAL_APP_IS_THE_LATEST
	}
	
	public static final int HANDLER_EMPTY_MESSAGE_WHAT = -2012;
	
	public static enum OrderTypeEnum {
		// 普通订单
		ORDINARY("0"),
		// 快速支付订单
		QUICK_PAY("1");
		
		private final String value;
		
		public String getValue() {
			return value;
		}
		
		private OrderTypeEnum(String value) {
			this.value = value;
		}
	}
	
	public static enum SexEnum {
		//
		WOMEN("0"),
		//
		MEN("1");
		
		private final String value;
		
		public String getValue() {
			return value;
		}
		
		private SexEnum(String value) {
			this.value = value;
		}
	}
	
	// 房源列表排序类型(这个枚举类, 一定要使用 getValue())
	public static enum RoomListOrderTypeEnum {
		// 爱日租推荐
		ORDER_BY_AIRIZU_COMMEND("tja"),
		// 价格从高到低
		ORDER_BY_PRICE_HEIGHT_TO_LOW("jgd"),
		// 价格从低到高
		ORDER_BY_PRICE_LOW_TO_HEIGHT("jga"),
		// 评论从高到低
		ORDER_BY_COMMEND_NUMBER("pjd"),
		// 距离由近到远
		ORDER_BY_DISTANCE("jla");
		
		private final String value;
		
		public String getValue() {
			return value;
		}
		
		private RoomListOrderTypeEnum(String value) {
			this.value = value;
		}
	}
	
	// 优惠卷类型
	public static enum VoucherMethodEnum {
		// 通过括号赋值,而且必须有带参构造器和一属性跟方法，否则编译出错,
		// 赋值必须是都赋值或都不赋值，不能一部分赋值一部分不赋值,
		// 如果不赋值则不能写构造器，赋值编译也出错
		
		// 不使用优惠
		DO_NOT_USE_PROMOTIONS(0),
		// VIP优惠
		VIP_PROMOTIONS(1),
		// 普通优惠卷
		ORDINARY_COUPONS(2),
		// 现金卷
		CASH_VOLUME(3);
		
		private final int value;
		
		public int getValue() {
			return value;
		}
		
		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		VoucherMethodEnum(int value) {
			this.value = value;
		}
	}
	
	// 注意:枚举重写了ToString(),说以枚举变量的值是不带前缀的, 所以在switch中使用枚举对象时, 不要加前缀
	
	// 订单状态
	public static enum OrderStateEnum {
		// 待确认
		WAIT_CONFIRM(1),
		// 待支付
		WAIT_PAY(2),
		// 待入住
		WAIT_LIVE(3),
		// 待评论
		WAIT_COMMENT(4),
		// 已完成
		HAS_ENDED(5);
		
		private final int value;
		
		public int getValue() {
			return value;
		}
		
		OrderStateEnum(int value) {
			this.value = value;
		}
	}
	
	public static enum UserActionEnum {
		// 用户登录 "成功"
		USER_LOGON_SUCCESS,
		// 获取用户当前的经纬度 "成功"
		GET_USER_LOCATION_SUCCESS,
		// 获取用户当前地址 "成功"
		GET_USER_ADDRESS_SUCCESS,
		// 获取软件新版本信息 "成功"
		GET_NEW_APP_VERSION_SUCCESS,
		// 跳转到首页
		GOTO_HOME_PAGE
	};
	
	// 图片获取的方式
	public static enum PhotoGetTypeActionEnum {
		// 相册
		PICK("android.intent.action.PICK"),
		// 照相机
		IMAGE_CAPTURE(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		
		private final String value;
		
		public String getValue() {
			return value;
		}
		
		private PhotoGetTypeActionEnum(String value) {
			this.value = value;
		}
	}
	
	public static final Map<String, String> dataSourceForPhotoGetTypeList = new LinkedHashMap<String, String>();
	
	public static void initDataSourceForPhotoGetTypeList(Map<String, String> map) {
		map.put("调用相册", PhotoGetTypeActionEnum.PICK.getValue());
		map.put("调用照相机", PhotoGetTypeActionEnum.IMAGE_CAPTURE.getValue());
	}
	
	public static final Map<String, String> dataSourceForOccupancyCountList = new LinkedHashMap<String, String>();
	
	public static void initDataSourceForOccupancyCountList(Map<String, String> map) {
		map.put("1人", "1");
		map.put("2人", "2");
		map.put("3人", "3");
		map.put("4人", "4");
		map.put("5人", "5");
		map.put("6人", "6");
		map.put("7人", "7");
		map.put("8人", "8");
		map.put("9人", "9");
		map.put("10人以上", "10");
	}
	
	public static final Map<String, String> dataSourceForPriceDifferenceList = new LinkedHashMap<String, String>();
	
	public static void initDataSourceForPriceDifferenceList(Map<String, String> map) {
		map.put("100元", "0-100");
		map.put("100-200元", "100-200");
		map.put("200-300元", "200-300");
		map.put("300元以上", "300");
	}
	
	public static final Map<String, String> dataSourceForDistanceList = new LinkedHashMap<String, String>();
	
	public static void initDataSourceForDistanceList(Map<String, String> map) {
		map.put("500米", "500");
		map.put("1000米", "1000");
		map.put("3000米", "3000");
	}
	
	public static final Map<String, String> dataSourceForSortTypeList = new LinkedHashMap<String, String>();
	
	public static void initDataSourceForSortTypeList(Map<String, String> map) {
		map.put("爱日租推荐", GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_AIRIZU_COMMEND.getValue());
		map.put("价格从高到低", GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_PRICE_HEIGHT_TO_LOW.getValue());
		map.put("价格从低到高", GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_PRICE_LOW_TO_HEIGHT.getValue());
		map.put("评论从高到低", GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_COMMEND_NUMBER.getValue());
		map.put("距离由近到远", GlobalConstant.RoomListOrderTypeEnum.ORDER_BY_DISTANCE.getValue());
	}
	
	static {
		initDataSourceForPhotoGetTypeList(dataSourceForPhotoGetTypeList);
		initDataSourceForOccupancyCountList(dataSourceForOccupancyCountList);
		initDataSourceForPriceDifferenceList(dataSourceForPriceDifferenceList);
		initDataSourceForDistanceList(dataSourceForDistanceList);
		initDataSourceForSortTypeList(dataSourceForSortTypeList);
	}
	
	public static final String MONEY_MARK_STRING = "￥";
}
