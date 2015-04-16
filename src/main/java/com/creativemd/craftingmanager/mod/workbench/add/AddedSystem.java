package com.creativemd.craftingmanager.mod.workbench.add;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.creativemd.craftingmanager.api.common.utils.packet.ArrayPacketEntry;
import com.creativemd.craftingmanager.api.common.utils.packet.PacketEntry;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.common.utils.string.StringUtils;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.workbench.WorkbenchHelper;

public class AddedSystem extends ConfigSystem {
	
	public static List addedRecipes;
	
	public AddedSystem() {
		super("Adding Crafting Recipes", CraftingManagerMod.workbenchTab);
	}

	@Override
	public void loadSystem() {
		addedRecipes = new ArrayList();
	}

	@Override
	public void loadConfig(Configuration config) {
		addedRecipes.clear();
		
		//items|result|x|y
		Map<String, Property> shaped = config.getCategory("addshapedore").getValues();
		for(int zahl = 0; zahl < shaped.size(); zahl++)
		{
			Property RecipeData = (Property) shaped.values().toArray()[zahl]; 
			String Data = RecipeData.getName();
			IRecipe recipe = WorkbenchHelper.StringToRecipe(true, Data);
			if(recipe != null)
				addedRecipes.add(recipe);
				
		}
		
		Map<String, Property> shapless = config.getCategory("addshapelessore").getValues();
		for(int zahl = 0; zahl < shapless.size(); zahl++)
		{
			Property RecipeData = (Property) shapless.values().toArray()[zahl]; 
			String Data = RecipeData.getName();
			IRecipe recipe = WorkbenchHelper.StringToRecipe(false, Data);
			if(recipe != null)
				addedRecipes.add(recipe);
		}
		CraftingManager.getInstance().getRecipeList().addAll(addedRecipes);
	}

	@Override
	public void saveConfig(Configuration config) {
		if(config.getCategory("addshapedore") != null)
			config.getCategory("addshapedore").clear();
		if(config.getCategory("addshapelessore") != null)
			config.getCategory("addshapelessore").clear();
		for(int zahl = 0; zahl < addedRecipes.size(); zahl++)
		{
			IRecipe recipeO = (IRecipe) addedRecipes.get(zahl);
			if(recipeO != null)
				config.get("add" + WorkbenchHelper.getRecipeCategoryString(recipeO), WorkbenchHelper.RecipeToString(recipeO), true);
		}
	}

	@Override
	public ArrayList<ConfigEntry> getEntries() {
		ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		for (int i = 0; i < addedRecipes.size(); i++) {
			entries.add(new AddCraftingEntry((IRecipe) addedRecipes.get(i)));
		}
		entries.add(new AddCraftingEntry(null));
		return entries;
	}

	@Override
	public ArrayList<PacketEntry> getPacketInformation() {
		ArrayList<PacketEntry> entries = new ArrayList<PacketEntry>();
		String[] recipes = new String[addedRecipes.size()];
		for(int zahl = 0; zahl < recipes.length; zahl++)
			recipes[zahl] = StringUtils.ObjectsToString(WorkbenchHelper.getRecipeCategoryString(addedRecipes.get(zahl)), WorkbenchHelper.RecipeToString((IRecipe) addedRecipes.get(zahl)));
		entries.addAll(ArrayPacketEntry.getPacketArray(recipes));
		return entries;
	}
	
	@Override
	public void UpdateInformation(ArrayList<PacketEntry> Packet)
	{
		addedRecipes.clear();
		String[] input = ArrayPacketEntry.getArray(Packet);
		for(int zahl = 0; zahl < input.length; zahl++)
		{
			Object[] objects = StringUtils.StringToObjects(input[zahl]);
			if(objects.length == 2 && objects[0] instanceof String && objects[1] instanceof String)
			{
				IRecipe recipe = WorkbenchHelper.StringToRecipe(objects[0].equals("shapedore"), (String)objects[1]);
				if(recipe != null && !addedRecipes.contains(recipe))
					addedRecipes.add(recipe);
			}
			
		}
		List mcRecipes = CraftingManager.getInstance().getRecipeList();
		List newRecipes = new ArrayList();
		for (int i = 0; i < mcRecipes.size(); i++) {
			if(!(mcRecipes.get(i) instanceof BetterShapedRecipe) && !(mcRecipes.get(i) instanceof BetterShapelessRecipe))
				newRecipes.add(mcRecipes.get(i));
		}
		newRecipes.addAll(addedRecipes);
		mcRecipes.clear();
		mcRecipes.addAll(newRecipes);
	}
	
	@Override
	public String getRecieveInformation() {
		return "Added " + addedRecipes.size() + " crafting recipe(s)";
	}

	@Override
	public boolean needClientUpdate() {
		return true;
	}

	@Override
	public void onEntryChange(ConfigEntry entry) {
		
	}

}
