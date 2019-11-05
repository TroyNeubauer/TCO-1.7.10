package com.troy.tco.proxy;

import com.troy.tco.net.TCOPackets;

public abstract class CommonProxy implements IProxy
{
	@Override
	public void registerNetworking()
	{
		TCOPackets.init();
		
	}
}
