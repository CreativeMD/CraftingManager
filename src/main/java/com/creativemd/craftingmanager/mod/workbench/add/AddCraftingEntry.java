package com.creativemd.craftingmanager.mod.workbench.add;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.entry.CraftingRecipeEntry;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;
import com.creativemd.craftingmanager.mod.utils.server.SlotInput;
import com.creativemd.craftingmanager.mod.workbench.WorkbenchHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AddCraftingEntry extends CraftingRecipeEntry{
	
	public IRecipe recipe;
	public static final String[] modes = {"ShapedOre", "ShapelessOre"};
	
	public AddCraftingEntry(IRecipe recipe)
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
		for (int i = 0; i < 10; i++) {
			if(slots.get(i) instanceof SlotInput)
				((SlotInput)slots.get(i)).info = null;
			if(recipe instanceof BetterShapedRecipe && i < ((BetterShapedRecipe) recipe).itemData.length)
				((SlotInput)slots.get(i)).info = ((BetterShapedRecipe) recipe).itemData[i];
			if(recipe instanceof BetterShapelessRecipe && i < ((BetterShapelessRecipe) recipe).itemData.size())
				((SlotInput)slots.get(i)).info = ((BetterShapelessRecipe) recipe).itemData.get(i);
		}
		int index = 0;
		if(recipe instanceof BetterShapelessRecipe)
			index = 1;
		if(buttons.size() >= 3)
		{
			buttons.get(0).enabled = false;
			((GuiButtonState)buttons.get(2)).setIndex(index);
		}
	}

	@Override
	public ArrayList<GuiButton> getButtons() {
		ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButton(0, 140, 0, 60, 20, "Save"));
		buttons.add(new GuiButton(1, 140, 20, 60, 20, "Remove"));
		buttons.add(new GuiButtonState(2, 100, 40, 100, 20, modes));
		return buttons;
	}
	
	@Override
	public int getMoveY()
	{
		return (int) (getHeight()/4 - Math.round(SlotInfo.slotSize*1.5D));
	}

	@Override
	public void drawEntry(FontRenderer renderer, int X, int Y) {
		
	}
	
	public boolean isColNeeded(int id)
	{
		return slots.get(id).getHasStack() | slots.get(id+3).getHasStack() | slots.get(id+6).getHasStack();			
	}
	
	public boolean isRowNeeded(int id)
	{
		return slots.get(id*3).getHasStack() | slots.get(id*3+1).getHasStack() | slots.get(id*3+2).getHasStack();					
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {
		switch(index)
		{
		case 0:
			boolean stack = false;
			for (int i = 0; i < 9; i++) {
				if(slots.get(i).getHasStack())
					stack = true;
			}
			if(!slots.get(9).getHasStack() || !stack || !CraftingManagerMod.forbiddenOutputs.isObjectValid(slots.get(9).getStack()))
				return ;
			
			ItemInfo[] stacks = new ItemInfo[9];
			for (int i = 0; i < stacks.length; i++) {
				stacks[i] = ((SlotInput)slots.get(i)).info;
			}
			
			String typ = buttons.get(2).displayString;
			
			IRecipe newRecipe = WorkbenchHelper.getRecipeFromGrid(slots.get(9).getStack(), stacks, typ.equals("ShapedOre"));
			if(newRecipe != null)
			{
				if(recipe == null)
					AddedSystem.addedRecipes.add(newRecipe);
				else
					AddedSystem.addedRecipes.set(AddedSystem.addedRecipes.indexOf(recipe), newRecipe);
				CraftingManagerMod.showGui(null, CraftingManagerMod.addedSystem.getID(), true, getPage());
			}
			break;
		case 1:
			//Remove
			if(AddedSystem.addedRecipes.contains(recipe))
				AddedSystem.addedRecipes.remove(recipe);
			CraftingManagerMod.showGui(null, CraftingManagerMod.addedSystem.getID(), true, getPage());
			break;
		case 2:
			buttons.get(0).enabled = true;
			break;
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
	public ArrayList<SlotInfo> getSlots(EntityPlayer player) {
		ArrayList<SlotInfo> slots = super.getSlots(player);
		for (int i = 0; i < slots.size(); i++) {
			if(i == 9)
				slots.get(i).type = 2;
			else
				slots.get(i).type = 1;
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
	
	@Override
	public boolean canBeFound(String search)
	{
		if(recipe != null)
			return WorkbenchHelper.canRecipeBeFound(recipe, search);
		return true;
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		return new ArrayList<GuiInput>();
	}

}
