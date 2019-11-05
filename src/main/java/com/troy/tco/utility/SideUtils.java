package com.troy.tco.utility;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class SideUtils
{
	public static Side getSide(World world)
	{
		return world.isRemote ? Side.CLIENT : Side.SERVER;
	}

	public static Side getSide(MessageContext ctx)
	{
		if (ctx.netHandler instanceof NetHandlerPlayServer)
			return Side.SERVER;
		else
			return Side.CLIENT;
	}
}
