package com.creativemd.craftingmanager.mod.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return new ContainerConfig(player, ID);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return new GuiConfig(player, ID);
	}

}
