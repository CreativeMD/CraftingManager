package com.creativemd.craftingmanager.mod.utils.packets;

import io.netty.buffer.ByteBuf;

import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

public class RecievePacket extends CreativeCorePacket{
	
	/**mode 0: All Information; 1: Notification; 2: None**/
	public static int mode;
	
	public RecievePacket()
	{
		super();
	}

	@Override
	public void executeClient(EntityPlayer player) {
		String message = "";
		if(mode == 0)
		{
			message = "CraftingManager: [";
			for (int i = 0; i < ConfigRegistry.systems.size(); i++) {
				String information = ConfigRegistry.getConfigFromID(i).getRecieveInformation();
				message += information;
				if(i < ConfigRegistry.systems.size()-1 && !information.equals(""))
					message += ",";
			}
			message += "]";
		}
		if(mode == 1)
			message = "Recieved new Configuration";
		
		if(!message.equals(""))
			player.addChatComponentMessage(new ChatComponentTranslation(message));
	}

	@Override
	public void executeServer(EntityPlayer player) {
		CraftingManagerMod.save();
		PacketHandler.sendPacketToAllPlayers(new RecievePacket());
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		
	}

	@Override
	public void readBytes(ByteBuf buf) {
		
	}
}
