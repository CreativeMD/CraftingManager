package com.creativemd.craftingmanager.mod.workbench.disable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

import com.creativemd.craftingmanager.api.common.utils.packet.ArrayPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.IntegerPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.workbench.WorkbenchHelper;

public class DisableSystem extends ConfigSystem{
	
	public static List allRecipes;
	public static List disableRecipes;
	
	/**0: None, 1: Only 'Vanilla' Recipe, 2: Only Added Recipes, 3: All**/
	public static int recipeMode = 0;
	
	public DisableSystem() {
		super("Disabling Crafting Recipes", CraftingManagerMod.workbenchTab);
	}

	@Override
	public void loadSystem() {
		allRecipes = new ArrayList();
		disableRecipes = new ArrayList();
	}

	@Override
	public void loadConfig(Configuration config) {
		if(allRecipes.size() == 0)
			allRecipes.addAll(CraftingManager.getInstance().getRecipeList());
		List crafting = allRecipes;
		disableRecipes = new ArrayList();
		for(int zahl = 0; zahl < crafting.size(); zahl++)
			if(!disableRecipes.contains(crafting.get(zahl)) && config.hasKey(WorkbenchHelper.getRecipeCategoryString((IRecipe)crafting.get(zahl)), WorkbenchHelper.getRecipeInfo(crafting.get(zahl))))
				disableRecipes.add(crafting.get(zahl));
		recipeMode = config.get("CraftingRecipes", "mode", 0).getInt(0);
		updateDisabledRecipe();
	}

	@Override
	public void saveConfig(Configuration config) {
		if(config.getCategory("shapedore") != null)
			config.getCategory("shapedore").clear();
		if(config.getCategory("shaped") != null)
			config.getCategory("shaped").clear();
		if(config.getCategory("shapelessore") != null)
			config.getCategory("shapelessore").clear();
		if(config.getCategory("shapeless") != null)
			config.getCategory("shapeless").clear();
		if(config.getCategory("irecipe") != null)
			config.getCategory("irecipe").clear();
		
		for(int zahl = 0; zahl < disableRecipes.size(); zahl++)
		{
			config.get(WorkbenchHelper.getRecipeCategoryString((IRecipe)disableRecipes.get(zahl)), WorkbenchHelper.getRecipeInfo(disableRecipes.get(zahl)), false).set(false);
		}
		config.get("CraftingRecipes", "mode", 0).set(recipeMode);
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		entries.add(new ModeEntry());
		for (int i = 0; i < allRecipes.size(); i++) {
			entries.add(new CraftingEntry((IRecipe) allRecipes.get(i)));
		}
		return entries;
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		entries.add(new IntegerPacketEntry(recipeMode));
		String[] input = new String[disableRecipes.size()];
		for (int i = 0; i < input.length; i++) {
			input[i] =  WorkbenchHelper.getRecipeInfo(disableRecipes.get(i));
		}
		entries.addAll(ArrayPacketEntry.getPacketArray(input));
		return entries;
	}

	@Override
	public void PreUpdateInformation(ArrayList<PacketEntry> Packet) {
		if(allRecipes.size() == 0)
			allRecipes.addAll(CraftingManager.getInstance().getRecipeList());
		CraftingManager.getInstance().getRecipeList().clear();
		CraftingManager.getInstance().getRecipeList().addAll(allRecipes);
		disableRecipes.clear();
		if(Packet.get(0) instanceof IntegerPacketEntry)
			recipeMode = ((IntegerPacketEntry)Packet.get(0)).value;
		
		
		String[] disable = ArrayPacketEntry.getArray(Packet, 1);
		
		//ArrayToList
		List disabled = new ArrayList();
		for(int zahl = 0; zahl < disable.length; zahl++)
			disabled.add(disable[zahl]);
		
		for (int j = 0; j < allRecipes.size(); j++) {
			if(disabled.contains(WorkbenchHelper.getRecipeInfo(allRecipes.get(j))))
				disableRecipes.add(allRecipes.get(j));
		}
		
		updateDisabledRecipe();
	}
	
	@Override
	public void PostUpdateInformation(ArrayList<PacketEntry> Packet) {
		if(recipeMode == 3)
			CraftingManager.getInstance().getRecipeList().clear();
		if(recipeMode == 1)
			updateDisabledRecipe();
	}
	
	public void updateDisabledRecipe()
	{
		List recipe = CraftingManager.getInstance().getRecipeList();
		List newRecipeList = new ArrayList();
		for (int i = 0; i < recipe.size(); i++) {
			if(!disableRecipes.contains(recipe.get(i)))
				newRecipeList.add(recipe.get(i));
		}
		recipe.clear();
		if(recipeMode != 2)
			recipe.addAll(newRecipeList);
	}
	
	@Override
	public String getRecieveInformation() {
		if(recipeMode == 3)
			return "Disabling all crafting recipes";
		if(recipeMode == 2)
			return "Disabling all vanilla crafting recipes";
		if(recipeMode == 1)
			return "Disabling all added and " + disableRecipes.size() + " vanilla recipe(s)";
		return "Disabling " + disableRecipes.size() + " crafting recipe(s)";
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}
	
	public static boolean isRecipeDisabled(Object recipe)
	{
		return disableRecipes.contains(recipe);
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		
	}
}
