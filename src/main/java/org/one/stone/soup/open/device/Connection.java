package org.one.stone.soup.open.device;

import java.io.InputStream;
import java.io.OutputStream;

public interface Connection extends AliasedInstance{
	public abstract InputStream getInputStream() throws Exception;
	public abstract OutputStream getOutputStream() throws Exception;
	public abstract void connect() throws Exception;
	public abstract void disconnect() throws Exception;
}
