package com.creativemd.craftingmanager.mod.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;
import com.creativemd.craftingmanager.mod.utils.packets.SystemPacket;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommandGUI implements ICommand {

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "CraftingManager";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return null;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		EntityPlayer player = null;
		for (int i = 0; i < players.size(); i++) {
			if(((EntityPlayer)players.get(i)).getCommandSenderName().equals(icommandsender.getCommandSenderName()))
				player = (EntityPlayer) players.get(i);
		}
		
		for (int j = 0; j < ConfigRegistry.systems.size(); j++) {
			if(!ConfigRegistry.systems.get(j).needClientUpdate())
			{
				ArrayList<CreativeCorePacket> packets = SystemPacket.getPackets(j);
				for (int i = 0; i < packets.size(); i++) {
					PacketHandler.sendPacketToPlayer(packets.get(i), (EntityPlayerMP) player);
				}
				
			}
		}
		CraftingManagerMod.showGui(player, 0, true);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (int i = 0; i < players.size(); i++) {
			if(((EntityPlayerMP)players.get(i)).getCommandSenderName().equals(icommandsender.getCommandSenderName()))
				return ((EntityPlayerMP)players.get(i)).canCommandSenderUseCommand(1, "CraftingManager");
		}
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

}
