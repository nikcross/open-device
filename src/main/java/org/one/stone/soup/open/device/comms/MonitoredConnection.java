package org.one.stone.soup.open.device.comms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.one.stone.soup.open.device.Connection;

public abstract class MonitoredConnection {

	private long dataSent = 0;
	private long dataReceived = 0;
	
	private class MonitoredInputStream extends InputStream
	{
		private InputStream inputStream;
		private MonitoredInputStream(InputStream inputStream)
		{
			this.inputStream = inputStream;
		}
		
		public int read() throws IOException
		{
			dataReceived++;
			return inputStream.read();
		}

		public int available() throws IOException {
			return inputStream.available();
		}

		public void close() throws IOException {
			inputStream.close();
		}
	}
	
	private class MonitoredOutputStream extends OutputStream
	{
		private OutputStream outputStream;
		private MonitoredOutputStream(OutputStream outputStream)
		{
			this.outputStream = outputStream;
		}
		
		public void write(int b) throws IOException
		{
			dataSent++;
			outputStream.write(b);
		}

		public void close() throws IOException {
			outputStream.close();
		}

		public void flush() throws IOException {
			outputStream.flush();
		}
	}	
	
	private Connection connection;
	private long startTime;
	
	public MonitoredConnection(Connection connection) {
		this.connection = connection;
		startTime = System.currentTimeMillis();
	}
	
	public InputStream getInputStream() throws Exception{
		return new MonitoredInputStream(connection.getInputStream());
	}
	public OutputStream getOutputStream() throws Exception {
		return new MonitoredOutputStream(connection.getOutputStream());		
	}
	public void connect() throws Exception {
		connection.connect();
	}
	public void disconnect() throws Exception {
		connection.disconnect();
	}
}
