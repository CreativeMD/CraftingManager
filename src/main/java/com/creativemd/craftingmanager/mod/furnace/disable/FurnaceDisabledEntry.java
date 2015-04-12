package com.creativemd.craftingmanager.mod.furnace.disable;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.furnace.FurnaceEntry;
import com.creativemd.craftingmanager.mod.furnace.FurnaceRecipe;
import com.creativemd.craftingmanager.mod.furnace.disable.DisableFurnace;
import com.creativemd.craftingmanager.mod.workbench.disable.DisableSystem;

public class FurnaceDisabledEntry extends FurnaceEntry{
	
	public FurnaceRecipe recipe;
	
	public FurnaceDisabledEntry(FurnaceRecipe recipe)
	{
		this.recipe = recipe;
	}
	
	@Override
	public void loadInformation(EntityPlayer player, int state) {
		loadRecipe(recipe, state);
		updateButtons();
	}
	
	public void updateButtons()
	{
		if(buttons.size() > 0)
		{
			if(DisableFurnace.disabledRecipes.contains(recipe))
				((GuiButtonState)buttons.get(0)).setIndex(1);
			else
				((GuiButtonState)buttons.get(0)).setIndex(0);
		}
	}

	@Override
	public ArrayList<GuiButton> getButtons() {
		ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButtonState(0, getWith()-150, getHeight()/2-8, 70, 20, "Enabled", "Disabled"));
		return buttons;
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		ArrayList<GuiInput> buttons = new ArrayList<GuiInput>();
		return buttons;
	}

	@Override
	public void drawEntry(FontRenderer renderer, int X, int Y) {
		
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {
		if(button instanceof GuiButtonState)
		{	
			if(!CraftingManagerMod.forbiddenOutputs.isObjectValid(recipe.output))
			{
				((GuiButtonState) button).setIndex(0);
				return ;
			}
			if(((GuiButtonState) button).getIndex() == 0)
				DisableFurnace.disabledRecipes.remove(recipe);
			else if(!DisableFurnace.disabledRecipes.contains(recipe))
				DisableFurnace.disabledRecipes.add(recipe);				
		}
	}

	@Override
	public boolean canBeFound(String search) {
		search = search.toLowerCase();
		
		if("disabled".contains(search) && DisableFurnace.disabledRecipes.contains(recipe))
			return true;
		
		if("enabled".contains(search) && !DisableFurnace.disabledRecipes.contains(recipe))
			return true;
		
		return FurnaceRecipe.RecipeToString(recipe).toLowerCase().contains(search);
	}

}
