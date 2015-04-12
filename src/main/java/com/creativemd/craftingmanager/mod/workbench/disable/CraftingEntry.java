package com.creativemd.craftingmanager.mod.workbench.disable;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;

import com.creativemd.craftingmanager.api.common.utils.entry.CraftingRecipeEntry;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.workbench.WorkbenchHelper;
import com.creativemd.craftingmanager.mod.workbench.disable.DisableSystem;

public class CraftingEntry extends CraftingRecipeEntry{
	
	public IRecipe recipe;
	
	public CraftingEntry(IRecipe recipe)
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
			if(DisableSystem.isRecipeDisabled(recipe))
				((GuiButtonState)buttons.get(0)).setIndex(1);
			else
				((GuiButtonState)buttons.get(0)).setIndex(0);
		}
	}

	@Override
	public ArrayList<GuiButton> getButtons() {
		ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButtonState(0, getWith()-100, getHeight()/2-10, 70, 20, "Enabled", "Disabled"));
		updateButtons();
		return buttons;
	}

	@Override
	public void drawEntry(FontRenderer renderer, int X, int Y) {
		renderer.drawString(recipe.getClass().getSimpleName(), X+110, Y+10, 0);
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {
		if(!CraftingManagerMod.forbiddenOutputs.isObjectValid(recipe.getRecipeOutput()))
		{
			((GuiButtonState)buttons.get(0)).setIndex(0);
			return ;
		}
		int state = ((GuiButtonState)buttons.get(0)).getIndex();
		if(state == 0)
		{
			int i = 0;
			while(i < DisableSystem.disableRecipes.size())
			{
				if(WorkbenchHelper.getRecipeInfo(DisableSystem.disableRecipes.get(i)).equals(WorkbenchHelper.getRecipeInfo(recipe)))
					DisableSystem.disableRecipes.remove(i);
				else
					i++;
			}
		}
		else
			if(!DisableSystem.disableRecipes.contains(recipe))
				DisableSystem.disableRecipes.add(recipe);
	}

	@Override
	public boolean canBeFound(String search) {
		return WorkbenchHelper.canRecipeBeFound(recipe, search);
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		return null;
	}

}
