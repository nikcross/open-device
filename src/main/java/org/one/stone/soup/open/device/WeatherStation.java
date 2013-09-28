package org.one.stone.soup.open.device;

public interface WeatherStation extends Thermometer{
	public double getWindSpeed();
	public int getWindDirection();
	public double getRainFall();
	public int getHumidity();
}
