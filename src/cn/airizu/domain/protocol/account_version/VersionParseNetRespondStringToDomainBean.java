package cn.airizu.domain.protocol.account_version;

import org.json.JSONObject;
import android.text.TextUtils;
import cn.airizu.domain.domainbean_tools.IParseNetRespondStringToDomainBean;
import cn.airizu.toolutils.JSONTools;

public final class VersionParseNetRespondStringToDomainBean implements IParseNetRespondStringToDomainBean {
	
	@Override
	public Object parseNetRespondStringToDomainBean(String netRespondString) throws Exception {
		
		if (TextUtils.isEmpty(netRespondString)) {
			throw new IllegalArgumentException("netRespondString is empty ! ");
		}
		
		JSONObject jsonRootObject = new JSONObject(netRespondString);
		// 关键数据丢失测试
		if (JSONTools.isEmpty(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.newVersion.name())) {
			throw new IllegalArgumentException("is not find 'newVersion' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.fileSize.name())) {
			throw new IllegalArgumentException("is not find 'fileSize' field ! ");
		}
		if (JSONTools.isEmpty(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.downloadAddress.name())) {
			throw new IllegalArgumentException("is not find 'downloadAddress' field ! ");
		}
		String newVersion = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.newVersion.name(), "");
		String fileSize = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.fileSize.name(), "");
		String updateContent = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.updateContent.name(), "");
		String downloadURL = JSONTools.safeParseJSONObjectForValueIsString(jsonRootObject, VersionDatabaseFieldsConstant.RespondBean.downloadAddress.name(), "");
		
		return new VersionNetRespondBean(newVersion, fileSize, updateContent, downloadURL);
	}
}
