package com.creativemd.craftingmanager.mod.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import scala.collection.parallel.ParIterableLike.Min;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.creativemd.craftingmanager.api.core.ConfigRegistry;
import com.creativemd.craftingmanager.api.core.ConfigSystem;
import com.creativemd.craftingmanager.mod.CraftingManagerMod;
import com.creativemd.craftingmanager.mod.utils.packets.CraftResultPacket;
import com.creativemd.craftingmanager.mod.workbench.change.WorkbenchSwitchHelper;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler {
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if(event.player instanceof EntityPlayerMP && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			CraftingManagerMod.sendUpdateToClient(event.player);
		}
	}
	
	public static boolean clicked = false;
	
	public static int index = 0;
	
	public static ItemStack[] craftMatrix = new ItemStack[9];
	
	public static ArrayList<ItemStack> recipes = new ArrayList<ItemStack>();
	
	public static ArrayList<PacketRequest> packetrequest = new ArrayList<PacketRequest>();
	
	public static int maxPacketsPerTick = 10;
	
	@SubscribeEvent
	public void tick(TickEvent event)
	{
		if(event.phase == Phase.START && (event.type == Type.CLIENT || event.type == Type.SERVER))
		{
			for (int i = 0; i < maxPacketsPerTick; i++) {
				PacketRequest request = null;
				if(packetrequest.size() > 0)
					request = packetrequest.get(0);
				if(request != null)
				{
					boolean issended = false;
					if(request.toServer && event.side == Side.CLIENT)
					{
						PacketHandler.sendPacketToServer(request.packet);
						issended = true;
					}else if(!request.toServer && event.side == Side.SERVER){
						if(request.player == null)
							PacketHandler.sendPacketToAllPlayers(request.packet);
						else
							PacketHandler.sendPacketToPlayer(request.packet, (EntityPlayerMP) request.player);
						issended = true;
					}
					if(packetrequest.size() > 0 && issended)
						packetrequest.remove(0);
				}
				
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tick(RenderTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(event.phase == Phase.END && mc.thePlayer != null && mc.thePlayer.openContainer instanceof ContainerWorkbench)
		{
			ContainerWorkbench container = (ContainerWorkbench) mc.thePlayer.openContainer;
			boolean changed = false;
			for (int i = 0; i < craftMatrix.length; i++) {
				if(craftMatrix[i] != container.craftMatrix.getStackInSlot(i))
				{
					changed = true;
					craftMatrix[i] = container.craftMatrix.getStackInSlot(i);
				}
			}
			if(changed)
			{
				index = 0;
				recipes.clear();
				recipes.addAll(WorkbenchSwitchHelper.findMatchingRecipe(container.craftMatrix, mc.theWorld));
				for (int i = 0; i < recipes.size(); i++) {
					if(recipes.get(i).stackSize == 0)
						recipes.get(i).stackSize = 1;
				}
			}
			if(recipes.size() > 0)
			{
				if(recipes.size() > 1)
				{
					GuiButton button = new GuiButton(0, mc.currentScreen.width/2+20, mc.currentScreen.height/2-25, 50, 20, "Switch");
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_LIGHTING);
					renderButton(mc.currentScreen, button, false, 0, 0);
					ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			        int i = scaledresolution.getScaledWidth();
			        int j = scaledresolution.getScaledHeight();
					mc.fontRenderer.drawString((index+1) + " of " + recipes.size(), i/2+30, j/2-65, 0);
					GL11.glPopMatrix();
					if(!ItemStack.areItemStacksEqual(container.craftResult.getStackInSlot(0), recipes.get(index)))
					{
						//container.craftResult.setInventorySlotContents(0, recipes.get(index).copy());
						PacketHandler.sendPacketToServer(new CraftResultPacket(index, recipes.get(index)));
					}
					if(Mouse.getEventButtonState() && Mouse.getEventButton() == 0)
					{
						if(!clicked)
						{
							//Mouse clicked
							int x = Mouse.getEventX() * i / mc.displayWidth;
					        int y = j - Mouse.getEventY() * j / mc.displayHeight - 1;
							clicked = true;
							if(button.mousePressed(mc, x, y))
							{
								int newindex = index+1;
								if(newindex >= recipes.size())
									newindex = 0;
								PacketHandler.sendPacketToServer(new CraftResultPacket(newindex, recipes.get(newindex)));
							}
						}
					}else
						clicked = false;
				}
			}else
				container.craftResult.setInventorySlotContents(0, null);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderButton(GuiScreen screen, GuiButton button, boolean front, int xOffzet, int yOffzet)
	{
		Minecraft mc = Minecraft.getMinecraft();
		int k = 0;//(screen.width - 176) / 2;
		int l = 0;//(screen.height - 166) / 2;
		button.xPosition += xOffzet;
		button.yPosition += yOffzet;
		int oldX = button.xPosition;
		int oldY = button.yPosition;
		button.xPosition = oldX-k;
		button.yPosition = oldY-l;
		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int k2 = Mouse.getX() * i / mc.displayWidth;
        int l2 = j - Mouse.getY() * j / mc.displayHeight - 1;
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
	
	public static class PacketRequest {
		public EntityPlayer player;
		public CreativeCorePacket packet;
		public boolean toServer;
		
		public PacketRequest(EntityPlayer player, CreativeCorePacket packet, boolean toServer)
		{
			this.player = player;
			this.packet = packet;
			this.toServer = toServer;
		}
	}

}
