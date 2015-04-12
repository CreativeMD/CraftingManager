package com.creativemd.craftingmanager.api.common.utils.entry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.creativemd.craftingmanager.api.common.SlotInfo;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapedRecipe;
import com.creativemd.craftingmanager.api.common.utils.recipe.BetterShapelessRecipe;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.utils.server.SlotInput;

public abstract class CraftingRecipeEntry extends ConfigEntry{
	
	@Override
	public int getHeight() {
		return 68;
	}

	public void loadRecipe(IRecipe recipe, int state) {
		for(int zahl = 0; zahl < 9; zahl++)
			slots.get(zahl).putStack(null);
		
		if(recipe instanceof BetterShapedRecipe)
		{
			 setInput(((BetterShapedRecipe) recipe).getInput(), recipe.getRecipeSize(), state, ((BetterShapedRecipe) recipe).recipeWidth, ((BetterShapedRecipe) recipe).itemData);
		}else if(recipe instanceof ShapedOreRecipe)
		{
			ShapedOreRecipe newrecipe = (ShapedOreRecipe) recipe;
			setInput(newrecipe.getInput(), newrecipe.getRecipeSize(), state, 0);
		}
		else if(recipe instanceof BetterShapelessRecipe)
		{
			ItemInfo[] infos = new ItemInfo[((BetterShapelessRecipe) recipe).itemData.size()];
			for (int i = 0; i < infos.length; i++) {
				infos[i] = ((BetterShapelessRecipe) recipe).itemData.get(i);
			}
			setInput(((BetterShapelessRecipe) recipe).getInput().toArray(), recipe.getRecipeSize(), state, 0, infos);
		}
		else if(recipe instanceof ShapedRecipes)
		{
			ShapedRecipes newrecipe = (ShapedRecipes) recipe;
			setInput(newrecipe.recipeItems, newrecipe.getRecipeSize(), state, newrecipe.recipeWidth);
		}
		else if(recipe instanceof ShapelessOreRecipe)
		{
			ShapelessOreRecipe newrecipe = (ShapelessOreRecipe) recipe;
			setInput(newrecipe.getInput().toArray(), 9, state, 0);
		}	
		else if(recipe instanceof ShapelessRecipes)
		{
			ShapelessRecipes newrecipe = (ShapelessRecipes) recipe;
			setInput(newrecipe.recipeItems.toArray(), 9, state, 0);
		}
		
		if(state != 2)
			slots.get(9).putStack(recipe.getRecipeOutput());
	}
	
	public void setInput(Object[] items, int size, int state, int cols)
	{
		setInput(items, size, state, cols, null);
	}
	
	public void setInput(Object[] items, int size, int state, int cols, ItemInfo[] info)
	{
		if(state == 2 | state == 1)return ;
		if(cols == 0)
		{
			switch(size)
			{
			case 1:
				cols = 1;
			case 2:
				cols = 2;
				break;
			case 3:
				cols = 3;
				break;
			case 4:
				cols = 2;
				break;
			case 6:
				cols = 3;
				break;
			default:
				cols = 3;
				break;
			}
		}
		for(int zahl = 0; zahl < size; zahl++)
		{
			int row = zahl/cols;
			int index = row*3 + zahl - row*cols;
			if(items.length > zahl && index < 9 && index > -1)
			{
				if(info != null && slots.get(index) instanceof SlotInput && info[zahl] != null)
				{
					((SlotInput)slots.get(index)).info = info[zahl];
					slots.get(index).putStack(info[zahl].getItemStack());
				}else{
					if(slots.get(index) instanceof SlotInput)
						((SlotInput)slots.get(index)).info = null;
					slots.get(index).putStack(getItemStack(items[zahl]));
				}
			}
		}
	}
	
	public ItemStack getItemStack(Object object)
	{
		ItemStack[] result = ObjectoItemStack(object);
		ItemStack Return = null;
		if(result.length > 0)
			Return = result[result.length-1];
		if(Return != null)
			Return.stackSize = 1;
		return Return;
	}
	
	/*public Object getItemStack(ItemInfo info, Object stack)
	{
		Object item;
		if(info != null && stack instanceof ItemStack)
			info.stack = ((ItemStack)stack).copy();
		if(info == null)
		{
			item = null;
		}else if(info.stack == null){
			return stack;
		}else if(!info.needDamage && !info.needNBT)
		{
			if(info.stack.getItem() != null && Block.getBlockFromItem(info.stack.getItem()) instanceof BlockAir)
			{
				item = info.stack.getItem();
			}else{
				item = Block.getBlockFromItem(info.stack.getItem());
			}
		}else if(!info.OreName.equals("")){
			item = info.OreName;
		}else{
			item = info.stack.copy();
		}
		if(item instanceof ItemStack)
		{
			if(!info.needDamage)
				((ItemStack) item).setItemDamage(0);
			if(!info.needNBT)
				((ItemStack) item).stackTagCompound = null;
		}
		if(item instanceof Block)
			return new ItemStack((Block) item);
		if(item instanceof Item)
			return new ItemStack((Item)item);
		return item;
	}*/
	
	public ItemStack[] ObjectoItemStack(Object object)
	{
		if(object instanceof Item){
			return new ItemStack[]{new ItemStack((Item) object)};
		}else if(object instanceof Block){
			return new ItemStack[]{new ItemStack((Block) object)};
		}else if(object instanceof ItemStack){
			return new ItemStack[]{((ItemStack) object).copy()};
		}else if(object instanceof List<?>){
			List stacks = (List) object;
			ItemStack[] result = new ItemStack[stacks.size()];
			for(int zahl = 0; zahl < stacks.size(); zahl++)
				if(stacks.get(zahl) instanceof ItemStack)
					result[zahl] = ((ItemStack) stacks.get(zahl)).copy();
					
			return result;
					
		}
		return new ItemStack[0];
	}
	
	/**X Position for the crafting  grid**/
	public int getMoveX()
	{
		return 40;
	}
	
	/**Y Position for the crafting  grid**/
	public int getMoveY()
	{
		return (int) (getHeight()/2 - Math.round(SlotInfo.slotSize*1.5D));
	}
	
	@Override
	public ArrayList<SlotInfo> getSlots(EntityPlayer player) {
		ArrayList<SlotInfo> result = new ArrayList<SlotInfo>();
		int moveX = getMoveX();
		int moveY = getMoveY();
		for (int i = 0; i < 9; i++) {
			result.add(new SlotInfo(0, moveX+SlotInfo.slotSize*(i-3*(i/3)), moveY+SlotInfo.slotSize*(int)(i/3)));
		}
		result.add(new SlotInfo(0, moveX+SlotInfo.slotSize*4, moveY+SlotInfo.slotSize));
		return result;
	}
}
