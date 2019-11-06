package com.troy.tco.client.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import com.troy.tco.game.TCOKit;
import com.troy.tco.net.*;
import com.troy.tco.reference.Reference;
import com.troy.tco.utility.TCO;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

public class GuiTest extends GuiScreen
{
	private static final int WIDTH = 300, HEIGHT = 175;

	private GuiTextField newName;

	@Override
	public void drawScreen(int x, int y, float ticks)
	{
		
		
		int guiX = (width - WIDTH) / 2;
		int guiY = (height - HEIGHT) / 2;
		drawDefaultBackground();

		mc.renderEngine.bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/test_gui.png"));

		drawTexturedModalRect(guiX, guiY, 0, 0, WIDTH, HEIGHT);

		super.drawScreen(x, y, ticks);
		newName.drawTextBox();

		fontRendererObj.drawString("TCO Setup", guiX + 5, guiY + 5, 0x404040);
		
		fontRendererObj.drawString("Kit Editor", guiX + 180, guiY + 5, 0x404040);

	}

	@Override
	public void initGui()
	{
		buttonList.clear();
		System.out.println(height);
		int guiX = (width - WIDTH) / 2;
		int guiY = (height - HEIGHT) / 2;
		int x = guiX + 180;
		int y = guiY + 20;
		int nameWidth = 80;
		for (TCOKit kit : TCOStorage.get().getKits())
		{
			buttonList.add(new TCOKitButton(kit.id, x, y, Math.min(nameWidth - 5, fontRendererObj.getStringWidth(kit.name) + 8), fontRendererObj.FONT_HEIGHT + 3, kit.name));
			buttonList.add(new GuiButton(kit.id, x + nameWidth, y, 15, fontRendererObj.FONT_HEIGHT + 3, "X"));
			y += fontRendererObj.FONT_HEIGHT + 5;
		}
		y += 5;
		newName = new GuiTextField(fontRendererObj, x, y, 90, 18);
		y += 20;
		buttonList.add(new GuiButton(0, x - 2, y, 95, 15, "Create New Kit"));
		super.initGui();
	}

	private static class TCOKitButton extends GuiButton
	{

		public TCOKitButton(int id, int x, int y, int width, int height, String name)
		{
			super(id, x, y, width, height, name);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{

		if (button.displayString.equals("X"))
		{
			TCOStorage.get().deleteKit(button.id);
			TCOPackets.HANDLER.sendToServer(new TCOPackets.TCOKitDeleted(button.id));
			initGui();
		} else if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
		{
			if (button instanceof TCOKitButton)
			{
				TCOPackets.HANDLER.sendToServer(new TCOPackets.TCOKitGuiOpened(button.id));
			} else if (button.displayString.equals("Create New Kit") && !newName.getText().isEmpty())
			{
				System.out.println("Creating kit");
				TCOKit kit = new TCOKit(newName.getText(), 0);
				TCOStorage.get().updateKit(kit);
				TCOPackets.HANDLER.sendToServer(new TCOPackets.TCOKitCreated(kit.name));
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected void keyTyped(char key, int other)
	{
		super.keyTyped(key, other);
		this.newName.textboxKeyTyped(key, other);
	}

	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		this.newName.mouseClicked(x, y, button);
	}

}
