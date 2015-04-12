package com.creativemd.craftingmanager.mod.utils.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;

public class SlotInput extends SlotNormal{
	
	public ItemInfo info = null;
	
	public SlotInput(ConfigEntry entry, ContainerConfig container, IInventory par1iInventory, int par2, int par3, int par4) {
		super(entry, container, par1iInventory, par2, par3, par4);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
		if(gui != null)
			gui.popUp(getSlotIndex(), getStack().copy());
        return super.canTakeStack(par1EntityPlayer);
    }
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		if(gui != null)
			gui.popUp(getSlotIndex(), par1ItemStack.copy());
		return super.isItemValid(par1ItemStack);
    }
	
}
