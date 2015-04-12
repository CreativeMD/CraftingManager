package com.creativemd.craftingmanager.mod.furnace.add;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8.Fun;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.creativemd.craftingmanager.api.common.utils.packet.ArrayPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.furnace.FurnaceRecipe;
import com.creativemd.craftingmanager.mod.furnace.disable.DisableFurnace;

public class AddFurnace extends ConfigSystem{

	public AddFurnace() {
		super("Adding Furnace Recipes", CraftingManagerMod.furnaceTab);
	}
	
	public static ArrayList<FurnaceRecipe> addedRecipes;

	@Override
	public void loadSystem() {
		addedRecipes = new ArrayList<FurnaceRecipe>();
	}

	@Override
	public void loadConfig(Configuration config) {
		addedRecipes.clear();
		Map<String, Property> shaped = config.getCategory("addsmelting").getValues();
		for(int zahl = 0; zahl < shaped.size(); zahl++)
		{
			Property RecipeData = (Property) shaped.values().toArray()[zahl]; 
			String Data = RecipeData.getName();
			FurnaceRecipe recipe = FurnaceRecipe.StringToRecipe(Data);
			if(recipe != null)
				addedRecipes.add(recipe);
				
		}
		addRecipes();
	}

	@Override
	public void saveConfig(Configuration config) {
		if(config.getCategory("addsmelting") != null)
			config.getCategory("addsmelting").clear();
		
		for (int i = 0; i < addedRecipes.size(); i++)
			config.get("addsmelting", FurnaceRecipe.RecipeToString(addedRecipes.get(i)), true);
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		for (int i = 0; i < addedRecipes.size(); i++) {
			entries.add(new FurnaceAddedEntry(addedRecipes.get(i)));
		}
		entries.add(new FurnaceAddedEntry(null));
		return entries;
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		String[] recipes = new String[addedRecipes.size()];
		for (int i = 0; i < recipes.length; i++) {
			recipes[i] = FurnaceRecipe.RecipeToString(addedRecipes.get(i));
		}
		entries.addAll(ArrayPacketEntry.getPacketArray(recipes));
		return entries;
	}
	
	public void addRecipes()
	{
		CraftingManagerMod.disableFurnace.updateDisabledRecipe();
	}
	
	@Override
	public void UpdateInformation(ArrayList<PacketEntry> Packet)
	{
		addedRecipes.clear();
		String[] recipes = ArrayPacketEntry.getArray(Packet);
		for (int i = 0; i < recipes.length; i++) {
			FurnaceRecipe recipe = FurnaceRecipe.StringToRecipe(recipes[i]);
			if(recipe != null)
				addedRecipes.add(recipe);
			else
				System.out.println("Found invalid recipe! String=" + recipes[i]);
		}
		addRecipes();
	}

	@Override
	public String getRecieveInformation() {
		return "Added " + addedRecipes.size() + " furnace recipe(s)";
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}

}
