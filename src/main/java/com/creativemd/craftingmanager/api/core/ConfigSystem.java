package com.creativemd.craftingmanager.api.core;

import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;

import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;

public abstract class ConfigSystem {
	
	private int id;
	public String name;
	
	public ConfigTab tab;
	
	public ConfigSystem(String name, ConfigTab tab)
	{
		this.name = name;
		this.tab = tab;
		if(!ConfigTab.tabs.contains(tab))
			ConfigTab.tabs.add(tab);
	}
	
	public void setID(int id)
	{
		 this.id = id;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	/**Used for example: getting all recipes**/
	public abstract void loadSystem();
	
	public abstract void loadConfig(Configuration config);
	
	public abstract void saveConfig(Configuration config);
	
	/**If the gui contains an Input Search field**/
	public boolean hasInputField()
	{
		return true;
	}
	
	public abstract ArrayList<ConfigEntry> getEntries();
	
	public abstract void onEntryChange(ConfigEntry entry);
	
	public abstract ArrayList<PacketEntry> getPacketInformation();
	
	/**An example would be: "Disabling X CraftingRecipes""**/
	public abstract String getRecieveInformation();
	
	/**If the packet has to be send to every client (Login/Changes)**/
	public abstract boolean needClientUpdate();
	
	public void PreUpdateInformation(ArrayList<PacketEntry> Packet){}
	
	public void UpdateInformation(ArrayList<PacketEntry> Packet){}
	
	public void PostUpdateInformation(ArrayList<PacketEntry> Packet){}
	
}
