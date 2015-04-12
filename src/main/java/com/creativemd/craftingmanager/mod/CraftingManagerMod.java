package com.creativemd.craftingmanager.mod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import com.creativemd.craftingmanager.api.common.utils.packet.ArrayPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.BooleanPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.StringPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.common.utils.string.StringUtils;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.craftingmanager.api.utils.sorting.ItemSortingList;
import com.creativemd.craftingmanager.api.utils.sorting.items.BlockSorting;
import com.creativemd.craftingmanager.mod.core.CoreSystem;
import com.creativemd.craftingmanager.mod.furnace.add.AddFurnace;
import com.creativemd.craftingmanager.mod.furnace.disable.DisableFurnace;
import com.creativemd.craftingmanager.mod.utils.CommandGUI;
import com.creativemd.craftingmanager.mod.utils.EventHandler.PacketRequest;
import com.creativemd.craftingmanager.mod.utils.GuiHandler;
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
import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = CraftingManagerMod.modid, name = CraftingManagerMod.name, version = CraftingManagerMod.version)
public class CraftingManagerMod extends DummyModContainer{
	
	public CraftingManagerMod() {

		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = CraftingManagerMod.modid + "core";
		meta.name = CraftingManagerMod.name + " Core";
		meta.version = CraftingManagerMod.version; //String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
		meta.credits = "CreativeMD";
		meta.authorList = Arrays.asList("CreativeMD");
		meta.description = "";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	public static final String modid = "zcraftingmanager"; 
	public static final String version = "1.1.0";
	public static final String name = "Crafting Manager";
	
	public static ItemSortingList forbiddenOutputs = new ItemSortingList().setBlackList();
	
	@Instance(CraftingManagerMod.modid)
	public static CraftingManagerMod instance = new CraftingManagerMod();
	
	@SidedProxy(clientSide = "com.creativemd.craftingmanager.mod.CraftingManagerModClient", serverSide = "com.creativemd.craftingmanager.mod.CraftingManagerModServer")
	public static CraftingManagerModServer proxy;
	
	public static Configuration config;
	public static Configuration settings;
	
	public static void save()
	{
		if(MinecraftServer.getServer() != null)
		{
			if(!config.hasChanged())
				config.load();
			for(int zahl = 0; zahl < ConfigRegistry.systems.size(); zahl++)
				ConfigRegistry.systems.get(zahl).saveConfig(config);
			config.save();
		}
	}
	
	@EventHandler
	public void load(FMLPreInitializationEvent event)
	{
		settings = new Configuration(event.getSuggestedConfigurationFile());
		settings.load();
		String name = settings.get("General", "configName", "new1").getString();
		if(name.equals(""))
			name = "new1";
		settings.save();
		config = new Configuration(new File(new File(event.getModConfigurationDirectory(), "CraftingManager"), name + ".cfg"));
	}
	
	public static CoreSystem coreSystem;
	
	public static AddedSystem addedSystem;
	public static DisableSystem disableSystem;
	
	public static DisableFurnace disableFurnace;
	public static AddFurnace addFurnace;
	
	public static ConfigTab vanillaTab = new ConfigTab("CraftingManagerCore", new ItemStack(Items.iron_pickaxe));
	public static ConfigTab furnaceTab = new ConfigTab("FurnaceUtils", new ItemStack(Blocks.furnace));
	public static ConfigTab workbenchTab = new ConfigTab("WorkbenchUtils", new ItemStack(Blocks.crafting_table));
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new com.creativemd.craftingmanager.mod.utils.EventHandler());
		FMLCommonHandler.instance().bus().register(new com.creativemd.craftingmanager.mod.utils.EventHandler());
		CreativeCorePacket.registerPacket(CraftResultPacket.class, "CraftResult");
		CreativeCorePacket.registerPacket(GuiPacket.class, "ConfigGui");
		CreativeCorePacket.registerPacket(GuiPopupPacket.class, "GuiPopup");
		CreativeCorePacket.registerPacket(RecievePacket.class, "RecieveConfig");
		CreativeCorePacket.registerPacket(SystemPacket.class, "CSystemUpdate");
		//network.registerMessage((Class<? extends IMessageHandler<IMessage, IMessage>>)ReceiveHandler.class, (Class<? extends IMessage>)SystemPacket.class, 0, Side.CLIENT);
		
		proxy.loadSide();
		RecipeSorter.register("craftingmanager:bettershaped", BetterShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
		RecipeSorter.register("craftingmanager:bettershapeless", BetterShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
		//PacketEntry.registerEntry(ArrayPacketEntry.class);
		PacketEntry.registerEntry(IntegerPacketEntry.class);
		PacketEntry.registerEntry(BooleanPacketEntry.class);
		PacketEntry.registerEntry(StringPacketEntry.class);
		
		StringUtils.loadUtils();
		
		//Add Configs
		coreSystem = (CoreSystem) ConfigRegistry.registerConfig(new CoreSystem());
		disableSystem = (DisableSystem) ConfigRegistry.registerConfig(new DisableSystem());
		addedSystem = (AddedSystem) ConfigRegistry.registerConfig(new AddedSystem());
		disableFurnace = (DisableFurnace) ConfigRegistry.registerConfig(new DisableFurnace());
		addFurnace = (AddFurnace) ConfigRegistry.registerConfig(new AddFurnace());
	}
	
	public static void showGui(EntityPlayer player, int id, boolean correct)
	{
		showGui(player, id, correct, 0);
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
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandGUI());
	}
	
	@EventHandler
	public void finishModLoading(FMLLoadCompleteEvent event)
	{
		for (int i = 0; i < ConfigRegistry.systems.size(); i++) {
			ConfigRegistry.systems.get(i).loadSystem();
		}
	}
	
	@EventHandler
	public void serverStart(FMLServerStartedEvent event)
	{
		for (int i = 0; i < ConfigRegistry.systems.size(); i++) {
			ConfigRegistry.systems.get(i).loadConfig(config);
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
