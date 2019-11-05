package com.troy.tco.proxy;

import com.troy.tco.client.handler.KeyInputEventHandler;
import com.troy.tco.client.settings.KeyBindings;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{

	@Override
	public void registerKeyBindings()
	{
		ClientRegistry.registerKeyBinding(KeyBindings.OPEN_GUI);
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
	}

}
