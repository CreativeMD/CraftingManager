package com.creativemd.craftingmanager.mod.furnace;

import java.util.ArrayList;

import com.creativemd.craftingmanager.api.common.utils.string.StringUtils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class FurnaceRecipe {
	
	public Object input;
	public ItemStack output;
	public float experience;
	
	public FurnaceRecipe(Object input, ItemStack output, float experience)
	{
		this.input = input;
		this.output = output;
		this.experience = experience;
		if(input == null || output == null)
			System.out.println("Invalid smelting recipe!");
	}
	
	public ItemStack getInput()
	{
		ItemStack result = null;
		if(input instanceof Block)
			result = new ItemStack((Block) input);
		if(input instanceof Item)
			result = new ItemStack((Item) input);
		if(input instanceof ItemStack)
			result = (ItemStack) input;
		if(input instanceof String)
		{
			ArrayList<ItemStack> stacks = OreDictionary.getOres((String)input);
			if(stacks.size() > 0)
				return stacks.get(0);
		}
		return result;
	}
	
	public static FurnaceRecipe StringToRecipe(String input)
	{
		Object[] objects = StringUtils.StringToObjects(input);
		Object item = null;
		if(objects.length == 2)
		{
			item = objects[0];
		
			if((item instanceof Item || item instanceof Block || item instanceof ItemStack || item instanceof String) && objects[1] instanceof ItemStack)
			{
				return new FurnaceRecipe(item, (ItemStack)objects[1], 1);
			}
		}
		return null;
	}
	
	public static String RecipeToString(FurnaceRecipe recipe)
	{
		return StringUtils.ObjectsToString(recipe.input, recipe.output);
	}

}
