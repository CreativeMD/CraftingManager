package com.creativemd.craftingmanager.mod.utils.server;

import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotNormal extends Slot{
	
	public ContainerConfig container;
	public GuiConfig gui;
	public ConfigEntry entry;
		
	public SlotNormal(ConfigEntry entry, ContainerConfig container, IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		this.container = container;
		if(container.gui != null)
			gui = container.gui;
		this.entry = entry;
	}
	
	public void onSlotChanged()
    {
        super.onSlotChanged();
        entry.onSlotChange(inventory, getSlotIndex());
        container.system.onEntryChange(entry);
    }
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return !container.popup;
    }
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		return !container.popup;
    }

}
