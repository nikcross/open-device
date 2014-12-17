package org.onestonesoup.opendevice.comms;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.onestonesoup.opendevice.Connection;

public class HttpConnection implements Connection {

	private String alias;
	private String host;
	public String getHost() {
		return host;
	}

	private String urlData;
	private URLConnection connection;
	public HttpConnection(String host)
	{
		this.host=host;
		this.urlData = "http://"+host+"/rpc";
	}
	
	public void connect() throws Exception {
		URL url = new URL(urlData);
		connection = url.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		connection.setRequestProperty( "Content-Type","application/x-www-form-urlencoded" );
		connection.setRequestProperty( "User-Agent","Data Logger");	
		connection.setRequestProperty( "Content-Length","122" );
	}

	public void disconnect() throws Exception {
		connection.getOutputStream().close();
	}

	public InputStream getInputStream() throws Exception {
		return connection.getInputStream();
	}

	public OutputStream getOutputStream() throws Exception {
		return connection.getOutputStream();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
	}

	public String getDefaultAlias() {
		return alias;
	}

}
