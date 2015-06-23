package org.onestonesoup.opendevice.webcam;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FormatControl;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.util.BufferToImage;

import org.onestonesoup.core.ImageHelper;

public class WebCam implements Runnable {

	private boolean streaming = false;
	private static Player player = null;
	private FrameGrabbingControl frameGrabbingControl;
	private BufferedImage bufferedImage;
	private FrameSender frameSender;
	private long frameTime = 500;
	
	public static final void main(String[] args) throws Exception {
		
		if(args.length==0) {
			String[] devices = getWebCamDevices();
			if(devices.length==0) {
				System.out.println("No Devices Found");
			}
			for(String name: devices) {
				System.out.println("Device: "+name);
			}
		} else {
			WebCam webCam = new WebCam(args[0]);
			webCam.capture();
			ImageIO.write(webCam.bufferedImage, "PNG", new File("~/webcam.png"));
		}
	}
	
	public static final String[] getWebCamDevices() {
		Vector<CaptureDeviceInfo> devices = CaptureDeviceManager.getDeviceList(null);
		List<String> names = new ArrayList<String>();
		
		for(CaptureDeviceInfo deviceInfo: devices) {
			names.add(deviceInfo.getName());
		}
		
		return names.toArray(new String[]{});
	}
	
	public WebCam() {}
	
	public WebCam(String deviceName) throws Exception
	{
		
		int format=0;
		if(deviceName.indexOf("{")!=-1)
		{
			format=Integer.parseInt( deviceName.substring(deviceName.indexOf("{")+1,deviceName.indexOf("}")) );
			deviceName = deviceName.substring(0,deviceName.indexOf("{"));
		}
	    CaptureDeviceInfo di = CaptureDeviceManager.getDevice( deviceName );
	    Format f = di.getFormats()[format];
	    
	    MediaLocator ml = di.getLocator();
	    
	    
	    CaptureDevice ds = ((CaptureDevice)Manager.createDataSource(ml));
	    FormatControl formatControls[] = ds.getFormatControls();
	    for(int i=0;i<formatControls.length;i++) {
	    	Format[] supportedFormats = formatControls[i].getSupportedFormats();
	    	for(int j=0;j<supportedFormats.length;j++) {
	    		if(supportedFormats[i].matches(f)) {
//	    			formatControls[i].setFormat(supportedFormats[i]);
	    		}
	    	}
	    }
    
	    player = Manager.createRealizedPlayer(ml);
	    player.start(); 
	    
	    while(player.getState()!=Player.Started)
	    {
	    	try{Thread.sleep(50);}catch(Exception e){}
	    }

	    frameGrabbingControl = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");	      
	}
	
	public long getFrameTime() {
		return frameTime;
	}
	public void setFrameTime(long frameTime) {
		this.frameTime = frameTime;
	}
	
	public void startStreaming(FrameSender frameSender)
	{
		this.frameSender = frameSender;
		new Thread(this,"Web Cam Streamer").start();
	}
	
	public void stopStreaming()
	{
		streaming=false;
	}
	
	public void run()
	{
		if(streaming==true) return;
		streaming=true;
		
		while(streaming)
		{
			capture();

		    try{Thread.sleep(frameTime);}catch(Exception e){}
		}
	}
	
	public void sendFrame() throws Exception
	{
		if(frameSender==null) {
			return;
		}
		OutputStream outputStream = frameSender.getOutputStream(bufferedImage);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	  	ImageIO.write(bufferedImage,"PNG",bOut);
	  	bOut.writeTo(outputStream);
	  	outputStream.close();
	}
	
	private void capture()
	{
		  // Grab a frame
		  Buffer buf = frameGrabbingControl.grabFrame();
		  
		  // Convert it to an image
		  BufferToImage btoi = new BufferToImage((VideoFormat)buf.getFormat());
		  Image img = btoi.createImage(buf);
		
		  while(img==null)
		  {
			  buf = frameGrabbingControl.grabFrame();
		      btoi = new BufferToImage((VideoFormat)buf.getFormat());
		      img = btoi.createImage(buf);
		
		      try{Thread.sleep(10);}catch(Exception e){}
		  }	      
		  
		BufferedImage newBufferedImage = ImageHelper.convertToBufferedImage(img);
		bufferedImage = newBufferedImage;
	}
	
	public void destroy()
	{
	    player.close();
	    player.deallocate();
	}
	
	public void finalize(){
		destroy();
	}
}
