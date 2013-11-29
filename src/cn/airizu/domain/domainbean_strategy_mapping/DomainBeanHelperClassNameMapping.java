package cn.airizu.domain.domainbean_strategy_mapping;

import cn.airizu.domain.protocol.account_forget_password.ForgetPasswordDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_forget_password.ForgetPasswordNetRequestBean;
import cn.airizu.domain.protocol.account_help.HelpDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_help.HelpNetRequestBean;
import cn.airizu.domain.protocol.account_index.AccountIndexDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_index.AccountIndexNetRequestBean;
import cn.airizu.domain.protocol.account_login.LogonDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_login.LogonNetRequestBean;
import cn.airizu.domain.protocol.account_logout.LogoutDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_logout.LogoutNetRequestBean;
import cn.airizu.domain.protocol.account_register.RegisterDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_register.RegisterNetResquestBean;
import cn.airizu.domain.protocol.account_system_message.SystemMessagesDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_system_message.SystemMessagesNetRequestBean;
import cn.airizu.domain.protocol.account_update.UpdateAccountInfoDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_update.UpdateAccountInfoNetRequestBean;
import cn.airizu.domain.protocol.account_version.VersionDomainBeanToolsFactory;
import cn.airizu.domain.protocol.account_version.VersionNetRequestBean;
import cn.airizu.domain.protocol.address_citys.CitysDomainBeanToolsFactory;
import cn.airizu.domain.protocol.address_citys.CitysNetRequestBean;
import cn.airizu.domain.protocol.address_districts.DistrictsDomainBeanToolsFactory;
import cn.airizu.domain.protocol.address_districts.DistrictsNetRequestBean;
import cn.airizu.domain.protocol.order_cancel.OrderCancelDomainBeanToolsFactory;
import cn.airizu.domain.protocol.order_cancel.OrderCancelNetRequestBean;
import cn.airizu.domain.protocol.order_detail.OrderDetailDomainBeanToolsFactory;
import cn.airizu.domain.protocol.order_detail.OrderDetailNetRequestBean;
import cn.airizu.domain.protocol.order_freebook.FreeBookDomainBeanToolsFactory;
import cn.airizu.domain.protocol.order_freebook.FreeBookNetRequestBean;
import cn.airizu.domain.protocol.order_list.OrderOverviewListDomainBeanToolsFactory;
import cn.airizu.domain.protocol.order_list.OrderOverviewListNetRequestBean;
import cn.airizu.domain.protocol.order_submit.OrderSubmitDomainBeanToolsFactory;
import cn.airizu.domain.protocol.order_submit.OrderSubmitNetRequestBean;
import cn.airizu.domain.protocol.pay_pay_info.PayInfoDomainBeanToolsFactory;
import cn.airizu.domain.protocol.pay_pay_info.PayInfoNetRequestBean;
import cn.airizu.domain.protocol.review_item.ReviewItemDomainBeanToolsFactory;
import cn.airizu.domain.protocol.review_item.ReviewItemNetRequestBean;
import cn.airizu.domain.protocol.review_review_list.RoomReviewDomainBeanToolsFactory;
import cn.airizu.domain.protocol.review_review_list.RoomReviewNetRequestBean;
import cn.airizu.domain.protocol.review_submit.ReviewSubmitDomainBeanToolsFactory;
import cn.airizu.domain.protocol.review_submit.ReviewSubmitNetRequestBean;
import cn.airizu.domain.protocol.room_calendar.RoomCalendarDomainBeanToolsFactory;
import cn.airizu.domain.protocol.room_calendar.RoomCalendarNetRequestBean;
import cn.airizu.domain.protocol.room_detail.RoomDetailDomainBeanToolsFactory;
import cn.airizu.domain.protocol.room_detail.RoomDetailNetRequestBean;
import cn.airizu.domain.protocol.room_dictionary.DictionaryDomainBeanToolsFactory;
import cn.airizu.domain.protocol.room_dictionary.DictionaryNetRequestBean;
import cn.airizu.domain.protocol.room_recommend.RecommendDomainBeanToolsFactory;
import cn.airizu.domain.protocol.room_recommend.RecommendNetRequestBean;
import cn.airizu.domain.protocol.room_search.RoomSearchDomainBeanToolsFactory;
import cn.airizu.domain.protocol.room_search.RoomSearchNetRequestBean;

public final class DomainBeanHelperClassNameMapping extends StrategyClassNameMappingBase {
	
	// 所有业务接口, 要在这里完成映射 
	// 注意： Key   : 网络请求业务Bean
	//       Value : 是该网络接口对应的抽象工厂类
	public DomainBeanHelperClassNameMapping() {
		
		/**
		 * 2.1 用户注册
		 */
		strategyClassesNameMappingList.put(RegisterNetResquestBean.class.getName(), RegisterDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.2 用户登录
		 */
		strategyClassesNameMappingList.put(LogonNetRequestBean.class.getName(), LogonDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.3 忘记密码
		 */
		strategyClassesNameMappingList.put(ForgetPasswordNetRequestBean.class.getName(), ForgetPasswordDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.4 房间推荐
		 */
		strategyClassesNameMappingList.put(RecommendNetRequestBean.class.getName(), RecommendDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.5房间搜索
		 */
		strategyClassesNameMappingList.put(RoomSearchNetRequestBean.class.getName(), RoomSearchDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.6 搜索城市
		 */
		strategyClassesNameMappingList.put(CitysNetRequestBean.class.getName(), CitysDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.7 根据城市获取区县
		 */
		strategyClassesNameMappingList.put(DistrictsNetRequestBean.class.getName(), DistrictsDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.8 初始化字典
		 */
		strategyClassesNameMappingList.put(DictionaryNetRequestBean.class.getName(), DictionaryDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.9 获取房屋类型(废弃)
		 */
		
		/**
		 * 2.10 获取出租方式(废弃)
		 */
		
		/**
		 * 2.11 获取房间设施(废弃)
		 */
		
		/**
		 * 2.12 房间详情
		 */
		strategyClassesNameMappingList.put(RoomDetailNetRequestBean.class.getName(), RoomDetailDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.13 规则(废弃, 会在 房间详情接口 中返回)
		 */
		
		/**
		 * 2.14 修改用户信息
		 */
		strategyClassesNameMappingList.put(UpdateAccountInfoNetRequestBean.class.getName(), UpdateAccountInfoDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.15 获取账号首页信息
		 */
		strategyClassesNameMappingList.put(AccountIndexNetRequestBean.class.getName(), AccountIndexDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.16 帮助
		 */
		strategyClassesNameMappingList.put(HelpNetRequestBean.class.getName(), HelpDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.17 登出
		 */
		strategyClassesNameMappingList.put(LogoutNetRequestBean.class.getName(), LogoutDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.18 获得系统通知
		 */
		strategyClassesNameMappingList.put(SystemMessagesNetRequestBean.class.getName(), SystemMessagesDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.19 版本更新
		 */
		strategyClassesNameMappingList.put(VersionNetRequestBean.class.getName(), VersionDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.20 订单预订
		 */
		strategyClassesNameMappingList.put(FreeBookNetRequestBean.class.getName(), FreeBookDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.21 提交订单
		 */
		strategyClassesNameMappingList.put(OrderSubmitNetRequestBean.class.getName(), OrderSubmitDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.22 查看我的订单
		 */
		strategyClassesNameMappingList.put(OrderOverviewListNetRequestBean.class.getName(), OrderOverviewListDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.23 查看订单详情
		 */
		strategyClassesNameMappingList.put(OrderDetailNetRequestBean.class.getName(), OrderDetailDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.24 取消订单
		 */
		strategyClassesNameMappingList.put(OrderCancelNetRequestBean.class.getName(), OrderCancelDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.25 获得房间评论
		 */
		strategyClassesNameMappingList.put(RoomReviewNetRequestBean.class.getName(), RoomReviewDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.26 获取评论项(这个用于 写评论 界面)
		 */
		strategyClassesNameMappingList.put(ReviewItemNetRequestBean.class.getName(), ReviewItemDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.27 对房间进行评论
		 */
		strategyClassesNameMappingList.put(ReviewSubmitNetRequestBean.class.getName(), ReviewSubmitDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.28 房间日历
		 */
		strategyClassesNameMappingList.put(RoomCalendarNetRequestBean.class.getName(), RoomCalendarDomainBeanToolsFactory.class.getName());
		
		/**
		 * 2.29 房间推荐(废弃, 统一使用 2.5房间搜索)
		 */
		
		/**
		 * 2.30 筛选和排序(废弃, 统一使用 2.5房间搜索)
		 */
		
		/**
		 * 2.31 地图(废弃, 统一使用 2.5房间搜索)
		 */
		
		/**
		 * 2.31 支付
		 */
		strategyClassesNameMappingList.put(PayInfoNetRequestBean.class.getName(), PayInfoDomainBeanToolsFactory.class.getName());
		
	}
}
