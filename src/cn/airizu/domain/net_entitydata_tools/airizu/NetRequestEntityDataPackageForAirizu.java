package cn.airizu.domain.net_entitydata_tools.airizu;

import java.util.Map;

import cn.airizu.domain.net_entitydata_tools.INetRequestEntityDataPackage;
import cn.airizu.toolutils.HttpEntityDataProcessTools;

public class NetRequestEntityDataPackageForAirizu implements INetRequestEntityDataPackage {
	
	@Override
	public String packageNetRequestEntityData(Map<String, String> domainDD) throws Exception {
		return HttpEntityDataProcessTools.getHttpEntityDataStringForDefault(domainDD, "UTF-8");
	}
	
}
