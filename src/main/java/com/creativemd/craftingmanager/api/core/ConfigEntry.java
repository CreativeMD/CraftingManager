package com.creativemd.craftingmanager.api.core;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ConfigEntry {
	
	public static final int pageHeight = 140;
	public static final int space = 2;
	
	/**If loadInformation method is running**/
	public static boolean isLoading = false;
	
	public static int getWith()
	{
		return 240;
	}
	
	public static int getX()
	{
		return 10;
	}
	
	/**Use getSlots to fill them (contains SlotNormal)**/
	public ArrayList<Slot> slots = new ArrayList<Slot>();
	
	/**Use getButtons to fill them**/
	public ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
	
	public ArrayList<GuiInput> inputs = new ArrayList<GuiInput>();
	
	/**NOTE: The height cannot be higher than ContainerConfig.pageHeight**/
	public abstract int getHeight();
	
	/**index of this configEntry, state(Slot) = 0: everything is shown; 1: only output is shown; 2: nothing is shown**/
	public abstract void loadInformation(EntityPlayer player, int state);
	
	public abstract ArrayList<SlotInfo> getSlots(EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	public abstract ArrayList<GuiButton> getButtons();
	
	@SideOnly(Side.CLIENT)
	public abstract ArrayList<GuiInput> getTextFields(FontRenderer font);
	
	@SideOnly(Side.CLIENT)
	public abstract void drawEntry(FontRenderer renderer, int X, int Y);
	
	@SideOnly(Side.CLIENT)
	public abstract void buttonClicked(int index, GuiButton button);
	
	public abstract boolean canBeFound(String search);
	
	/**Don't called while loadInformation**/
	public void onSlotChange(IInventory inventory, int index) {}
	
	public void onInputChange(GuiInput input){}
	
	public static int getHeight(ArrayList<ConfigEntry> entries)
	{
		int height = 0;
		for(int i = 0; i < entries.size(); i++)
			height += entries.get(i).getHeight();
		return height;
	}
	
	public static int getPages(ArrayList<ConfigEntry> entries)
	{
		ArrayList<ConfigEntry> entry = (ArrayList<ConfigEntry>) entries.clone();
		int i = 0;
		int height = 0;
		int page = 1;
		while(height <= pageHeight && entry.size() > 0)
		{
			height += entries.get(i).getHeight();
			if(height <= pageHeight)
			{
				entry.remove(0);
				i++;
			}else{
				height = 0;
				page++;
			}
		}
		return page;
	}
	
	public static ArrayList<SlotInfo> getPlayerInventorySlots(EntityPlayer player, int X, int Y)
	{
		ArrayList<SlotInfo> slots = new ArrayList<SlotInfo>();
		int l;
        for (l = 0; l < 3; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
            	SlotInfo info = new SlotInfo(3, i1 * 18 + X, l * 18+Y);
            	info.index = i1 + l * 9 + 9;
            	info.inventory = player.inventory;
            	slots.add(info);
            }
        }

        for (l = 0; l < 9; ++l)
        {
        	SlotInfo info = new SlotInfo(3, l * 18+X, 58+Y);
        	info.index = l;
        	info.inventory = player.inventory;
        	slots.add(info);
        }
        return slots;
	}
	
	public static ArrayList<ConfigEntry> getEntriesOnPage(int index, ArrayList<ConfigEntry> entries)
	{
		return getEntriesOnPage(index, entries, 1);
	}
	
	public static ArrayList<ConfigEntry> getEntriesOnPage(int index, ArrayList<ConfigEntry> entries, int pages)
	{
		ArrayList<ConfigEntry> result = new ArrayList<ConfigEntry>();
		int i = index;
		for (int page = 0; page < pages; page++)
		{
			int height = 0;
			while(height <= pageHeight && i < entries.size())
			{
				height += entries.get(i).getHeight()+space;
				if(height <= pageHeight)
				{
					result.add(entries.get(i));
					i++;
				}
			}
			
		}
		return result;
	}
}
