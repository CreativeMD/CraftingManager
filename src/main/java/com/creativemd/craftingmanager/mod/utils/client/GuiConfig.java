package com.creativemd.craftingmanager.mod.utils.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.craftingmanager.api.common.utils.gui.GuiButtonState;
import com.creativemd.craftingmanager.api.common.utils.gui.GuiInput;
import com.creativemd.craftingmanager.api.core.ConfigEntry;
import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.api.core.ConfigTab;
import com.creativemd.craftingmanager.api.core.ItemInfo;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.packets.GuiPopupPacket;
import com.creativemd.craftingmanager.mod.utils.server.ContainerConfig;
import com.creativemd.craftingmanager.mod.utils.server.SlotInput;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfig extends GuiContainer{
	
	public static final ResourceLocation background = new ResourceLocation(CraftingManagerMod.modid + ":textures/gui/background.png");
	public static final ResourceLocation popbackground = new ResourceLocation(CraftingManagerMod.modid + ":textures/gui/ManagerAddItem.png");
	
	public ContainerConfig container;
	
	public GuiInput textBox;
	public String InputText = "";
	
	public int lastTabX;
	public int lastTabY;
	
	public static boolean notClosing = false;
	
	public int popupIndex;
	public ItemStack popStack;
	
	public static final int tabsOnPage = 18;
	
	public int tabPage;
	public static int tabPages;
	
	public GuiConfig(EntityPlayer player, int id)
	{
		super(new ContainerConfig(player, id));
		
		container = (ContainerConfig) inventorySlots;
		container.gui = this;
		
		tabPages = ConfigTab.tabs.size() / tabsOnPage + 1;
		tabPage = container.tabIndex/tabsOnPage;
		
		popupIndex = -1;
		xSize = 255;
		ySize = 212;
		if(container.system != null && container.system.hasInputField())
			createInputField();
		updateButtons();
		container.updateSearchResult();
	}
	
	public void createInputField()
	{
		textBox = new GuiInput(Minecraft.getMinecraft().fontRenderer, 6, 192, 13, 243);
		textBox.setMaxStringLength(100);
		textBox.setFocused(true);
		textBox.setText(InputText);
	}
	
	public void popUp(int index, ItemStack stack)
	{
		popupIndex = index;
		popStack = stack;
		popStack.stackSize = 1;
		container.popup = true;
		PacketHandler.sendPacketToServer(new GuiPopupPacket(true));
		initGui();
	}
	
	public boolean hasPopup()
	{
		return popupIndex != -1;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		buttonList.clear();
		buttonList.add(new GuiButton(0, this.width/2-120, this.height/2+58, 10, 20, "<"));
		buttonList.add(new GuiButton(1, this.width/2+110, this.height/2+58, 10, 20, ">"));
		buttonList.add(new GuiButton(2, this.width/2-120, this.height/2-106, 10, 20, "<"));
		buttonList.add(new GuiButton(3, this.width/2+110, this.height/2-106, 10, 20, ">"));
		buttonList.add(new GuiButton(4, this.width/2-150, this.height/2-130, 20, 20, "<"));
		buttonList.add(new GuiButton(5, this.width/2+130, this.height/2-130, 20, 20, ">"));
		GuiButtonState buttonState = new GuiButtonState(6, this.width/2-50, this.height/2+100, 100, 20, "Show Everything", "Show Only Output", "Show Nothing");
		buttonState.setIndex(container.showState);
		buttonList.add(buttonState);
		
		if(container.system != null && container.system.hasInputField())
			createInputField();
		if(hasPopup())
			initFront();
	}
	
	public void initFront()
	{
		SlotInput slot = (SlotInput) inventorySlots.getSlot(popupIndex);
		if(slot.info == null)
			slot.info = new ItemInfo(slot.getStack(), false, false);
		buttonList.add(new GuiButton(7, this.width/2+51, this.height/2-10, 35, 20, "Done"));
		buttonList.add(new GuiButton(8, this.width/2+5, this.height/2-10, 45, 20, "Remove"));
		buttonList.add(new GuiButtonState(9, this.width/2-80, this.height/2-30, 80, 20, getStringFromBoolean(slot.info.needDamage()) + " Damage", getStringFromBoolean(!slot.info.needDamage()) + " Damage"));
		buttonList.add(new GuiButtonState(10, this.width/2+5, this.height/2-30, 80, 20, getStringFromBoolean(slot.info.needNBT()) + " NBT", getStringFromBoolean(!slot.info.needNBT()) + " NBT"));
		buttonList.add(new GuiButton(11, this.width/2-80, this.height/2-10, 80, 20, slot.info.ore));
		if(slot.info.ore.equals(""))
			((GuiButton)buttonList.get(11)).displayString = "No Ore";
	}
	
	public void updateButtons()
	{
		int height = 0;
		for (int i = 0; i < container.entryList.size(); i++) {
			container.entryList.get(i).buttons.clear();
			List list = container.entryList.get(i).getButtons();
			if(list != null)
				container.entryList.get(i).buttons.addAll(list);
			container.entryList.get(i).inputs.clear();
			list = container.entryList.get(i).getTextFields(Minecraft.getMinecraft().fontRenderer);
			if(list != null)
				container.entryList.get(i).inputs.addAll(list);
		}
	}
	
	public static String getStringFromBoolean(boolean Input)
	{
		if(Input)
			return "Need";
		else
			return "No";
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
		try
		{
			super.drawScreen(par1, par2, par3);
		}catch(Exception e){
			if(container.system != null)
				System.out.println("Rendering of " + container.system.name + " failed: " + e.getMessage());
			//Minecraft.getMinecraft().rel
			return ;
		}
        if(hasPopup())
        {
        	this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
        	int k = this.guiLeft;
            int l = this.guiTop;
            this.drawDefaultBackground();
	        GL11.glTranslatef((float)k, (float)l, 0.0F);
	        GL11.glDisable(GL11.GL_LIGHTING);
	        this.drawGuiContainerForegroundLayer(par1, par2);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        this.zLevel = 0.0F;
	        itemRender.zLevel = 0.0F;

        }
    }
	
	public ConfigTab getTab()
	{
		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int x = Mouse.getEventX() * i / mc.displayWidth;
        x -= (i - xSize) / 2;
        int y = j - Mouse.getEventY() * j / mc.displayHeight - 1;
        y -= (j - ySize) / 2;
        lastTabX = x;
        lastTabY = y;
        //System.out.println("X:" + x + " Y:" + y);
		for (int q = 0; q < tabsOnPage; q++) {
			int index = tabPage*tabsOnPage+q;
			if(index < ConfigTab.tabs.size())
			{
				int posX = q*ConfigTab.width;
				boolean down = q >= tabsOnPage/2;
				if(down)
					posX -= tabsOnPage/2*ConfigTab.width;
				if((!down && y < 0 && y >= -ConfigTab.height) | (down && y > ySize && y <= ySize+ConfigTab.height))
					if(x >= posX && x <= posX+ConfigTab.width)
						return ConfigTab.tabs.get(index);
			}
		}
		return null;
	}
	
	@Override    
    protected void mouseClicked(int par1, int par2, int par3)
    {
    	super.mouseClicked(par1, par2, par3);	
    	
    	if(container.system != null && container.system.hasInputField())
    		textBox.mouseClicked(par1-(this.width/2-xSize/2), par2-(this.height/2-ySize/2), par3);
    	
    	for(int zahl = 0; zahl < buttonList.size(); zahl++)
    	{
    		GuiButton button = (GuiButton) buttonList.get(zahl);
    		if(button.mousePressed(mc, par1, par2))
    			buttonClicked(button, zahl);
    	}
    	
    	int height = ContainerConfig.pageStart;
    	for (int i = 0; i < container.currentList.size(); i++) {
			for (int q = 0; q < container.currentList.get(i).buttons.size(); q++) {
				GuiButton button = (GuiButton) container.currentList.get(i).buttons.get(q);
	    		if(button.mousePressed(mc, par1-(this.width/2-xSize/2+ConfigEntry.getX()), par2-(this.height/2-ySize/2+height)))
	    		{
	    			if(button instanceof GuiButtonState)
	    				((GuiButtonState) button).buttonClicked();
	    			container.currentList.get(i).buttonClicked(q, button);
	    			container.system.onEntryChange(container.currentList.get(i));
	    			button.func_146113_a(this.mc.getSoundHandler());
	    		}
			}
			for (int q = 0; q < container.currentList.get(i).inputs.size(); q++) {
				container.currentList.get(i).inputs.get(q).mouseClicked(par1-(this.width/2-xSize/2+ConfigEntry.getX()), par2-(this.height/2-ySize/2+height), par3);
			}
			height += container.currentList.get(i).getHeight() + ConfigEntry.space;
		}
    	
    	ConfigTab tab = getTab();
    	if(tab != null)
    	{
    		CraftingManagerMod.showGui(container.player, ConfigRegistry.systems.size() + ConfigTab.getIndexOfTab(tab), false);
    	}
    }
	
	public void buttonClicked(GuiButton button, int id)
    {
		if(!hasPopup())
		{
			switch(id)
			{
			case 0:
				container.setPage(container.getPage()-1);
				if(container.getPage() < 0)container.setPage(container.pages-1);
				if(container.getPage() < 0)container.setPage(0);
				break;
			case 1:
				container.setPage(container.getPage()+1);
				if(container.getPage() >= container.pages)container.setPage(0);
				break;
			case 2:
			case 3:
				int index = -1;
				for (int i = 0; i < container.tabConfigs.size(); i++) {
					if(container.tabConfigs.get(i) == container.system)
						index = i;
				}
				if(id == 2)
					index--;
				else
					index++;
				if(index < 0)
					index = container.tabConfigs.size()-1;
				if(index >= container.tabConfigs.size())
					index = 0;
				if(index >= 0 && index < container.tabConfigs.size())
					CraftingManagerMod.showGui(container.player, container.tabConfigs.get(index).getID(), true);
				break;
			case 4:
				tabPage--;
			case 5:
				if(id == 5)
					tabPage++;
				if(tabPage < 0)
					tabPage = tabPages-1;
				if(tabPage >= tabPages)
					tabPage = 0;
				break;
			case 6:
				((GuiButtonState)buttonList.get(id)).buttonClicked();
				container.showState = ((GuiButtonState)buttonList.get(id)).getIndex();
				CraftingManagerMod.showGui(null, container.system.getID(), true, container.getPage());
				break;
			}
		}else{
			switch(id)
			{
    		case 7:
    			popStack.stackSize = 1;
    			((Slot)container.inventorySlots.get(popupIndex)).putStack(popStack.copy());
    			((SlotInput)container.inventorySlots.get(popupIndex)).info = new ItemInfo(popStack.copy(), ((GuiButton)buttonList.get(11)).displayString.replace("No Ore", ""), ((GuiButton)buttonList.get(9)).displayString.contains("Need"),
    					((GuiButton)buttonList.get(10)).displayString.contains("Need"));
    		case 8:
    			if(id == 8)
    			{
	    			((SlotInput)container.inventorySlots.get(popupIndex)).info = null;
					((Slot)container.inventorySlots.get(popupIndex)).putStack(null);
    			}
				
    			popupIndex = -1;
    			popStack = null;
    			
    			container.popup = false;
    			PacketHandler.sendPacketToServer(new GuiPopupPacket(false));
    				
    			while(buttonList.size() > 7)
    				buttonList.remove(7);    			
    			break;
    		case 9:
    		case 10:
    			if(((GuiButton)buttonList.get(id)).displayString.contains("Need"))
    				((GuiButton)buttonList.get(id)).displayString = ((GuiButton)buttonList.get(id)).displayString.replace("Need", "No");
    			else
    				((GuiButton)buttonList.get(id)).displayString = ((GuiButton)buttonList.get(id)).displayString.replace("No", "Need");
    			break;
    		case 11:
    			int[] ores = OreDictionary.getOreIDs(popStack);
    			int ore = OreDictionary.getOreID(((GuiButton)buttonList.get(id)).displayString);
    			int index = -1;
    			for (int i = 0; i < ores.length; i++) {
					if(ores[i] == ore)
						index = i;
				}
    			index++;
    			if(index >= ores.length)
    				index = -1;
    			if(index == -1)
    				((GuiButton)buttonList.get(id)).displayString = "No Ore";
    			else
    				if(((GuiButton)buttonList.get(id)).displayString == "No Ore")
    					((GuiButton)buttonList.get(id)).displayString = OreDictionary.getOreName(ores[index]);
    				else ((GuiButton)buttonList.get(id)).displayString = "No Ore";
    			break;
			}
		}
    }
	
	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
		
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		
		for (int q = 0; q < tabsOnPage; q++) {
			int index = tabPage*tabsOnPage+q;
			if(index < ConfigTab.tabs.size())
				ConfigTab.tabs.get(index).drawTab(k, l, index == container.tabIndex, q);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 30);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(k, l+20, 0, 0, this.xSize, this.ySize-20);
		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            this.drawTexturedModalRect(k+slot.xDisplayPosition-1, l+slot.yDisplayPosition-1, 0, 192, 18, 18);
        }
		
		
    }
	
	@Override
	public void keyTyped(char par1, int par2)
    {
		if(container.system != null && container.system.hasInputField())
		{
			InputText = textBox.getText();
			textBox.textboxKeyTyped(par1, par2);
			if(textBox.isFocused() && !InputText.equals(textBox.getText()))
			{
				InputText = textBox.getText();
				container.search = textBox.getText();
				container.updateSearchResult();
				container.setPage(container.getPage());
				return ;
			}
    	}
		
    	for (int i = 0; i < container.currentList.size(); i++) {
			for (int q = 0; q < container.currentList.get(i).inputs.size(); q++) {
				GuiInput input = container.currentList.get(i).inputs.get(q);
				if(input.isFocused() && input.textboxKeyTyped(par1, par2))
				{
					container.currentList.get(i).onInputChange(input);
					container.system.onEntryChange(container.currentList.get(i));
					return ;
				}
			}
		}
    	
    	if(par2 != Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode())
			super.keyTyped(par1, par2);
    }
	
	@Override
	public void onGuiClosed()
    {
		if(!notClosing)
		{
		    super.onGuiClosed();
		    CraftingManagerMod.sendUpdateToServer();
		}else{
			GuiConfig.notClosing = false;
		}
    }
	
	@Override                                   
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{       
		this.fontRendererObj.drawString("Page " + (container.getPage()+1) + " of " + container.pages, 30, 170, 0);
		if(container.system != null)
			this.fontRendererObj.drawString(container.system.name, 30, 5, 0);
		else
			this.fontRendererObj.drawString("No ConfigSystem found", 30, 5, 0);
		if(container.system != null && container.system.hasInputField())
		{
			this.textBox.drawTextBox();  
		}
		
		int height = ContainerConfig.pageStart;
		for (int i = 0; i < container.currentList.size(); i++) {
			container.currentList.get(i).drawEntry(fontRendererObj, ConfigEntry.getX(), height);
			for (int q = 0; q < container.currentList.get(i).buttons.size(); q++) {
				renderButton(container.currentList.get(i).buttons.get(q), false, this.width/2-xSize/2+ConfigEntry.getX(), this.height/2-ySize/2+height);
			}
			for (int q = 0; q < container.currentList.get(i).inputs.size(); q++) {
				renderInput(container.currentList.get(i).inputs.get(q), ConfigEntry.getX(), height);
			}
			height += container.currentList.get(i).getHeight() + ConfigEntry.space;
		}
		
		ConfigTab tab = getTab();
		if(tab != null)
		{
			drawCreativeTabHoveringText(tab.name, lastTabX, lastTabY);
			RenderHelper.enableGUIStandardItemLighting();
		}
		if(hasPopup())
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(popbackground);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(35, 50, 0, 0, 200, 70);
			if(popStack  != null)
			{
				String NBT = "null";
				if(popStack.stackTagCompound != null)
					NBT = popStack.stackTagCompound.toString();
				GL11.glTranslated(0, 0, 500);
				this.fontRendererObj.drawString("itemID: " + Item.getIdFromItem(popStack.getItem()) + "; damage:" + popStack.getItemDamage() + "; stackSize:" + popStack.stackSize, 37, 52, 0);
				this.fontRendererObj.drawString("NBT: " + NBT, 37, 60, 0);
				GL11.glTranslated(0, 0, -500);
				//Tessellator.instance.zOffset = 0;
				//this.zLevel = 0;
			}
			
			//Render Foreground buttons
			for (int i = 7; i < buttonList.size(); i++) {
				renderButton((GuiButton) buttonList.get(i), true, 0, 0);
			}
		}
	}
	
	public void renderInput(GuiInput input, int xOffzet, int yOffzet)
	{
		input.xPosition += xOffzet;
		input.yPosition += yOffzet;
		input.drawTextBox();
		input.xPosition -= xOffzet;
		input.yPosition -= yOffzet;
	}
	
	
	public void renderButton(GuiButton button, boolean front, int xOffzet, int yOffzet)
	{
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		button.xPosition += xOffzet;
		button.yPosition += yOffzet;
		int oldX = button.xPosition;
		int oldY = button.yPosition;
		button.xPosition = oldX-k;
		button.yPosition = oldY-l;
		ScaledResolution scaledresolution = new ScaledResolution(mc, this.mc.displayWidth, this.mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int k2 = Mouse.getX() * i / this.mc.displayWidth;
        int l2 = j - Mouse.getY() * j / this.mc.displayHeight - 1;
        if(front)
        	GL11.glTranslated(0, 0, 500);
        button.drawButton(mc, k2-k, l2-l);
        if(front)
        	GL11.glTranslated(0, 0, -500);
		
		button.xPosition = oldX;
		button.yPosition = oldY;
		button.xPosition -= xOffzet;
		button.yPosition -= yOffzet;
	}
	
	public void func_146977_a(Slot slot)
	{
		ItemStack stack = slot.getStack();
		slot.putStack(null);
        try
        {
            func_146977_a(slot);
            
            //GL11.glEnable(GL11.GL_DEPTH_TEST);
            
            /*if (Container.func_94527_a(p_146977_1_, stack, true) && this.inventorySlots.canDragIntoSlot(p_146977_1_))
            {
            	stack = itemstack1.copy();
                boolean flag = true;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, itemstack, p_146977_1_.getStack() == null ? 0 : p_146977_1_.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > p_146977_1_.getSlotStackLimit())
                {
                    s = EnumChatFormatting.YELLOW + "" + p_146977_1_.getSlotStackLimit();
                    itemstack.stackSize = p_146977_1_.getSlotStackLimit();
                }
            }*/
            if(stack != null)
            {
	            String s = null;
	            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, slot.xDisplayPosition, slot.yDisplayPosition);
	            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, slot.xDisplayPosition, slot.yDisplayPosition, s);
            }
        }catch(Exception e){

        	this.zLevel = 100.0F;
            itemRender.zLevel = 100.0F;
            
        	ResourceLocation resourcelocation = Minecraft.getMinecraft().renderEngine.getResourceLocation(1);
            IIcon icon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
            
            /*GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);*/
            
            Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
            
        	itemRender.renderIcon(slot.xDisplayPosition, slot.yDisplayPosition, icon, 16, 16);
        	
        	/*GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);*/
        	

            itemRender.zLevel = 0.0F;
            this.zLevel = 0.0F;
        }
        slot.putStack(stack);
    }

}
