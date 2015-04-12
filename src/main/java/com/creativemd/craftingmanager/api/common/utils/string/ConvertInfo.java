package com.creativemd.craftingmanager.api.common.utils.string;

import net.minecraft.nbt.NBTTagCompound;

import com.creativemd.craftingmanager.api.core.ItemInfo;

public class ConvertInfo extends StringConverter{

	public ConvertInfo() {
		super("ItemInfo");
	}

	@Override
	public Class getClassOfObject() {
		return ItemInfo.class;
	}

	@Override
	public String toString(Object object) {
		ItemInfo info = (ItemInfo) object;
		if(info.nbt != null)
			return StringUtils.ObjectsToString(info.damage, info.item, info.ore, info.nbt);
		else
			return StringUtils.ObjectsToString(info.damage, info.item, info.ore, "");
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length == 4 && objects[0] instanceof Integer && objects[1] instanceof String && objects[2] instanceof String && (objects[3] instanceof NBTTagCompound || objects[3] instanceof String))
		{
			if(objects[3] instanceof NBTTagCompound)
				return new ItemInfo((String)objects[1], (String)objects[2], (Integer)objects[0], (NBTTagCompound)objects[3]);
			else
				return new ItemInfo((String)objects[1], (String)objects[2], (Integer)objects[0], null);
		}
		return null;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
