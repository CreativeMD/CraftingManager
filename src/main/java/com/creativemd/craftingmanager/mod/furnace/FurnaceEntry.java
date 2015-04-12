package com.creativemd.craftingmanager.mod.furnace;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.utils.server.SlotInput;

public abstract class FurnaceEntry extends ConfigEntry{

	@Override
	public int getHeight() {
		return 20;
	}

	@Override
	public ArrayList<SlotInfo> getSlots(EntityPlayer player) {
		int x = 40;
		int y = 4;
		ArrayList<SlotInfo> slots = new ArrayList<SlotInfo>();
		slots.add(new SlotInfo(0, x, y));
		slots.add(new SlotInfo(0, x+30, y));
		return slots;
	}
	
	public void loadRecipe(FurnaceRecipe recipe, int showState)
	{
		if(showState == 0)
		{
			slots.get(0).putStack(recipe.getInput());
			if(slots.get(0) instanceof SlotInput)
				((SlotInput)slots.get(0)).info = ItemInfo.getInfo(recipe.input);
		}
		if(showState != 2)
			slots.get(1).putStack(recipe.output);
	}

}
