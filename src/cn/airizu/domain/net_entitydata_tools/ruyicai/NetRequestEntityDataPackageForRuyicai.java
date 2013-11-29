package cn.airizu.domain.net_entitydata_tools.ruyicai;

import java.util.Map;

import cn.airizu.domain.net_entitydata_tools.INetRequestEntityDataPackage;
import cn.airizu.toolutils.HttpEntityDataProcessTools;

public class NetRequestEntityDataPackageForRuyicai implements INetRequestEntityDataPackage {

	@Override
	public String packageNetRequestEntityData(Map<String, String> domainDD) throws Exception {
		// 先生成JSON格式的数据
		String entityString = HttpEntityDataProcessTools.getHttpEntityDataStringForJSON(domainDD, "UTF-8");
		// 加密数据
		//return ToolsAesCrypt.Encrypt(entityString, Constants.KEY);
		return entityString;
	}

}
