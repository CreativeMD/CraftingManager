package com.creativemd.craftingmanager.mod.utils.server;

import com.creativemd.craftingmanager.api.core.ConfigEntry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends SlotNormal{

	public SlotOutput(ConfigEntry entry, ContainerConfig container, IInventory par1iInventory, int par2, int par3, int par4) {
		super(entry, container, par1iInventory, par2, par3, par4);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		inventory.setInventorySlotContents(getSlotIndex(), par1ItemStack.copy());
		return false;
    }
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
		inventory.setInventorySlotContents(getSlotIndex(), null);
        return false;
    }
	
}
