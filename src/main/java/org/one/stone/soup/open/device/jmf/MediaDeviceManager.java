package org.one.stone.soup.open.device.jmf;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;

public class MediaDeviceManager 
{
	private HashMap<String,WebCam> webCams =  new HashMap<String,WebCam>();
	
	public class NullWebCam extends WebCam { }
	
	public MediaDeviceManager()
	{
	}
	
	public class WebFrameSender implements FrameSender {

		public WebFrameSender() {
			
		}
		
		public OutputStream getOutputStream(BufferedImage bufferedImage) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public String[] getDevices()
	{
		ArrayList<String> devices = new ArrayList<String>();
		Vector list = CaptureDeviceManager.getDeviceList(null);
		for(int i=0;i<list.size();i++)
		{
			CaptureDeviceInfo info = (CaptureDeviceInfo)list.get(i);
			Format[] formats = info.getFormats();
			for(int j=0;j<formats.length;j++)
			{
				devices.add(info.getName().replace("[","(").replace("]",")")+"{"+j+"}"+formats[j]);
			}
		}
		
		return devices.toArray(new String[]{});
	}
	
	public void startStreaming(String deviceName,String targetUrl) {
		
	}
	
	public WebCam getWebCam(String deviceName) throws Exception
	{
		if(webCams.get(deviceName)!=null)
		{
			WebCam cam = webCams.get(deviceName);
			if(cam instanceof NullWebCam) {
				return null;
			} else {
				return cam;
			}
		}
		else
		{
			webCams.put( deviceName,new NullWebCam() );
			
			WebCam webCam = new WebCam(deviceName);
			
			webCams.put( deviceName,webCam );
			return webCams.get(deviceName);
		}
	}
	
	public void releaseWebCam(String deviceName) throws Exception
	{
		WebCam webCam = getWebCam(deviceName);
		webCams.remove(deviceName);
		webCam.destroy();
	}
}
