package com.creativemd.craftingmanager.api.common.utils.packet;

import java.util.ArrayList;

import net.minecraft.block.BlockPortal.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class ArrayPacketEntry/* extends PacketEntry*/{
	
	public static ArrayList<PacketEntry> getPacketArray(String[] array)
	{
		ArrayList<PacketEntry> result = new ArrayList<PacketEntry>();
		result.add(new IntegerPacketEntry(array.length));
		for (int i = 0; i < array.length; i++) {
			result.add(new StringPacketEntry(array[i]));
		}
		return result;
	}
	
	public static String[] getArray(ArrayList<PacketEntry> entries)
	{
		return getArray(entries, 0);
	}
	
	public static String[] getArray(ArrayList<PacketEntry> entries, int startIndex)
	{
		String[] result = new String[((IntegerPacketEntry)entries.get(startIndex)).value];
		for (int i = 0; i < result.length; i++) {
			result[i] = ((StringPacketEntry)entries.get(i+1+startIndex)).value;
		}
		return result;
	}
	
	/*public String[] input;
	
	public ArrayPacketEntry(String[] input)
	{
		this.input = input;
	}
	
	public ArrayPacketEntry()
	{
		this.input = new String[0];
	}
	
	@Override
	public void writeEntry(ByteBuf buffer) {
		buffer.writeInt(input.length);
		for(int zahl = 0; zahl < input.length; zahl++)
		{
			int index = buffer.writerIndex();
			ByteBufUtils.writeUTF8String(buffer, input[zahl]);
			index = buffer.writerIndex()- index;
			System.out.println("Sending string:" + input[zahl] + ";Length=" + index);
			
		}
	}

	@Override
	public void readEntry(ByteBuf buffer) {
		int count = buffer.readInt();
		input = new String[count];
		for(int zahl = 0; zahl < input.length; zahl++)
			input[zahl] = ByteBufUtils.readUTF8String(buffer);
	}*/

}
