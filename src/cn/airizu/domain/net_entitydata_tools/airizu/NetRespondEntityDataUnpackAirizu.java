package cn.airizu.domain.net_entitydata_tools.airizu;

import cn.airizu.domain.net_entitydata_tools.INetRespondRawEntityDataUnpack;

public final class NetRespondEntityDataUnpackAirizu implements INetRespondRawEntityDataUnpack {
	
	@Override
	public String unpackNetRespondRawEntityData(byte[] rawData) throws Exception {
		return new String(rawData, "utf-8");
	}
	
}
