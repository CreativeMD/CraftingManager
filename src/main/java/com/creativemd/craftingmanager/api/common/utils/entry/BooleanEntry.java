package com.creativemd.craftingmanager.api.common.utils.entry;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ConfigEntry;

public class BooleanEntry extends StateEntry{
	
	public boolean value;
	
	public BooleanEntry(String name, boolean value) {
		super(name, 0, "Enabled", "Disabled");
		this.value = value;
	}

	@Override
	public int loadState() {
		if(value)
			return 0;
		return 1;
	}

	@Override
	public void onValueChange() {
		value = getState() == 0;
	}
	
}