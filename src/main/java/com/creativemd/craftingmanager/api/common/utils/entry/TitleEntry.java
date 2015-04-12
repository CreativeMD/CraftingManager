package com.creativemd.craftingmanager.api.common.utils.entry;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ConfigEntry;

public abstract class TitleEntry extends ConfigEntry{
	
	public String Title;
	
	public TitleEntry(String name)
	{
		this.Title = name;
	}
	
	@Override
	public int getHeight() {
		return 30;
	}
	
	@Override
	public ArrayList<SlotInfo> getSlots(EntityPlayer player) {
		return new ArrayList<SlotInfo>();
	}
	
	@Override
	public ArrayList<GuiInput> getTextFields(FontRenderer font) {
		return null;
	}
	
	@Override
	public void drawEntry(FontRenderer renderer, int X, int Y) {
		renderer.drawString(Title, X, Y+getHeight()/2-renderer.FONT_HEIGHT/2, 0);
	}
	
	@Override
	public boolean canBeFound(String search) {
		return Title.toLowerCase().contains(search.toLowerCase());
	}
	
}
