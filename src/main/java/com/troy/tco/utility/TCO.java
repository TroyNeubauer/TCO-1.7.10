package com.troy.tco.utility;

import com.troy.tco.client.handler.*;
import com.troy.tco.net.TCOStorage;
import com.troy.tco.proxy.*;
import com.troy.tco.reference.Reference;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.world.WorldEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class TCO
{
	@Mod.Instance(Reference.MOD_ID)
	public static TCO instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		proxy.registerKeyBindings();

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		FMLCommonHandler.instance().bus().register(this);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new TCOGuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{

	}

}
