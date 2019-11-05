package com.troy.tco.client.handler;

import java.util.HashMap;

import com.troy.tco.client.gui.GuiKitEditor;
import com.troy.tco.game.TCOKit;
import com.troy.tco.inventory.ContainerKitEditor;
import com.troy.tco.net.TCOStorage;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TCOGuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (x == -1 && y == -1 && z == -1 && player.capabilities.isCreativeMode)
		{
			return new ContainerKitEditor(player.inventory, TCOStorage.get().getKit(ID));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (x == -1 && y == -1 && z == -1 && player.capabilities.isCreativeMode)
			return new GuiKitEditor(player.inventory, TCOStorage.get().getKit(ID));
		return null;
	}

}
