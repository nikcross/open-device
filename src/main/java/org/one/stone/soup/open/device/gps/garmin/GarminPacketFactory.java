package org.one.stone.soup.open.device.gps.garmin;

import java.io.*;
public class GarminPacketFactory {

//Packet ids
	public static final int PKT_PRODUCT = 0;
	public static final int PKT_OFF = 1;
	public static final int PKT_START_PVT = 2;
	public static final int PKT_STOP_PVT = 3;		
	public static final int PKT_ACK = 4;
	public static final int PKT_NAK = 5;

//Control bytes
	public static final byte DLE = (byte)16;
	public static final byte ETX = (byte)3;

//Basic
	public static final byte ACK_PID 			= (byte)6;
	public static final byte NAK_PID 			= (byte)21;
	public static final byte PROTOCOL_ARRAY_PID = (byte)253;
	public static final byte PRODUCT_RQST_PID 	= (byte)254;
	public static final byte PRODUCT_DATA_PID 	= (byte)255;

//Link Protocol
	public static final byte COMMAND_DATA_PID 	= (byte)10;
	public static final byte XFER_CMPLT_PID 	= (byte)12;
	public static final byte DATE_TIME_DATA_PID = (byte)14;
	public static final byte POSITION_DATA_PID 	= (byte)17;
	public static final byte PRX_WPT_DATA_PID 	= (byte)19;
	public static final byte RECORDS_DATA_PID 	= (byte)27;
	public static final byte RTE_HDR_PID 		= (byte)29;
	public static final byte RTE_WPT_DATA_PID 	= (byte)30;
	public static final byte ALMANAC_DATA_PID 	= (byte)31;
	public static final byte TRK_DATA_PID 		= (byte)34;
	public static final byte WPT_DATA_PID 		= (byte)35;
	public static final byte PVT_DATA_PID 		= (byte)51;

//Command Packets
	public static final byte CMD_TRANSFER_TRACK_LOGS	= (byte)6;
	public static final byte CMD_TRANSFER_WPT			= (byte)7;
	public static final byte CMD_POWER_OFF				= (byte)8;
	public static final byte CMD_START_PVT_SEND			= (byte)49;
	public static final byte CMD_STOP_PVT_SEND			= (byte)50;
private GarminPacketFactory() {
	super();
}
private static void addStart(byte[] data,byte id) {

	data[0]=DLE;
	data[1]=id;
}
private static void complete(byte[] data, int size) {
	data[2]=(byte)(size-6);

	setChecksum(data,size);
	
	data[size-2]=DLE;
	data[size-1]=ETX;
}
private static GarminPacket generatePacket(byte[] data,byte id) {

	byte[] packet = new byte[data.length+6];

	addStart(packet,id);
	
	System.arraycopy(data,0,packet,3,data.length);

	setChecksum(packet,packet.length);

	complete(packet,packet.length);
	
	return new GarminPacket(packet);
}
public static final GarminPacket getACKPacket(byte confirmId) {
	byte[] data = new byte[2];
	data[0] = confirmId;
	return generatePacket(data,(byte)ACK_PID);
}
private static final GarminPacket getCommandPacket(byte command) {

	byte[] data = new byte[2];

	data[0]=command;
	data[1]=0;
	
	return generatePacket(data,COMMAND_DATA_PID);
}
public static final GarminPacket getStartPVTPacket() {
	return getCommandPacket(CMD_START_PVT_SEND);
}
/**
 * 
 * @return wet.wired.gps.garmin.GarminPacket
 */
public static final GarminPacket getStopPVTPacket() {
	return getCommandPacket(CMD_STOP_PVT_SEND);
}
public static final GarminPacket getTransferTracksPacket() {
	return getCommandPacket(CMD_TRANSFER_TRACK_LOGS);
}
public static final GarminPacket getTransferWayPointsPacket() {
	return getCommandPacket(CMD_TRANSFER_WPT);
}
public static final GarminPacket getTurnOffPacket() {
	return getCommandPacket(CMD_POWER_OFF);

}
private static byte makeChecksum(byte[] data, int size) {
	byte checksum = 0;

	for(int loop=1;loop<size-4;loop++)
	{
		checksum+=data[loop];
	}
	
	return (byte)-checksum;//wet.wired.math.BinaryMath.twosComplement(checksum);
}
private static byte setChecksum(byte[] data, int size) {
/*	byte checksum = 0;

	for(int loop=1;loop<size-4;loop++)
	{
		checksum+=data[loop];
	}
	
	checksum = wet.wired.math.BinaryMath.twosComplement(checksum);*/

	byte checksum = makeChecksum(data,size);

	data[size-3] = checksum;

	return checksum;
}
protected static void setPacketResponse(GarminPacket packet, byte[] response) throws GarminPacketException
{
/*	for(int loop=0;loop<response.length;loop++)
	{
		System.out.println("B["+loop+"]="+response[loop]);
	}*/
	//System.exit(1);
	
	if(response[0]!=DLE)
	{
		throw new GarminPacketException(GarminPacketException.ERROR_BAD_START);
	}
	
	if(response[1]==ACK_PID)
	{
		packet.setResponseId(ACK_PID);
	}
	else if(response[1]==NAK_PID)
	{
		packet.setResponseId(NAK_PID);
		//packet.trace.addElement("NAK found");
	}
	else if(response[1]==PVT_DATA_PID)
	{
		try{
			PVTPacket pvt = PVTPacketFactory.buildPacket(response);
			packet.setResponseId(PVT_DATA_PID);
			packet.setPvt(pvt);

			//int size = response[2];
			//packet.trace.addElement("Data Size:"+size);

			//String desc = new String(response,15,response.length-17);
			//desc.replace((char)0x00,'\n');
			//packet.trace.addElement("Description:"+desc);
		}catch(IOException ie)
		{
			throw new GarminPacketException(GarminPacketException.ERROR_BAD_PVT);
		}
	}
	else
	{
		packet.setResponseId(response[1]);
		int size = response[2];
		byte[] data = new byte[size];
		System.arraycopy(response,3,data,0,size);
		
		packet.setResponseData(data);
	}
/*	else if(response[1]==RECORDS_DATA_PID)
	{
		packet.setResponseId(RECORDS_DATA_PID);
	}
	else
	{
		throw new GarminPacketException(GarminPacketException.ERROR_BAD_PID);
	}*/
}
}
