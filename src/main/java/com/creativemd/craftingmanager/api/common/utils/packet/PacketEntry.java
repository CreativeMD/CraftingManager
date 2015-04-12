package com.creativemd.craftingmanager.api.common.utils.packet;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class PacketEntry {
	
	public static ArrayList<Class<? extends PacketEntry>> entries = new ArrayList<Class<? extends PacketEntry>>();
	
	public static void registerEntry(Class<? extends PacketEntry> entry)
	{
		//entry.id = entries.size();
		entries.add(entry);
	}
	
	public static int getID(Class<? extends PacketEntry> entry)
	{
		for (int i = 0; i < entries.size(); i++) {
			if(entry.equals(entries.get(i)))
				return i;
		}
		return -1;
	}
	
	public static PacketEntry getPacketEntry(int id)
	{
		try {
			return entries.get(id).getConstructor().newInstance();
		} catch (Exception e){
			return null;
		}
	}
	
	public abstract void writeEntry(ByteBuf buffer);
	
	public abstract void readEntry(ByteBuf buffer);
	
	/**Called after readEntry. An optional way to handle the packet.
	 * updateInformation() in ConfigSystem will handle all entries at ones.**/
	public void handleEntry(){ }
}
