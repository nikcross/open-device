package org.one.stone.soup.open.device.comms.linux;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class USBSerialMapTest {

	private static String ATTACH_SERIAL_0 = "[281080.591696] usb 1-1.4.4: pl2303 converter now attached to ttyUSB0";
	private static String ATTACH_SERIAL_1 = "[281080.591696] usb 1-1.4.4: pl2303 converter now attached to ttyUSB1";
	private static String ATTACH_SERIAL_2 = "[281080.591696] usb 1-1.4.4: pl2303 converter now attached to ttyUSB2";
	
	private static String DETTACH_SERIAL_0 = "[300454.015139] pl2303 ttyUSB0: pl2303 converter now disconnected from ttyUSB0";
	private static String DETTACH_SERIAL_1 = "[300454.015139] pl2303 ttyUSB1: pl2303 converter now disconnected from ttyUSB1";
	private static String DETTACH_SERIAL_2 = "[300454.015139] pl2303 ttyUSB2: pl2303 converter now disconnected from ttyUSB2";
	
	private static String SERIAL_0 = "ttyUSB0";
	private static String SERIAL_1 = "ttyUSB1";
	private static String SERIAL_2 = "ttyUSB2";
	
	@Test
	public void testSerialPortsListed() throws Exception {
		USBSerialMap map = new USBSerialMap();
		map.processMatch(ATTACH_SERIAL_0);
		map.processMatch(ATTACH_SERIAL_1);
		map.processMatch(ATTACH_SERIAL_2);
		map.processEnd();
		
		String[] ports = map.getPorts();
		ArrayList<String> portList = new ArrayList<String>();
		for(String port: ports) {
			portList.add(port);
		}
		
		assertTrue( portList.contains(SERIAL_0) );
		assertTrue( portList.contains(SERIAL_1) );
		assertTrue( portList.contains(SERIAL_2) );
	}
	
	@Test
	public void testAddedSerialPortsListed() throws Exception {
		USBSerialMap map = new USBSerialMap();
		map.processMatch(ATTACH_SERIAL_0);
		map.processMatch(ATTACH_SERIAL_1);
		map.processMatch(ATTACH_SERIAL_2);
		map.processEnd();
		
		String[] ports = map.getAddedPorts();
		ArrayList<String> portList = new ArrayList<String>();
		for(String port: ports) {
			portList.add(port);
		}
		
		assertTrue( portList.contains(SERIAL_0) );
		assertTrue( portList.contains(SERIAL_1) );
		assertTrue( portList.contains(SERIAL_2) );
		assertEquals(0, map.getRemovedPorts().length);
	}
	
	@Test 
	public void testRemovedSerialPortsListed() throws Exception {
		USBSerialMap map = new USBSerialMap();
		map.processMatch(ATTACH_SERIAL_0);
		map.processMatch(ATTACH_SERIAL_1);
		map.processMatch(ATTACH_SERIAL_2);
		map.processEnd();
		map.processMatch(DETTACH_SERIAL_0);
		map.processMatch(DETTACH_SERIAL_1);
		map.processMatch(DETTACH_SERIAL_2);
		map.processEnd();
		
		String[] ports = map.getRemovedPorts();
		ArrayList<String> portList = new ArrayList<String>();
		for(String port: ports) {
			portList.add(port);
		}
		
		assertTrue( portList.contains(SERIAL_0) );
		assertTrue( portList.contains(SERIAL_1) );
		assertTrue( portList.contains(SERIAL_2) );
		assertEquals(0, map.getPorts().length);
		assertEquals(0, map.getAddedPorts().length);
	}
}
