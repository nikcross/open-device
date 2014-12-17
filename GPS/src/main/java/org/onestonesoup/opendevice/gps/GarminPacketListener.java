package org.onestonesoup.opendevice.gps;

public interface GarminPacketListener {
/**
 * 
 * @param packet wet.wired.gps.garmin.GarminPacket
 */
void packetResponse(GarminPacket packet);
}
