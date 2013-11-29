package cn.airizu.domain.nethelper.network_engine_for_http;

public final class HttpNetworkEngineFactoryMethodSingleton implements IHttpNetworkEngineFactoryMethod {
	private HttpNetworkEngineFactoryMethodSingleton() {
	}
	
	private static HttpNetworkEngineFactoryMethodSingleton myInstance = null;
	
	public static synchronized HttpNetworkEngineFactoryMethodSingleton getInstance() {
		if (null == myInstance) {
			myInstance = new HttpNetworkEngineFactoryMethodSingleton();
		}
		return myInstance;
	}
	
	private HttpNetworkEngineForHttpConnection httpNetworkEngineForHttpConnection = new HttpNetworkEngineForHttpConnection();
	
	@Override
	public IHttpNetworkEngine getHttpNetworkEngine() {
		return httpNetworkEngineForHttpConnection;
	}
	
}
