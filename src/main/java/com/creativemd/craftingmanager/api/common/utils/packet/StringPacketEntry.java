package com.creativemd.craftingmanager.api.common.utils.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class StringPacketEntry extends PacketEntry{
	
	public String value;
	
	public StringPacketEntry()
	{
		this.value = "";
	}
	
	public StringPacketEntry(String value)
	{
		this.value = value;
	}
	
	@Override
	public void writeEntry(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, value);
	}

	@Override
	public void readEntry(ByteBuf buffer) {
		value = ByteBufUtils.readUTF8String(buffer);	
	}

}
