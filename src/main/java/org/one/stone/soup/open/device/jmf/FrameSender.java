package org.one.stone.soup.open.device.jmf;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

public interface FrameSender {
	public OutputStream getOutputStream(BufferedImage bufferedImage);
}
