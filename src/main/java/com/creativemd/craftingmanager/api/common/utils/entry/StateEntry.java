package com.creativemd.craftingmanager.api.common.utils.entry;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.core.ConfigEntry;

public class StateEntry extends TitleEntry{
	
	
	public String[] states;
	public int index;
	
	public StateEntry(String name, int index, String... states)
	{
		super(name);
		this.states = states;
		this.index = index;
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {
		onValueChange();
	}
	
	@Override
	public ArrayList<GuiButton> getButtons() {
		ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButtonState(0, getWith()-100, getHeight()/2-10, 100, 20, states));
		return buttons;
	}
	
	@Override
	public void loadInformation(EntityPlayer player, int state) {
		if(buttons.size() > 0)
			((GuiButtonState)buttons.get(0)).setIndex(loadState());
	}
	
	@Override
	public boolean canBeFound(String search) {
		if(search.equalsIgnoreCase(""))
			return true;
		for (int i = 0; i < states.length; i++) {
			if(states[i].toLowerCase().contains(search.toLowerCase()))
				return true;
		}
		return super.canBeFound(search);
	}
	
	public int getState()
	{
		return ((GuiButtonState)buttons.get(0)).getIndex();
	}
	
	public int loadState()
	{
		return index;
	}
	
	public void onValueChange(){}
}
