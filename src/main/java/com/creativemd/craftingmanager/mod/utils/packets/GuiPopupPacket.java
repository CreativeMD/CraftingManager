package com.creativemd.craftingmanager.mod.utils.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;

import net.minecraft.entity.player.EntityPlayer;

public class GuiPopupPacket extends CreativeCorePacket {
	
	public boolean popup;
	
	public GuiPopupPacket(boolean popup)
	{
		this.popup = popup;
	}
	
	public GuiPopupPacket()
	{
		popup = false;
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(player.openContainer instanceof ContainerConfig)
			((ContainerConfig) player.openContainer).popup = popup;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeBoolean(popup);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		popup = buf.readBoolean();
	}

}
