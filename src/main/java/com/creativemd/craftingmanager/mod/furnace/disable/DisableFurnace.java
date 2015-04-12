package com.creativemd.craftingmanager.mod.furnace.disable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import com.creativemd.craftingmanager.api.common.utils.entry.StateEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.ArrayPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.furnace.FurnaceRecipe;
import com.creativemd.craftingmanager.mod.furnace.add.AddFurnace;

public class DisableFurnace extends ConfigSystem{
	
	public static ArrayList<FurnaceRecipe> allRecipes;
	public static ArrayList<FurnaceRecipe> disabledRecipes;
	
	/**0: None, 1: Only 'Vanilla' Recipe, 2: Only Added Recipes, 3: All**/
	public static int recipeMode = 0;
	
	public DisableFurnace() {
		super("Disabling Furnace Recipes", CraftingManagerMod.furnaceTab);
	}

	@Override
	public void loadSystem() {
		allRecipes = new ArrayList<FurnaceRecipe>();
		Object[] array = FurnaceRecipes.smelting().getSmeltingList().keySet().toArray();
		for(int zahl = 0; zahl < array.length; zahl++)
		{
			if(array[zahl] != null)
			{
				Object object = FurnaceRecipes.smelting().getSmeltingList().get(array[zahl]);
				if(object instanceof ItemStack && ((ItemStack) object).getItem() != null)
					allRecipes.add(new FurnaceRecipe(array[zahl], (ItemStack)object, FurnaceRecipes.smelting().func_151398_b((ItemStack)object)));
			}
		}
		disabledRecipes = new ArrayList<FurnaceRecipe>();
	}
	
	@Override
	public void loadConfig(Configuration config) {		
		disabledRecipes.clear();
		for (int i = 0; i < allRecipes.size(); i++)
			if(!disabledRecipes.contains(allRecipes.get(i)) && config.hasKey("smelting", FurnaceRecipe.RecipeToString(allRecipes.get(i))))
				disabledRecipes.add(allRecipes.get(i));
		recipeMode = config.get("smelting", "mode", 0).getInt();
		updateDisabledRecipe();
	}
	
	@Override
	public void saveConfig(Configuration config) {
		if(config.getCategory("smelting") != null)
			config.getCategory("smelting").clear();
		
		for (int i = 0; i < disabledRecipes.size(); i++)
			config.get("smelting", FurnaceRecipe.RecipeToString(disabledRecipes.get(i)), false).set(false);
		
		config.get("smelting", "mode", 0).set(recipeMode);
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		entries.add(new StateEntry("General disables", recipeMode, "None", "Only Vanilla", "Only Added Recipes", "All"));
		for (int i = 0; i < allRecipes.size(); i++) {
			entries.add(new FurnaceDisabledEntry(allRecipes.get(i)));
		}
		return entries;
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		if(entry instanceof StateEntry)
		{
			recipeMode = ((StateEntry) entry).getState();
		}
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		String[] recipes = new String[disabledRecipes.size()];
		for (int i = 0; i < recipes.length; i++) {
			recipes[i] = FurnaceRecipe.RecipeToString(disabledRecipes.get(i));
		}
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		entries.add(new IntegerPacketEntry(recipeMode));
		entries.addAll(ArrayPacketEntry.getPacketArray(recipes));
		return entries;
	}
	
	@Override
	public void PreUpdateInformation(ArrayList<PacketEntry> Packet) {
		FurnaceRecipes.smelting().getSmeltingList().clear();
		for (int i = 0; i < allRecipes.size(); i++)
			addFurnaceRecipe(allRecipes.get(i));
		
		if(Packet.get(0) instanceof IntegerPacketEntry)
			recipeMode = ((IntegerPacketEntry)Packet.get(0)).value;
		
		disabledRecipes.clear();
		
		String[] disable = ArrayPacketEntry.getArray(Packet, 1);
		
		//ArrayToList
		List disabled = new ArrayList();
		for(int zahl = 0; zahl < disable.length; zahl++)
			disabled.add(disable[zahl]);
		
		for (int j = 0; j < allRecipes.size(); j++) {
			if(disabled.contains(FurnaceRecipe.RecipeToString(allRecipes.get(j))))
				disabledRecipes.add(allRecipes.get(j));
		}
		
		updateDisabledRecipe();
	}
	
	@Override
	public void PostUpdateInformation(ArrayList<PacketEntry> Packet) {
		if(recipeMode == 3)
			FurnaceRecipes.smelting().getSmeltingList().clear();
		if(recipeMode == 1)
			updateDisabledRecipe();
	}
	
	public void updateDisabledRecipe()
	{
		FurnaceRecipes.smelting().getSmeltingList().clear();
		if(recipeMode != 2)
		{
			for (int i = 0; i < allRecipes.size(); i++)
				if(!disabledRecipes.contains(allRecipes.get(i)))
					addFurnaceRecipe(allRecipes.get(i));
		}
		if(recipeMode != 1)
			for (int i = 0; i < AddFurnace.addedRecipes.size(); i++)
				addFurnaceRecipe(AddFurnace.addedRecipes.get(i));
	}

	@Override
	public String getRecieveInformation() {
		if(recipeMode == 3)
			return "Disabling all furnace recipes";
		if(recipeMode == 2)
			return "Disabling all vanilla furnace recipes";
		if(recipeMode == 1)
			return "Disabling all added and " + disabledRecipes.size() + " vanilla furnace recipe(s)";
		return "Disabling " + disabledRecipes.size() + " furnace recipe(s)";
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}
	
	public static void addFurnaceRecipe(FurnaceRecipe recipe)
	{
		if(recipe.input == null || recipe.output == null || recipe.output.getItem() == null || Float.isNaN(recipe.experience))
		{
			String output = "";
			try{
				if(recipe.input != null)
					output = "Input:" + recipe.input.toString();
				if(recipe.output != null && recipe.output.getItem() != null)
					output += ";Output:" + recipe.output.getItem().getUnlocalizedName(recipe.output);
			}catch(Exception e){
				output = "Could not print recipe without an error.";
			}
			
			System.out.println("Found invalid furnace recipe! " + output);
			return ;
		}
		if(recipe.input instanceof Block)
			FurnaceRecipes.smelting().func_151393_a((Block) recipe.input, recipe.output, recipe.experience);
		if(recipe.input instanceof Item)
			FurnaceRecipes.smelting().func_151396_a((Item) recipe.input, recipe.output, recipe.experience);
		if(recipe.input instanceof ItemStack)
			FurnaceRecipes.smelting().func_151394_a((ItemStack) recipe.input, recipe.output, recipe.experience);
		if(recipe.input instanceof String)
		{
			ArrayList<ItemStack> stacks = OreDictionary.getOres((String)recipe.input);
			for (int i = 0; i < stacks.size(); i++) {
				FurnaceRecipes.smelting().func_151394_a(stacks.get(i), recipe.output, recipe.experience);
			}
		}
	}

}
