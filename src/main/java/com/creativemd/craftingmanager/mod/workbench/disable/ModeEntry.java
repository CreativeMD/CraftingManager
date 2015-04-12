package com.creativemd.craftingmanager.mod.workbench.disable;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;

import com.creativemd.craftingmanager.api.common.utils.entry.StateEntry;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;

public class ModeEntry extends StateEntry {

	public ModeEntry() {
		super("General disables", 0, "None", "Only Vanilla", "Only Added Recipes", "All");
	}

	@Override
	public int loadState() {
		return DisableSystem.recipeMode;
	}

	@Override
	public void onValueChange() {
		DisableSystem.recipeMode = getState();
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		return null;
	}

}
