package com.creativemd.craftingmanager.mod.utils.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import com.creativemd.craftingmanager.api.common.utils.packet.BooleanPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;

import net.minecraft.entity.player.EntityPlayer;

public class GuiPacket extends CreativeCorePacket{
	
	public int id;
	public int page;
	
	public GuiPacket(int id, int page)
	{
		this.id = id;
		this.page = page;
	}
	
	public GuiPacket()
	{
		super();
	}

	@Override
	public void executeClient(EntityPlayer player) {
		if(player.openContainer instanceof ContainerConfig && ((ContainerConfig)player.openContainer).system.getID() == id)
			 ((ContainerConfig)player.openContainer).setPage(page);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		CraftingManagerMod.showGui(player, id, false, page);
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(page);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		id = buf.readInt();
		page = buf.readInt();
	}

}
