package cn.airizu.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import cn.airizu.toolutils.DebugLog;

public class AirizuBootReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getSimpleName();
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		DebugLog.i(TAG, "onReceive");
	}

	@Override
	public IBinder peekService(Context myContext, Intent service) {
		DebugLog.i(TAG, "peekService");
		return super.peekService(myContext, service);
	}
	
}
