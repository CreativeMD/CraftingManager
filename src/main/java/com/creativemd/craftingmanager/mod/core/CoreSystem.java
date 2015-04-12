package com.creativemd.craftingmanager.mod.core;

import java.util.ArrayList;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.config.Configuration;

import com.creativemd.craftingmanager.api.common.utils.entry.IntegerEntry;
import com.creativemd.craftingmanager.api.common.utils.entry.StateEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.EventHandler;
import com.creativemd.craftingmanager.mod.utils.packets.RecievePacket;
import com.creativemd.craftingmanager.mod.utils.packets.SystemPacket;

public class CoreSystem extends ConfigSystem{

	public CoreSystem() {
		super("Core Settings", CraftingManagerMod.vanillaTab);
	}

	@Override
	public void loadSystem() {
		RecievePacket.mode = 0;
	}

	@Override
	public void loadConfig(Configuration config) {
		RecievePacket.mode = config.get("core", "RecieveMessage", 0).getInt();
		SystemPacket.maxEntriesInPacket = config.get("core", "maxEntriesInPacket", 30).getInt();
		EventHandler.maxPacketsPerTick = config.get("core", "maxPacketsPerTick", 10).getInt();
	}

	@Override
	public void saveConfig(Configuration config) {
		config.get("core", "RecieveMessage", 0).set(RecievePacket.mode);
		config.get("core", "maxEntriesInPacket", 30).set(SystemPacket.maxEntriesInPacket);
		config.get("core", "maxPacketsPerTick", 10).set(EventHandler.maxPacketsPerTick);
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		entries.add(new StateEntry("Recieve Message", RecievePacket.mode, "All Information", "Notification", "None"));
		entries.add(new IntegerEntry("Max Entries In Packet", SystemPacket.maxEntriesInPacket));
		entries.add(new IntegerEntry("Max Packets per Tick", EventHandler.maxPacketsPerTick));
		return entries;
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		if(entry instanceof StateEntry)
			RecievePacket.mode = ((StateEntry) entry).getState();
		else if(((IntegerEntry) entry).Title.equals("Max Entries In Packet")){
			if(((IntegerEntry) entry).value > 0)
				SystemPacket.maxEntriesInPacket = ((IntegerEntry) entry).value;
		}else
			if(((IntegerEntry) entry).value > 0)
				EventHandler.maxPacketsPerTick = ((IntegerEntry) entry).value;
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		entries.add(new IntegerPacketEntry(RecievePacket.mode));
		if(SystemPacket.maxEntriesInPacket < 1)
			SystemPacket.maxEntriesInPacket = 1;
		entries.add(new IntegerPacketEntry(SystemPacket.maxEntriesInPacket));
		if(EventHandler.maxPacketsPerTick < 1)
			EventHandler.maxPacketsPerTick = 1;
		entries.add(new IntegerPacketEntry(EventHandler.maxPacketsPerTick));
		return entries;
	}
	
	public void UpdateInformation(ArrayList<PacketEntry> Packet)
	{
		RecievePacket.mode = ((IntegerPacketEntry)Packet.get(0)).value;
		SystemPacket.maxEntriesInPacket = ((IntegerPacketEntry)Packet.get(1)).value;
		EventHandler.maxPacketsPerTick = ((IntegerPacketEntry)Packet.get(2)).value;
	}

	@Override
	public String getRecieveInformation() {
		return "";
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}

}
