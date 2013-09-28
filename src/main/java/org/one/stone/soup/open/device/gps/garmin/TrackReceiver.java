package org.one.stone.soup.open.device.gps.garmin;

/**
 * Insert the type's description here.
 * Creation date: (09/08/03 10:10:41)
 * @author: 
 */
public class TrackReceiver implements GarminPacketListener,Runnable{

	private boolean running = false;
	
	private GarminGPSController controller;
	private GarminPacketListener listener;
	private GarminProcessListener receiverListener;
/**
 * TrackReceiver constructor comment.
 */
public TrackReceiver(GarminGPSController controller,GarminPacketListener listener,GarminProcessListener receiverListener) {
	super();

	this.controller = controller;
	this.listener = listener;
	this.receiverListener = receiverListener;

	new Thread(this,"Track Receiver").start();
}
public void packetResponse(GarminPacket packet)
{
	switch(packet.getResponseId())
	{
		case GarminPacketFactory.RECORDS_DATA_PID:
			packet = GarminPacketFactory.getACKPacket(GarminPacketFactory.RECORDS_DATA_PID);
			packet.addListener(this);
			controller.sendPacket( packet );
			break;
		case GarminPacketFactory.TRK_DATA_PID:
			listener.packetResponse(packet);
			packet = GarminPacketFactory.getACKPacket(GarminPacketFactory.TRK_DATA_PID);
			packet.addListener(this);
			controller.sendPacket( packet );
			break;
		case GarminPacketFactory.XFER_CMPLT_PID:
			listener.packetResponse(packet);
			packet = GarminPacketFactory.getACKPacket(GarminPacketFactory.XFER_CMPLT_PID);
			packet.addListener(this);
			controller.sendPacket( packet );
			running = false;
			break;
		default:
			System.out.println("? ID:"+packet.getResponseId());
	}
}
	/**
	 * When an object implementing interface <code>Runnable</code> is used 
	 * to create a thread, starting the thread causes the object's 
	 * <code>run</code> method to be called in that separately executing 
	 * thread. 
	 * <p>
	 * The general contract of the method <code>run</code> is that it may 
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
public void run()
{
	GarminPacket packet = GarminPacketFactory.getTransferTracksPacket();
	packet.addListener(this);
	controller.sendPacket(packet);

	running = true;

	while(running)
	{
		try{Thread.sleep(1000);}catch(Exception e){}
	}
	
	receiverListener.processComplete();
}
}
