package org.onestonesoup.opendevice.webcam;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

public interface FrameSender {
	public OutputStream getOutputStream(BufferedImage bufferedImage);
}
