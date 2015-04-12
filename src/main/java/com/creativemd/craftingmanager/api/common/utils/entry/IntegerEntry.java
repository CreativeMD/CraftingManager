package com.creativemd.craftingmanager.api.common.utils.entry;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ConfigEntry;

public class IntegerEntry extends TitleEntry{
	
	public int value;
	
	public IntegerEntry(String name, int value) {
		super(name);
		this.value = value;
	}

	@Override
	public void loadInformation(EntityPlayer player, int state) {
		if(inputs.size() > 0)
		{
			inputs.get(0).setText(Integer.toString(value));
		}
	}

	@Override
	public ArrayList<GuiButton> getButtons() {
		return new ArrayList<GuiButton>();
	}
	
	public void onInputChange(GuiInput input)
	{
		try{
			Integer.parseInt(input.getText());
		}catch(Exception e){
			return ;	
		}
		value = Integer.parseInt(input.getText());
	}

	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		ArrayList<GuiInput> inputs = new ArrayList<GuiInput>();
		GuiInput input = new GuiInput(font, getWith()-100, getHeight()/2-10, 20, 100);
		input.onlyNumbers = true;
		inputs.add(input);
		return inputs;
	}

	@Override
	public void buttonClicked(int index, GuiButton button) {}
}
