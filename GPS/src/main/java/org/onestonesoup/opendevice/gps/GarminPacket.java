package org.onestonesoup.opendevice.gps;

import java.util.*;

public class GarminPacket {

	private int id;
	private byte[] data;

	private int responseId;
	private byte[] responseData;
	private PVTPacket pvt;

	private Vector listeners;
protected GarminPacket(byte[] newData) {
	super();

	data = newData;
}
/**
 * 
 * @param listener wet.wired.gps.garmin.GarminPacketListener
 */
protected void addListener(GarminPacketListener listener)
{
	if(listeners==null)
	{
		listeners = new Vector();
	}

	listeners.addElement(listener);
}
/**
 * 
 * @return byte[]
 */
public byte[] getBytes() {
	return data;
}
/**
 * 
 * @return int
 */
public int getId() {
	return id;
}
/**
 * 
 * @return wet.wired.gps.garmin.PVTPacket
 */
public PVTPacket getPvt() {
	return pvt;
}
/**
 * Insert the method's description here.
 * Creation date: (09/08/03 11:48:41)
 * @return byte[]
 */
public byte[] getResponseData() {
	return responseData;
}
/**
 * 
 * @return int
 */
public int getResponseId() {
	return responseId;
}
/**
 * 
 * @return boolean
 */
public boolean isWaiting() {
	return false;
}
/**
 * 
 *
 */
protected void notifyListeners()
{
	if(listeners==null)
	{
		return;
	}

	for(int loop=0;loop<listeners.size();loop++)
	{
		((GarminPacketListener)listeners.elementAt(loop)).packetResponse(this);
	}
}
/**
 * 
 * @return boolean
 */
public boolean overidesWaiting() {
	return false;
}
/**
 * 
 * @param newPvt wet.wired.gps.garmin.PVTPacket
 */
protected void setPvt(PVTPacket newPvt) {
	pvt = newPvt;
}
/**
 * Insert the method's description here.
 * Creation date: (09/08/03 11:48:41)
 * @param newResponseData byte[]
 */
public void setResponseData(byte[] newResponseData) {
	responseData = newResponseData;
}
/**
 * 
 * @param newResponseId int
 */
protected void setResponseId(int newResponseId) {
	responseId = newResponseId;
}
}
