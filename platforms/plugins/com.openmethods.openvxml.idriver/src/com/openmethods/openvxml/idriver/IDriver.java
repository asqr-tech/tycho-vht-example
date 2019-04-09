package com.openmethods.openvxml.idriver;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

public interface IDriver extends Library
{
	IDriver INSTANCE = (IDriver)Native.loadLibrary("ilib_SDK_MD", IDriver.class);
	NativeLong ANY_RQ = new NativeLong(0L);
	int CALL_STATUS_ESTABLISHED = 7;
	int INFO_CONN_ID = 101;
	boolean ilInitiate(String driverName);
	boolean ilSetTimeout(NativeLong timeout);
	boolean ilConnectionOpenConfigServer80(String primaryServerAddress,
			short primaryServerPort, short clientPort, String backupServerAddress,
			short backupServerPort, String appName, NativeLong timeout);
	NativeLong ilSRqNoteCallStart(NativeLong rqId, NativeLong port,
			String callId, String dnis, String ani, String tagCDT);
	NativeLong ilSRqNoteCallEnd(NativeLong rqId, NativeLong port);
	NativeLong ilSRqUDataAddKD(NativeLong rqId, NativeLong port, String key, String value);
	NativeLong ilSRqUDataAddList(NativeLong rqId, NativeLong port, String list);
	NativeLong ilSRqUDataGetKD(NativeLong rqId, NativeLong port, String key);
	NativeLong ilSRqUDataGetAll(NativeLong rqId, NativeLong port);
	NativeLong ilSRqUDataDelKD(NativeLong rqId, NativeLong port, String key);
	NativeLong ilSRqUDataDelAll(NativeLong rqId, NativeLong port);
	NativeLong ilGetReply(NativeLong requestId, ByteBuffer response, int len);
	int ilGetCallStatus(NativeLong port);
	NativeLong ilGetProcessingState();
	NativeLong ilSRqGetCallInfo(NativeLong requestId, NativeLong port, int type);
	NativeLong ilGetLastPortError(NativeLong port, int type);
}
