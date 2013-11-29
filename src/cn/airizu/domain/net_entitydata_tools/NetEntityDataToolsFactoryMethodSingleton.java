package cn.airizu.domain.net_entitydata_tools;

import cn.airizu.domain.net_entitydata_tools.airizu.NetRequestEntityDataPackageForAirizu;
import cn.airizu.domain.net_entitydata_tools.airizu.NetRespondEntityDataUnpackAirizu;
import cn.airizu.domain.net_entitydata_tools.airizu.ServerRespondDataTestAirizu;

/**
 * 这里使用了工厂方法设计模式
 * 
 * @author zhihua.tang
 */
public final class NetEntityDataToolsFactoryMethodSingleton implements INetEntityDataTools {
	
	private NetEntityDataToolsFactoryMethodSingleton() {
	}
	
	private static NetEntityDataToolsFactoryMethodSingleton myInstance = null;
	
	public static synchronized NetEntityDataToolsFactoryMethodSingleton getInstance() {
		if (null == myInstance) {
			myInstance = new NetEntityDataToolsFactoryMethodSingleton();
		}
		return myInstance;
	}
	
	/**
	 * 在这里完成不同项目的替换, 这里是工厂方法的应用
	 */
	INetRequestEntityDataPackage netRequestEntityDataPackage = new NetRequestEntityDataPackageForAirizu();
	INetRespondRawEntityDataUnpack netRespondEntityDataUnpack = new NetRespondEntityDataUnpackAirizu();
	IServerRespondDataTest serverRespondDataTest = new ServerRespondDataTestAirizu();
	
	@Override
	public INetRequestEntityDataPackage getNetRequestEntityDataPackage() {
		return netRequestEntityDataPackage;
	}
	
	@Override
	public INetRespondRawEntityDataUnpack getNetRespondEntityDataUnpack() {
		return netRespondEntityDataUnpack;
	}
	
	@Override
	public IServerRespondDataTest getServerRespondDataTest() {
		return serverRespondDataTest;
	}
	
}
