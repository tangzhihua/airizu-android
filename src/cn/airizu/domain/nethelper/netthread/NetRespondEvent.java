package cn.airizu.domain.nethelper.netthread;

import java.util.Arrays;

import cn.airizu.domain.net_error_handle.NetErrorBean;

public final class NetRespondEvent {
	private final int threadID;
	private final byte[] netRespondRawEntityData;
	private final NetErrorBean netError;
	
	public NetRespondEvent(int threadID, byte[] netRespondRawEntityData, NetErrorBean netError) {
		this.threadID = threadID;
		this.netRespondRawEntityData = netRespondRawEntityData;
		this.netError = netError;
	}
	
	public int getThreadID() {
		return threadID;
	}
	
	public byte[] getNetRespondRawEntityData() {
		return netRespondRawEntityData;
	}
	
	public NetErrorBean getNetError() {
		return netError;
	}
	
	@Override
	public String toString() {
		return "NetRespondEvent [threadID=" + threadID + ", netRespondRawEntityData=" + Arrays.toString(netRespondRawEntityData) + ", netError=" + netError + "]";
	}
	
}
