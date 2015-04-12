package com.creativemd.craftingmanager.mod.utils.server;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerConfig extends Container{
	
	public ConfigSystem system;
	public EntityPlayer player;
	public ArrayList<ConfigEntry> entryList;
	public ArrayList<ConfigEntry> foundList;
	public ArrayList<ConfigEntry> currentList;
	
	public int pages;
	private int page;
	public int entryIndex;
	
	/** 0: everything is shown; 1: only output is shown; 2: nothing is shown**/
	public static int showState = 0;
	
	public GuiConfig gui;
	public boolean popup = false;
	
	public static int pageStart = 24;
	
	public String search = "";
	
	public int tabIndex;
	public ArrayList<ConfigSystem> tabConfigs = new ArrayList<ConfigSystem>();
	
	public ContainerConfig(EntityPlayer player, int id)
	{
		this.player = player;
		if(id < ConfigRegistry.systems.size())
		{
			system = ConfigRegistry.getConfigFromID(id);
			tabIndex = ConfigTab.getIndexOfTab(system.tab);
		}else{
			tabIndex = id - ConfigRegistry.systems.size();
			system = null;
		}
		
		tabConfigs.addAll(ConfigRegistry.getConfigsFromTab(ConfigTab.tabs.get(tabIndex)));
		
		if(system == null && tabConfigs.size() > 0)
			system = tabConfigs.get(0);
		
		currentList = new ArrayList<ConfigEntry>();
		foundList = new ArrayList<ConfigEntry>();
		if(system != null)
			entryList = system.getEntries();
		else
			entryList = new ArrayList<ConfigEntry>();
		entryIndex = 0;
		page = 0;
		pages = getPages();
		updateSearchResult();
		updateSlots(page);
	}
	
	public void updateSearchResult()
	{
		foundList.clear();
		for (int i = 0; i < entryList.size(); i++)
		{
			if(entryList.get(i).canBeFound(search))
				foundList.add(entryList.get(i));
		}
		pages = getPages();
		setPage(page);
	}
	
	/**Page can also be below 0 and higher than pages because they are getting corrected**/
	public void setPage(int page)
	{
		if(page < 0)
			page = pages-1;
		if(page >= pages)
			page = 0;
		this.page = page;
		ArrayList<ConfigEntry> until = ConfigEntry.getEntriesOnPage(0, foundList, page);
		entryIndex = until.size();
		currentList.clear();
		currentList.addAll(ConfigEntry.getEntriesOnPage(entryIndex, foundList));
		updateSlots(page);
	}
	
	public int getPage()
	{
		return page;
	}
	
	public int getPages()
	{
		return ConfigEntry.getPages(foundList);
	}
	
	public void updateSlots(int page)
	{
		inventorySlots.clear();
		int height = pageStart;
		for(int i = 0; i < currentList.size(); i++)
		{
			currentList.get(i).slots.clear();
			ArrayList<SlotInfo> slots = currentList.get(i).getSlots(player);
			if(slots != null)
			{
				InventoryBasic inventory = new InventoryBasic(system.name, false, slots.size());
				for(int q = 0; q < slots.size(); q++)
				{
					SlotNormal slot = null;
					int posX = ConfigEntry.getX()+slots.get(q).posX;
					int posY = height+slots.get(q).posY;
					int index = q;
					if(slots.get(q).index != -1)
						index = slots.get(q).index;
					IInventory tempII = inventory;
					if(slots.get(q).inventory != null)
						tempII = slots.get(q).inventory;
					switch(slots.get(q).type)
					{
					case 0:
						slot = new SlotShow(currentList.get(i), this, tempII, index, posX, posY);
						break;
					case 1:
						slot = new SlotInput(currentList.get(i), this, tempII, index, posX, posY);
						break;
					case 2:
						slot = new SlotOutput(currentList.get(i), this, tempII, index, posX, posY);
						break;
					default:
						System.out.println("Found an invalid slot type [type=" + slots.get(q).type + "]");
					case 3:
						slot = new SlotNormal(currentList.get(i), this, tempII, index, posX, posY);
						break;
					}
					currentList.get(i).slots.add(slot);
					addSlotToContainer(slot);
				}
			}
			height += currentList.get(i).getHeight() + ConfigEntry.space;
			ConfigEntry.isLoading = true;
			currentList.get(i).loadInformation(player, showState);
			ConfigEntry.isLoading = false;
			
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        return null;
    }
	
	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {
		if(!popup)
			return super.slotClick(par1, par2, par3, par4EntityPlayer);
		return null;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack)
    {
		for (int i = 0; i < par1ArrayOfItemStack.length; ++i)
        {
			if(getSlot(i).inventory instanceof EntityPlayer)
				this.getSlot(i).putStack(par1ArrayOfItemStack[i]);
        }
    }
}
