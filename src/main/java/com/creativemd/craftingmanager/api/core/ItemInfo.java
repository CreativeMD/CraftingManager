package com.creativemd.craftingmanager.api.core;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemInfo {
	
	public NBTTagCompound nbt;
	public int damage;
	public String ore;
	public String item;
	
	public ItemInfo(String item, String ore, int damage, NBTTagCompound nbt)
	{
		this.item = item;
		this.ore = ore;
		this.damage = damage;
		this.nbt = nbt;
	}
	
	public ItemInfo(ItemStack stack, String ore, boolean needDamage, boolean needNBT)
	{
		this(stack, needDamage, needNBT);
		this.ore = ore;
	}
	
	public ItemInfo(ItemStack stack, boolean needDamage, boolean needNBT)
	{
		if(stack == null)
			this.item = "";
		else
		{
			if(Block.getBlockFromItem(stack.getItem()) instanceof BlockAir)
				this.item = Item.itemRegistry.getNameForObject(stack.getItem());
			else
				this.item = Block.blockRegistry.getNameForObject(Block.getBlockFromItem(stack.getItem()));
		}
		if(needDamage)
			this.damage = stack.getItemDamage();
		else
			this.damage = -1;
		if(needNBT)
			this.nbt = stack.stackTagCompound;
		else
			this.nbt = null;
		this.ore = "";
	}
	
	public static ItemInfo getInfo(Object item)
	{
		if(item instanceof Item || item instanceof Block)
		{
			String name = "";
			if(item instanceof Block)
				name = Block.blockRegistry.getNameForObject(item);
			if(item instanceof Item)
				name = Item.itemRegistry.getNameForObject(item);
			return new ItemInfo(name, "", -1, null);
		}
		if(item instanceof ItemStack)
			return new ItemInfo((ItemStack)item);
		if(item instanceof String)
			return new ItemInfo((String)item);
		return null;
	}
	
	public ItemInfo(ItemStack stack)
	{
		this(stack, true, true);
	}
	
	public ItemInfo(String ore)
	{
		this.ore = ore;
		this.item = "";
		this.nbt = null;
		this.damage = -1;
	}
	
	public Block getBlock()
	{
		return (Block) Block.blockRegistry.getObject(item);
	}
	
	public boolean isBlock()
	{
		return !(getBlock() instanceof BlockAir);
	}
	
	public Item getItem()
	{
		return (Item) Item.itemRegistry.getObject(item);
	}
	
	public boolean isItem()
	{
		return getItem() != null;
	}
	
	public boolean isOre()
	{
		return !ore.equals("");
	}
	
	public boolean needDamage()
	{
		return damage != -1;
	}
	
	public boolean needNBT()
	{
		return nbt != null;
	}
	
	public ItemStack getItemStack()
	{
		if(isOre())
		{
			ArrayList<ItemStack> stacks = OreDictionary.getOres(ore);
			if (stacks.size() > 0)
				return stacks.get(0).copy();
		}else{
			ItemStack stack = null;
			if(isItem())
				stack = new ItemStack(getItem());
			else
				stack = new ItemStack(getBlock());
			if(stack.getItem() == null)
				return null;
			stack.stackSize = 1;
			stack.setItemDamage(damage);
			stack.stackTagCompound = nbt;
			return stack;
		}
		return null;
	}
}
