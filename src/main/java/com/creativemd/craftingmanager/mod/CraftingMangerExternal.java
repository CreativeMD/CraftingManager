package com.creativemd.craftingmanager.mod;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;

import com.creativemd.craftingmanager.api.common.utils.packet.BooleanPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.StringPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.common.utils.string.StringUtils;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.mod.core.CoreSystem;
import com.creativemd.craftingmanager.mod.furnace.add.AddFurnace;
import com.creativemd.craftingmanager.mod.furnace.disable.DisableFurnace;
import com.creativemd.craftingmanager.mod.utils.GuiHandler;
import com.creativemd.craftingmanager.mod.utils.EventHandler.PacketRequest;
import com.creativemd.craftingmanager.mod.utils.client.GuiConfig;
import com.creativemd.craftingmanager.mod.utils.packets.CraftResultPacket;
import com.creativemd.craftingmanager.mod.utils.packets.GuiPacket;
import com.creativemd.craftingmanager.mod.utils.packets.GuiPopupPacket;
import com.creativemd.craftingmanager.mod.utils.packets.RecievePacket;
import com.creativemd.craftingmanager.mod.utils.packets.SystemPacket;
import com.creativemd.craftingmanager.mod.workbench.add.AddedSystem;
import com.creativemd.craftingmanager.mod.workbench.disable.DisableSystem;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

public class CraftingMangerExternal {

	public static void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(CraftingManagerMod.instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new com.creativemd.craftingmanager.mod.utils.EventHandler());
		FMLCommonHandler.instance().bus().register(new com.creativemd.craftingmanager.mod.utils.EventHandler());
		CreativeCorePacket.registerPacket(CraftResultPacket.class, "CraftResult");
		CreativeCorePacket.registerPacket(GuiPacket.class, "ConfigGui");
		CreativeCorePacket.registerPacket(GuiPopupPacket.class, "GuiPopup");
		CreativeCorePacket.registerPacket(RecievePacket.class, "RecieveConfig");
		CreativeCorePacket.registerPacket(SystemPacket.class, "CSystemUpdate");
		//network.registerMessage((Class<? extends IMessageHandler<IMessage, IMessage>>)ReceiveHandler.class, (Class<? extends IMessage>)SystemPacket.class, 0, Side.CLIENT);
		
		CraftingManagerMod.proxy.loadSide();
		RecipeSorter.register("craftingmanager:bettershaped", BetterShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
		RecipeSorter.register("craftingmanager:bettershapeless", BetterShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
		//PacketEntry.registerEntry(ArrayPacketEntry.class);
		PacketEntry.registerEntry(IntegerPacketEntry.class);
		PacketEntry.registerEntry(BooleanPacketEntry.class);
		PacketEntry.registerEntry(StringPacketEntry.class);
		
		StringUtils.loadUtils();
		
		//Add Configs
		CraftingManagerMod.coreSystem = (CoreSystem) ConfigRegistry.registerConfig(new CoreSystem());
		CraftingManagerMod.disableSystem = (DisableSystem) ConfigRegistry.registerConfig(new DisableSystem());
		CraftingManagerMod.addedSystem = (AddedSystem) ConfigRegistry.registerConfig(new AddedSystem());
		CraftingManagerMod.disableFurnace = (DisableFurnace) ConfigRegistry.registerConfig(new DisableFurnace());
		CraftingManagerMod.addFurnace = (AddFurnace) ConfigRegistry.registerConfig(new AddFurnace());
	}
	
	public static void showGui(EntityPlayer player, int id, boolean correct, int page)
	{
		if(correct)
		{
			if(id < 0)
				id = ConfigRegistry.systems.size()-1;
			if(id >= ConfigRegistry.systems.size())
				id = 0;
		}
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			GuiConfig.notClosing = true;
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && player instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)player).openGui(CraftingManagerMod.instance, id, ((EntityPlayerMP)player).worldObj, (int)((EntityPlayerMP)player).posX, (int)((EntityPlayerMP)player).posY, (int)((EntityPlayerMP)player).posZ);
			PacketHandler.sendPacketToPlayer(new GuiPacket(id, page), (EntityPlayerMP) player);
		}else{
			PacketHandler.sendPacketToServer(new GuiPacket(id, page));
		}
	}
	
	public static void sendUpdateToServer()
	{
		for(int zahl = 0; zahl < ConfigRegistry.systems.size(); zahl++)
		{
			ArrayList<CreativeCorePacket> packets = SystemPacket.getPackets(zahl);
			for (int i = 0; i < packets.size(); i++) {
				com.creativemd.craftingmanager.mod.utils.EventHandler.packetrequest.add(new PacketRequest(null, packets.get(i), true));
			}
			
		}
		com.creativemd.craftingmanager.mod.utils.EventHandler.packetrequest.add(new PacketRequest(null, new RecievePacket(), true));
	}
	
	public static void sendUpdateToAll(int systemID)
	{
		ArrayList<CreativeCorePacket> packets = SystemPacket.getPackets(systemID);
		for (int i = 0; i < packets.size(); i++) {
			com.creativemd.craftingmanager.mod.utils.EventHandler.packetrequest.add(new PacketRequest(null, packets.get(i), false));
		}
	}
	
	public static void sendUpdateToClient(EntityPlayer player)
	{
		for(int zahl = 0; zahl < ConfigRegistry.systems.size(); zahl++)
		{
			if(ConfigRegistry.systems.get(zahl).needClientUpdate())
			{
				ArrayList<CreativeCorePacket> packets = SystemPacket.getPackets(zahl);
				for (int i = 0; i < packets.size(); i++) {
					com.creativemd.craftingmanager.mod.utils.EventHandler.packetrequest.add(new PacketRequest((EntityPlayerMP) player, packets.get(i), false));
				}
			}
		}
		com.creativemd.craftingmanager.mod.utils.EventHandler.packetrequest.add(new PacketRequest(player, new RecievePacket(), false));
	}
	
}
