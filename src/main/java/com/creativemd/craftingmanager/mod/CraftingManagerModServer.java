package com.creativemd.craftingmanager.mod;

import com.creativemd.craftingmanager.mod.utils.GuiHandler;

import cpw.mods.fml.common.network.NetworkRegistry;

public class CraftingManagerModServer {

	public void loadSide()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(CraftingManagerMod.modid, new GuiHandler());
	}
}
