package com.creativemd.craftingmanager.api.common.utils.packet;

import io.netty.buffer.ByteBuf;

public class BooleanPacketEntry extends PacketEntry{
	
	public boolean value;
	
	public BooleanPacketEntry(boolean input)
	{
		this.value = input;
	}
	
	public BooleanPacketEntry()
	{
		this.value = false;
	}
	
	@Override
	public void writeEntry(ByteBuf buffer) {
		buffer.writeBoolean(value);
	}

	@Override
	public void readEntry(ByteBuf buffer) {
		value = buffer.readBoolean();
	}

}
