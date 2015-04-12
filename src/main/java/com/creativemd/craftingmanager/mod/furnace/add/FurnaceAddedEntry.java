package com.creativemd.craftingmanager.mod.furnace.add;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.furnace.FurnaceEntry;
import com.creativemd.craftingmanager.mod.furnace.FurnaceRecipe;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;
import com.creativemd.craftingmanager.mod.utils.server.SlotInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FurnaceAddedEntry extends FurnaceEntry{
	
	public FurnaceRecipe recipe;
	
	public FurnaceAddedEntry(FurnaceRecipe recipe)
	{
		this.recipe = recipe;
	}
	
	@Override
	public int getHeight() {
		return 120;
	}
	
	@Override
	public void loadInformation(EntityPlayer player, int state) {
		if(recipe != null)
			loadRecipe(recipe, state);
		else
			if(slots.get(0) instanceof SlotInput)
				((SlotInput)slots.get(0)).info = null;
		if(buttons.size() > 0)
		{
			buttons.get(0).enabled = false;
			buttons.get(1).enabled = recipe != null;
		}
	}

	@Override
	public ArrayList<GuiButton> getButtons() {
		ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButton(0, 100, 2, 60, 20, "Save"));
		buttons.add(new GuiButton(1, 170, 2, 60, 20, "Remove"));
		return buttons;
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		return new ArrayList<GuiInput>();
	}

	@Override
	public void drawEntry(FontRenderer renderer, int X, int Y) {
		
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {
		switch(index)
		{
		case 0:
			if(slots.get(0).getHasStack() && slots.get(1).getHasStack())
			{
				ItemInfo info = ((SlotInput)slots.get(0)).info;
				Object input = info.ore;
				if(!info.isOre())
				{
					if(info.needDamage() || info.needNBT())
					{
						input = info.getItemStack();
					}else{
						if(info.isBlock())
							input = info.getBlock();
						else
							input = info.getItem();
					}
				}
				if(input != null && CraftingManagerMod.forbiddenOutputs.isObjectValid(slots.get(1).getStack()))
				{
					FurnaceRecipe newrecipe = new FurnaceRecipe(input, slots.get(1).getStack(), 1);
					if(recipe == null)
						AddFurnace.addedRecipes.add(newrecipe);
					else
						AddFurnace.addedRecipes.set(AddFurnace.addedRecipes.indexOf(recipe), newrecipe);
					CraftingManagerMod.showGui(null, CraftingManagerMod.addFurnace.getID(), true, getPage());
				}
			}
			break;
		case 1:
			if(recipe != null)
			{
				AddFurnace.addedRecipes.remove(recipe);
				CraftingManagerMod.showGui(null, CraftingManagerMod.addFurnace.getID(), true, getPage());
			}
			break;
		}
	}
	
	@Override
	public ArrayList<SlotInfo> getSlots(EntityPlayer player) {
		ArrayList<SlotInfo> slots = super.getSlots(player);
		if(slots.size() == 2)
		{
			slots.get(0).type = 1;
			slots.get(1).type = 2;
		}
		slots.addAll(getPlayerInventorySlots(player, 37, 65));
		return slots;
	}
	
	@Override
	public void onSlotChange(IInventory inventory, int index)
	{
		if (!(inventory instanceof InventoryPlayer) && buttons.size() > 0)
		{
			buttons.get(0).enabled = true;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static int getPage()
	{
		if(Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerConfig)
			return ((ContainerConfig)Minecraft.getMinecraft().thePlayer.openContainer).getPage();
		return 0;
	}

	@Override
	public boolean canBeFound(String search) {
		if(recipe != null)
			return FurnaceRecipe.RecipeToString(recipe).toLowerCase().contains(search);
		return true;
	}

}
