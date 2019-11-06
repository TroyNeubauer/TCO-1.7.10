package com.troy.tco.net;

import java.util.*;

import com.troy.tco.game.TCOKit;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class TCOStorage extends WorldSavedData
{
	private static TCOStorage CLIENT_INSTANCE = null;
	private static TCOStorage SERVER_INSTANCE = null;

	private HashMap<Integer, TCOKit> kits = new HashMap<Integer, TCOKit>();
	// public ArrayList<TCOMap> maps;

	private int nextKitID = 1;

	public TCOStorage()
	{
		super(DATA_NAME);
	}

	public TCOStorage(String name)
	{
		super(name);
	}

	private static final String DATA_NAME = "TCO_DATA";

	private static TCOStorage load(World world)
	{
		// The IS_GLOBAL constant is there for clarity, and should be simplified into
		// the right branch.
		MapStorage storage = world.perWorldStorage;
		TCOStorage instance = (TCOStorage) storage.loadData(TCOStorage.class, DATA_NAME);

		if (instance == null)
		{
			instance = new TCOStorage();
			System.out.println("Creating default TCOStorage instance");
			storage.setData(DATA_NAME, instance);
		} else
		{
			System.out.println("Loaded " + instance.kits.size() + " TCO kits from the world save");
		}
		return instance;

	}

	public Collection<TCOKit> getKits()
	{
		return kits.values();
	}

	private static final String KITS = "kits", NEXT_KIT_ID = "next_kit_id";

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.nextKitID = nbt.getInteger(NEXT_KIT_ID);
		NBTTagList list = nbt.getTagList(KITS, NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++)
		{
			TCOKit kit = new TCOKit();
			kit.readFromNBT(list);
			kits.put(kit.id, kit);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong(NEXT_KIT_ID, nextKitID);
		NBTTagList list = new NBTTagList();
		for (TCOKit kit : kits.values())
		{
			kit.writeToNBT(list);
		}
		nbt.setTag(KITS, list);
	}

	public void updateKit(TCOKit kit)
	{
		markDirty();
		if (kits.containsKey(kit.id))
		{
			TCOKit real = kits.get(kit.id);
			real.name = kit.name;
			real.maxUsages = kit.maxUsages;

			real.inventory = kit.inventory;
			real.armor = kit.armor;
		} else
		{
			kits.put(kit.id, kit);
		}
	}

	public synchronized int nextKitID()
	{
		markDirty();
		return nextKitID++;
	}

	public void deleteKit(int ID)
	{
		markDirty();
		kits.remove(ID);
	}

	public TCOKit getKit(int ID)
	{
		return kits.get(ID);
	}

	public static TCOStorage get()
	{
		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
		{
			if (SERVER_INSTANCE == null)
				SERVER_INSTANCE = loadServer();
			return SERVER_INSTANCE;
		} else if (Thread.currentThread().getName().contains("Client"))
		{
			if (CLIENT_INSTANCE == null)
				CLIENT_INSTANCE = loadClient();
			return CLIENT_INSTANCE;
		} else
		{
			System.err.println("Accessing TCOStorage from non-standard thread: " + Thread.currentThread().getName());
		}
		return null;
	}

	private static TCOStorage loadServer()
	{
		return load(MinecraftServer.getServer().getEntityWorld());
	}

	@SideOnly(Side.CLIENT)
	private static TCOStorage loadClient()
	{
		return load(Minecraft.getMinecraft().theWorld);
	}

}
