package com.creativemd.craftingmanager.mod.workbench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.common.utils.string.StringUtils;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.workbench.disable.DisableSystem;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class WorkbenchHelper {
	
	public static Object[] addArray(Object[] Old, Object[] New)
	{
		Object[] result = new Object[Old.length+New.length];
		for(int zahl = 0; zahl < result.length; zahl++)
			if(zahl < Old.length)
				result[zahl] = Old[zahl];
			else
				result[zahl] = New[zahl-Old.length];
		return result;
	}
	
	public static IRecipe getRecipeFromGrid(ItemStack result, ItemInfo[] stacks, boolean isShaped)
	{
		if(isShaped)
		{			
			//Caculate size of needed grid
			int startX = -1;
			int startY = -1;
			int endX = -1;
			int endY = -1;
			
			for (int i = 0; i < stacks.length; i++)
			{
				int row = i/3;
				int col = i-row*3;
				
				if(stacks[i] != null)
				{
					if(startX == -1 || startX > col)
						startX = col;
					if(endX == -1 || endX < col)
						endX = col;
					if(startY == -1 || startY > row)
						startY = row;
					if(endY == -1 || endY < row)
						endY = row;						
				}
			}
			
			if(startX == -1 || endX == -1 || startY == -1 || endY == -1)
				return null;
			
			endX++;
			endY++;
			
			int sizeX = endX - startX;
			int sizeY = endY - startY;
			
			String[] rows = new String[sizeY];
			Arrays.fill(rows, "");
			
			ArrayList stackData = new ArrayList();
			ArrayList infoData = new ArrayList();
			
			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					int index = startX+(startY+y)*3+x;
					String charOfGrid = getStringInGrid(index);
					rows[y] += charOfGrid;
					
					int stackIndex = index;
					if(stacks[stackIndex] != null)
					{
						stackData.add(charOfGrid.charAt(0));
						stackData.add(stacks[stackIndex].getItemStack());
						infoData.add(charOfGrid.charAt(0));
						infoData.add(stacks[stackIndex]);
					}
					
				}
			}
			
			return new BetterShapedRecipe(result, WorkbenchHelper.addArray(rows, infoData.toArray()), WorkbenchHelper.addArray(rows, stackData.toArray()));			
		}else{
			ArrayList items = new ArrayList();
			ArrayList info = new ArrayList();
			
			for (int i = 0; i < stacks.length; i++) {
				if(stacks[i] != null)
				{
					items.add(stacks[i].getItemStack());
					info.add(stacks[i]);
				}
			}
			return new BetterShapelessRecipe(result, info, items.toArray());
		}
	}
	
	public static String getStringInGrid(int index)
	{
		switch(index)
		{
		case 0: return "A";
		case 1: return "B";
		case 2: return "C";
		case 3: return "D";
		case 4: return "E";
		case 5: return "F";
		case 6: return "G";
		case 7: return "H";
		case 8: return "I";
		case 9: return "J";
		case 10: return "K";
		case 11: return "L";
		}
		return "Z";
	}
	
	public static String getItemStackInfo(ItemStack stack)
	{
		if(stack == null || stack.getItem() == null)
			return "null";
		String Result = "";
		Result = Item.itemRegistry.getNameForObject(stack.getItem()) + "|" + stack.getItemDamage() + "|" + stack.stackSize;
		if(stack.stackTagCompound != null)Result = Result + "|" + stack.stackTagCompound.toString();
		try
		{
			Result = stack.getUnlocalizedName() + "|" + Item.itemRegistry.getNameForObject(stack.getItem()) + "|" + stack.getItemDamage() + "|" + stack.stackSize;
			if(stack.stackTagCompound != null)Result = Result + "|" + stack.stackTagCompound.toString();
		}catch (Exception e) {
			Result = Item.itemRegistry.getNameForObject(stack.getItem()) + "|" + stack.getItemDamage() + "|" + stack.stackSize;
			if(stack.stackTagCompound != null)Result = Result + "|" + stack.stackTagCompound.toString();
		}		
		return Result.replace("\"", "&high");
	}
	
	public static String getObjectInfo(Object[] items)
	{
		String Result = "";
		for(int zahl = 0; zahl < items.length; zahl++)
		{
			Object item = items[zahl];
			if(item instanceof Item){
				Result = Result + ((Item)item).getItemStackDisplayName(new ItemStack(((Item)item)));
			}else if(item instanceof Block){
				Result = Result + ((Block)item).getLocalizedName();
			}else if(item instanceof ItemStack){
				try{
					Result = Result + ((ItemStack) item).getDisplayName();
				}catch(Exception e){
					Result = Result + getItemStackInfo((ItemStack) item);
				}
			}else if(item instanceof List<?>){
				List stacks = (List) item;
				Result = Result + getObjectInfo(((List) item).toArray());
			}else if(item instanceof String){
				Result = Result + item;
			}else if(item == null){
				Result = Result + "null";
			}
			Result = Result + ";";
		}
		return Result.toLowerCase();
	}

	public static boolean canRecipeBeFound(IRecipe recipe, String search) {
		search = search.toLowerCase();
		if(!search.equals(""))
		{
			if("disabled".contains(search) && DisableSystem.disableRecipes.contains(recipe))
				return true;
			
			if("enabled".contains(search) && !DisableSystem.disableRecipes.contains(recipe))
				return true;
			
			String test = recipe.getClass().getName().toLowerCase();
			if(recipe.getClass().getName().toLowerCase().contains(search))
				return true;
			
			if(recipe instanceof ShapedRecipes)
			{
				if(getObjectInfo(((ShapedRecipes) recipe).recipeItems).toLowerCase().contains(search))
					return true;
			} 
			
			if(recipe instanceof ShapelessRecipes)
			{
				if(getObjectInfo(((ShapelessRecipes)recipe).recipeItems.toArray()).toLowerCase().contains(search))
					return true;
			}
			
			if(recipe instanceof ShapelessOreRecipe)
			{
				if(getObjectInfo(((ShapelessOreRecipe)recipe).getInput().toArray()).toLowerCase().contains(search))
					return true;
			}	
			
			if(recipe instanceof ShapedOreRecipe)
			{
				if(getObjectInfo(((ShapedOreRecipe)recipe).getInput()).toLowerCase().contains(search))
					return true;
			}	
			if(getObjectInfo(new Object[]{recipe.getRecipeOutput()}).toLowerCase().contains(search))
				return true;
			return false;
		}
		return true;
	}
	
	public static String getRecipeCategoryString(Object object)
	{
		if(object instanceof BetterShapedRecipe)
		{
			return "shapedore";
		}
		else if(object instanceof ShapedRecipes)
		{
			return "shaped";
		}
		else if(object instanceof BetterShapelessRecipe)
		{
			return "shapelessore";
		}	
		else if(object instanceof ShapelessRecipes)
		{
			return "shapeless";
		}	
		else if(object instanceof IRecipe)
		{
			return "irecipe";
		}
		return "other";
	}
	
	public static String RecipeToString(IRecipe recipe)
	{
		int sizeX = 0;
		int sizeY = 0;
		ArrayList objects = new ArrayList();
		if(recipe instanceof BetterShapedRecipe)
		{
			for (int i = 0; i < ((BetterShapedRecipe)recipe).itemData.length; i++)
				if(((BetterShapedRecipe)recipe).itemData[i] != null)
					objects.add(((BetterShapedRecipe)recipe).itemData[i]);
				else
					objects.add("null");
			
			sizeX = ((BetterShapedRecipe) recipe).recipeWidth;
			sizeY = ((BetterShapedRecipe) recipe).recipeHeight;	
		}
		if(recipe instanceof BetterShapelessRecipe)
		{
			objects.addAll(((BetterShapelessRecipe) recipe).itemData);
		}
 		return StringUtils.ObjectsToString(addArray(new Object[]{sizeX, sizeY, recipe.getRecipeOutput()}, objects.toArray()));
	}
	
	public static IRecipe StringToRecipe(boolean isShaped, String input)
	{
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length > 3 && objects[0] instanceof Integer && objects[1] instanceof Integer && objects[2] instanceof ItemStack)
		{
			int sizeX = (Integer)objects[0];
			int sizeY = (Integer)objects[1];
			ItemStack result = (ItemStack)objects[2];
			if(isShaped)
			{
				ArrayList infoData = new ArrayList();
				ArrayList stackData = new ArrayList();
				String[] rows = new String[sizeY];
				Arrays.fill(rows, "");
				for (int i = 0; i < sizeX*sizeY; i++) {
					String charOfGrid = getStringInGrid(i);
					rows[i/sizeX] += charOfGrid;
					int index = i+3;
					if(objects.length > i && objects[index] instanceof ItemInfo)
					{
						ItemStack stack = ((ItemInfo) objects[index]).getItemStack();
						if(stack != null)
						{
							stackData.add(charOfGrid.charAt(0));
							stackData.add(stack);
							infoData.add(charOfGrid.charAt(0));
							infoData.add((ItemInfo)objects[index]);
						}
					}
				}
				if(stackData.size() == 0)
				{
					System.out.println("Found invalid recipe");
					return null;
				}
				return new BetterShapedRecipe(result, addArray(rows, infoData.toArray()), addArray(rows, stackData.toArray()));
			}else{
				ArrayList<ItemInfo> infos = new ArrayList<ItemInfo>();
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				for (int i = 3; i < objects.length; i++) {
					if(objects[i] != null)
					{
						ItemStack stack = ((ItemInfo)objects[i]).getItemStack();
						if(stack != null)
						{
							infos.add((ItemInfo)objects[i]);
							stacks.add(stack);
						}
					}
				}
				if(stacks.size() == 0)
				{
					System.out.println("Found invalid recipe");
					return null;
				}
				return new BetterShapelessRecipe(result, infos, stacks.toArray());
			}
		}
		return null;
	}
	
	public static String getRecipeInfo(Object object)
	{
		if(object instanceof ShapedOreRecipe)
		{
			ShapedOreRecipe recipe = (ShapedOreRecipe) object;
			return "R-I-" + getObjectInfo(recipe.getInput()) + "-O-" + getItemStackInfo(recipe.getRecipeOutput());
		}
		else if(object instanceof ShapedRecipes)
		{
			ShapedRecipes recipe = (ShapedRecipes) object;
			return "R-I-" + getObjectInfo(recipe.recipeItems) + "-O-" + getItemStackInfo(recipe.getRecipeOutput());
		}
		else if(object instanceof ShapelessOreRecipe)
		{
			ShapelessOreRecipe recipe = (ShapelessOreRecipe) object;
			return "R-I-" + getObjectInfo(recipe.getInput().toArray()) + "-O-" + getItemStackInfo(recipe.getRecipeOutput());
		}	
		else if(object instanceof ShapelessRecipes)
		{
			ShapelessRecipes recipe = (ShapelessRecipes) object;
			return "R-I-" + getObjectInfo(recipe.recipeItems.toArray()) + "-O-" + getItemStackInfo(recipe.getRecipeOutput());
		}	
		else if(object instanceof IRecipe)
		{
			IRecipe recipe = (IRecipe) object;
			return "R-I-" + recipe.getRecipeSize() +"|" + recipe.getClass().getName() + "-O-" + getItemStackInfo(recipe.getRecipeOutput());
		}
		return "unkown";
	}
}
