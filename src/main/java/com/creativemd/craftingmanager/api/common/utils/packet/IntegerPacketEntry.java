package com.creativemd.craftingmanager.api.common.utils.packet;

import io.netty.buffer.ByteBuf;

public class IntegerPacketEntry extends PacketEntry{
	
	public int value;
	
	public IntegerPacketEntry(int input)
	{
		this.value = input;
	}
	
	public IntegerPacketEntry()
	{
		this.value = 0;
	}
	
	@Override
	public void writeEntry(ByteBuf buffer) {
		buffer.writeInt(value);
	}

	@Override
	public void readEntry(ByteBuf buffer) {
		value = buffer.readInt();
	}

}
