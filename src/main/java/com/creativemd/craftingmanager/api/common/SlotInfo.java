package com.creativemd.craftingmanager.api.common;

import net.minecraft.inventory.IInventory;

public class SlotInfo {
	
	public int posX;
	public int posY;
	/**0: SlotShow; 1: SlotInput; 2: SlotOuput; 3: SlotNormal**/
	public int type;
	
	/**Can be null; Note: if type = 1 this have to be null**/
	public IInventory inventory = null;
	
	/**Can be -1 or the index of the inventory**/
	public int index = -1;
	
	public static final int slotSize = 18;
	
	public SlotInfo(int type, int posX, int posY)
	{
		this.type = type;
		this.posX = posX;
		this.posY = posY;
	}
	
}
