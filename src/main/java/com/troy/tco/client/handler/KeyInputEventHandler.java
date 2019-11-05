package com.troy.tco.client.handler;

import com.troy.tco.client.gui.GuiTest;
import com.troy.tco.client.settings.KeyBindings;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class KeyInputEventHandler
{

	@SubscribeEvent
	public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
	{
		if (KeyBindings.OPEN_GUI.isPressed())
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiTest());
		}
	}

}
