package com.creativemd.craftingmanager.mod.utils.server;

import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotShow extends SlotNormal {
	
	public SlotShow(ConfigEntry entry, ContainerConfig container, IInventory par1iInventory, int par2, int par3, int par4) {
		super(entry, container, par1iInventory, par2, par3, par4);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		return false;
    }
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return false;
    }
}
