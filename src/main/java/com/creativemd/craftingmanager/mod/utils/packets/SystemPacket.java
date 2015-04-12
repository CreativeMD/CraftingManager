package com.creativemd.craftingmanager.mod.utils.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SystemPacket extends CreativeCorePacket{
	
	public static HashMap<Integer, ArrayList<SystemPacket>> packets = new HashMap<Integer, ArrayList<SystemPacket>>();
	
	public ArrayList<PacketEntry> entries;
	
	public int systemID;
	public int index;
	
	public boolean isFirst;
	public boolean isLast;
	
	public SystemPacket()
	{
		super();
		this.systemID = -1;
		this.entries = new ArrayList<PacketEntry>();
		this.isLast = false;
	}
	
	public SystemPacket(int id, ArrayList<PacketEntry> entries, boolean isFirst, boolean isLast)
	{
		this.entries = entries;
		this.isFirst = isFirst;
		this.isLast = isLast;
		this.systemID = id;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void executeClient(EntityPlayer player)
    {
    	
    }
    
	@Override
    public void executeServer(EntityPlayer player)
    {
    	if(isLast && ConfigRegistry.getConfigFromID(this.systemID).needClientUpdate())
    	{
    		CraftingManagerMod.sendUpdateToAll(systemID);
    	}
    }
    
    public static int maxEntriesInPacket = 30;
    
    public static ArrayList<CreativeCorePacket> getPackets(int id) {
    	ArrayList<CreativeCorePacket> packets = new ArrayList<CreativeCorePacket>();
    	ArrayList<PacketEntry> entries = ConfigRegistry.getConfigFromID(id).getPacketInformation();
    	ArrayList<PacketEntry> newentries = new ArrayList<PacketEntry>();
    	boolean first = true;
    	for (int i = 0; i < entries.size(); i++) {
    		newentries.add(entries.get(i));
			if((i > 0 && (double)i/(double)maxEntriesInPacket == Math.floor((double)i/(double)maxEntriesInPacket)) || i == entries.size()-1)
			{
				packets.add(new SystemPacket(id, (ArrayList<PacketEntry>)newentries.clone(), first, i == entries.size()-1));
				first = false;
				newentries.clear();
			}
		}
		return packets;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(entries.size());
    	for (int i = 0; i < entries.size(); i++) {
    		buf.writeInt(PacketEntry.getID(entries.get(i).getClass()));
			entries.get(i).writeEntry(buf);
		}
		buf.writeInt(this.systemID);
		buf.writeBoolean(isFirst);
		buf.writeBoolean(isLast);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		int count = buf.readInt();
		this.entries = new ArrayList<PacketEntry>();
		for (int i = 0; i < count; i++) {
			PacketEntry entry = PacketEntry.getPacketEntry(buf.readInt());
			entry.readEntry(buf);
			this.entries.add(entry);
		}
		this.systemID = buf.readInt();
		if(packets.get(this.systemID) == null)
			packets.put(this.systemID, new ArrayList<SystemPacket>());
		ArrayList<SystemPacket> systemPackets = packets.get(this.systemID);
		isFirst = buf.readBoolean();
		isLast = buf.readBoolean();
		
		if(isFirst)
			systemPackets.clear();
		
		systemPackets.add(this);
		
		if(isLast)
		{
			ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
			for (int i = 0; i < systemPackets.size(); i++) {
				entries.addAll(systemPackets.get(i).entries);
			}
			ConfigRegistry.getConfigFromID(this.systemID).PreUpdateInformation(entries);
			ConfigRegistry.getConfigFromID(this.systemID).UpdateInformation(entries);
			ConfigRegistry.getConfigFromID(this.systemID).PostUpdateInformation(entries);
		}
	}
    
}
