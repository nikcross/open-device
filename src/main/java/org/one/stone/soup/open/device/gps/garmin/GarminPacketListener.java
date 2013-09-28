package org.one.stone.soup.open.device.gps.garmin;

public interface GarminPacketListener {
/**
 * 
 * @param packet wet.wired.gps.garmin.GarminPacket
 */
void packetResponse(GarminPacket packet);
}
