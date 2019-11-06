package com.troy.tco.client.gui;

import com.troy.tco.game.TCOKit;
import com.troy.tco.inventory.ContainerKitEditor;
import com.troy.tco.reference.Reference;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiKitEditor extends GuiContainer
{
	private TCOKit kit;

	public GuiKitEditor(InventoryPlayer inventory, TCOKit kit)
	{
		super(new ContainerKitEditor(inventory, kit));
		this.kit = kit;

		xSize = 177;
		ySize = 197;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		int guiX = (width - xSize) / 2;
		int guiY = (height - ySize) / 2;
		drawDefaultBackground();

		mc.renderEngine.bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/tco_kit.png"));
		drawTexturedModalRect(guiX, guiY, 0, 0, this.xSize, this.ySize);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		String name = "Kit \"" + kit.name + "\"";
		this.fontRendererObj.drawString(name, 8, 8, 0x404040);
		this.fontRendererObj.drawString("Inventory", 8, 103, 0x404040);

	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

}
