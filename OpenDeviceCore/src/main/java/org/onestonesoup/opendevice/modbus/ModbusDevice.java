package org.onestonesoup.opendevice.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.one.stone.soup.core.StringHelper;
import org.one.stone.soup.core.data.BitString;

/**
 *
 * Information Sources:
 * http://www.lammertbies.nl/comm/info/modbus.html
 * http://www.simplymodbus.ca/FC03.htm
 *
 */

public class ModbusDevice {
           
            public static void main(String[] args)
            {
                        ModbusDevice controller = new ModbusDevice();
                       
                        byte[] frame = controller.createReadCoilStatusFrame(17, 19, 37);
                        System.out.println("Coil Status Frame:"+StringHelper.asHex(frame));
                       
                        frame = controller.createReadInputStatusFrame(17, 10197, 22);
                        System.out.println("Input Status Frame:"+StringHelper.asHex(frame)); 
                   
                        frame = controller.createReadHoldingRegistersFrame(17, 10197, 22);
                        System.out.println("Input Status Frame:"+StringHelper.asHex(frame));
                       
                        frame = controller.createReadInputRegistersFrame(17, 10197, 22);
                        System.out.println("Input Status Frame:"+StringHelper.asHex(frame));
                       
                        frame = controller.createForceSingleCoilFrame(17, 10197, true);
                        System.out.println("Input Status Frame:"+StringHelper.asHex(frame)); 
                      
                        frame = controller.createPresetSingleRegisterFrame(17, 10197, 22);
                        System.out.println("Input Status Frame:"+StringHelper.asHex(frame));             
            }
          
            public byte[] processFrame(OutputStream oStream,InputStream iStream,byte[] frame) throws IOException
            {
                        oStream.write( frame );
                        return readFrame( iStream );
            }
           
            public byte[] createReadCoilStatusFrame(int deviceId,int coilAddress,int numberOfCoils)
            {
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x01;
                       
                        byte[] address = getIntAsArray( coilAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        byte[] coils = getIntAsArray( numberOfCoils );
                       
                        frame[4] = coils[0];
                        frame[5] = coils[1];                    
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }
           
            public byte[] createReadInputStatusFrame(int deviceId,int inputAddress,int numberOfInputs)
            {
                        inputAddress-=10001;
                       
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x02;
                       
                        byte[] address = getIntAsArray( inputAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        byte[] coils = getIntAsArray( numberOfInputs );
                       
                        frame[4] = coils[0];
                        frame[5] = coils[1];                    
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }          
           
            public byte[] createReadHoldingRegistersFrame(int deviceId,int registerAddress,int numberOfRegisters)
            {
                        //registerAddress -= 40001;
                       
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x03;
                       
                        byte[] address = getIntAsArray( registerAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        byte[] coils = getIntAsArray( numberOfRegisters );
                       
                        frame[4] = coils[0];
                        frame[5] = coils[1];                    
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }
           
            public byte[] createReadInputRegistersFrame(int deviceId,int registerAddress,int numberOfRegisters)
            {
                        //registerAddress -= 30001;
                       
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x04;
                       
                        byte[] address = getIntAsArray( registerAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        byte[] coils = getIntAsArray( numberOfRegisters );
                       
                        frame[4] = coils[0];
                        frame[5] = coils[1];                    
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }
           
            public byte[] createForceSingleCoilFrame(int deviceId,int coilAddress,boolean state)
            {
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x01;
                       
                        byte[] address = getIntAsArray( coilAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        if(state==true)
                        {
                                    frame[4] = (byte)0xFF;
                                    frame[5] = 0x00;
                        }
                        else
                        {
                                    frame[4] = 0x00;
                                    frame[5] = 0x00;                                   
                        }
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }
           
            public byte[] createPresetSingleRegisterFrame(int deviceId,int registerAddress,int value)
            {
                        registerAddress -= 40001;
                       
                        byte[] frame = new byte[8];
                        frame[0] = (byte)deviceId;
                        frame[1] = 0x01;
                       
                        byte[] address = getIntAsArray( registerAddress );
                       
                        frame[2] = address[0];
                        frame[3] = address[1];
                       
                        byte[] coils = getIntAsArray( value );
                      
                        frame[4] = coils[0];
                        frame[5] = coils[1];                    
                       
                        byte[] crc = getIntAsArray( generateCRC(frame,6) );
                        frame[6] = crc[1];
                        frame[7] = crc[0];                      
                       
                        return frame;
            }
           
            public byte[] createForceMultipleCoilsFrame(int deviceId,int coilAddress,boolean[] states)
            {
                        byte[] frame = new byte[8];
                       
                        return frame;
            }
           
            public byte[] createPresetMultipleRegistersFrame(int deviceId,int coilAddress,int[] values)
            {
                        byte[] frame = new byte[8];
                       
                        return frame;
            }
           
            public byte[] readFrame(InputStream iStream) throws IOException
            {
            	        byte id = (byte)iStream.read();
                        byte command = (byte)iStream.read();
                        int size = iStream.read();
                       
                        byte[] frame = new byte[size+5];
                        frame[0] = id;
                        frame[1] = command;
                        frame[2] = (byte)size;
                       
                        for(int loop=0;loop<size+2;loop++)
                        {
                                    frame[loop+3] = (byte)iStream.read();
                        }
                       
                        int calculatedCrc = generateCRC(frame,frame.length-2);
                        int recievedCrc = getArrayAsInt(frame,size+3);
           
                        if(recievedCrc!=calculatedCrc)
                        {
                                    throw new IOException( "CRC failed" );
                        }
                       
                        return frame;
            }
           
            public boolean getFrameBitField(byte[] frame,int index) throws Exception
            {
                        byte[] data = new byte[frame.length-4];
                        System.arraycopy(frame, 2, data, 0, frame.length-4);
                        BitString bitString = new BitString(data);
                       
                        return bitString.getBit(index);
            }
   
            public int getFrameWordField(byte[] frame,int index)
            {
            	int position = 3+(index*2);
                int data = 0;
                if(frame[position]<0)
                {
                	data += (256+frame[position]);
                }
                else
                {
                	data += frame[position];
                }
                data = data<<8;
                if(frame[position+1]<0)
                {
                	data += (256+frame[position+1]);
                }
                else
                {
                	data += frame[position+1];
                }
                return data;
            }            
            
            public int getFrameIntField(byte[] frame,int index)
            {
            	int position = 3+(index*2);
                int data = 0;
                if(frame[position+1]<0)
                {
                	data += (256+frame[position+1]);
                }
                else
                {
                	data += frame[position+1];
                }
                data = data<<8;
                if(frame[position]<0)
                {
                	data += (256+frame[position]);
                }
                else
                {
                	data += frame[position];
                }
                return data;
            }
           
            public int generateCRC(byte[] data,int length)
            {
                        int crc=0xffff;
                        for(int loop=0;loop<length;loop++)
                        {
                                    byte c = data[loop];
                                    int i=0;
                                    int j=0;
                                   
                                    for(i=0;i!=8;c>>=1,i++)
                                    {
                                                j=(c^crc)&1;
                                                crc>>=1;
                                               
                                                if(j!=0)
                                                {
                                                            crc^=0xa001;
                                                }
                                    }
                        }
                        return crc;                    
            }
           
            public byte[] getIntAsArray(int address)
            {
                        byte[] data = new byte[2];
                        data[0] = (byte)((address>>8) & 0xFF);
                        data[1] = (byte)(address & 0xFF);
                       
                        return data;
            }
           
            public int getArrayAsInt(byte[] frame,int position)
            {
                        int data = 0;
                        if(frame[position+1]<0)
                        {
                        	data += (256+frame[position+1]);
                        }
                        else
                        {
                        	data += frame[position+1];
                        }
                        data = data<<8;
                        if(frame[position]<0)
                        {
                        	data += (256+frame[position]);
                        }
                        else
                        {
                        	data += frame[position];
                        }
                        return data;
            }
}
 
