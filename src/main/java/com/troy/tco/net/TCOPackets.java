package com.troy.tco.net;

import com.troy.tco.game.TCOKit;
import com.troy.tco.reference.Reference;
import com.troy.tco.utility.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;

public class TCOPackets
{

	public static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	private static int id = 0;

	static
	{
		HANDLER.registerMessage(TCOKitModifiedHandler.class, TCOKitModified.class, id++, Side.SERVER);
		HANDLER.registerMessage(TCOKitModifiedHandler.class, TCOKitModified.class, id++, Side.CLIENT);

		HANDLER.registerMessage(TCOKitApprovedHandler.class, TCOKitApproved.class, id++, Side.CLIENT);
		HANDLER.registerMessage(TCOKitGuiOpenedHandler.class, TCOKitGuiOpened.class, id++, Side.SERVER);
	}

	public static void init()
	{
		// Nop. Call static initializer
		System.out.println("Registered packets and packet handlers");
	}

	public static class TCOKitModifiedHandler implements IMessageHandler<TCOKitModified, TCOKitApproved>
	{// Updates the kit on both sides
		@Override
		public TCOKitApproved onMessage(TCOKitModified message, MessageContext ctx)
		{
			if (SideUtils.getSide(ctx).equals(Side.SERVER) && message.kit.id == -1)// This is the server
			{
				int id = TCOStorage.get().nextKitID();
				message.kit.id = id;// Assign the kit the next id
				System.out.println("Server assigning id: " + id + " to kit name " + message.kit.name);
				for (Object player : MinecraftServer.getServer().getEntityWorld().playerEntities)// Send to the other players
				{
					if (!ctx.getServerHandler().playerEntity.equals(player))
						HANDLER.sendTo(new TCOKitModified(message.kit), ((EntityPlayerMP) player));
				}
				TCOStorage.get().updateKit(message.kit);
				ctx.getServerHandler().playerEntity.openGui(TCO.instance, id, ctx.getServerHandler().playerEntity.worldObj, -1, -1, -1);
				return new TCOKitApproved(id);
			}
			System.out.println("Client updating kit " + message.kit.name);
			TCOStorage.get().updateKit(message.kit);
			return null;
		}
	}

	public static class TCOKitApprovedHandler implements IMessageHandler<TCOKitApproved, IMessage>
	{

		@Override
		public IMessage onMessage(TCOKitApproved message, MessageContext ctx)
		{
			TCOKit kit = TCOStorage.get().getKit(-1);
			if (kit == null)
				System.err.println("Client isnt waiting for a kit to be approved. New id is: " + message.id);
			else
			{
				kit.id = message.id;
				TCOStorage.get().deleteKit(-1);
				TCOStorage.get().updateKit(kit);
				System.out.println("Client created kit name " + kit.name + " has been assigned id " + message.id);
			}
			return null;
		}

	}

	public static class TCOKitGuiOpenedHandler implements IMessageHandler<TCOKitGuiOpened, IMessage>
	{

		@Override
		public IMessage onMessage(TCOKitGuiOpened message, MessageContext ctx)
		{
			ctx.getServerHandler().playerEntity.openGui(TCO.instance, message.id, ctx.getServerHandler().playerEntity.worldObj, -1, -1, -1);
			return null;
		}

	}

	public static class TCOKitGuiOpened implements IMessage
	{

		public int id;

		public TCOKitGuiOpened()
		{

		}

		public TCOKitGuiOpened(int id)
		{
			this.id = id;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.id = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(id);
		}

	}

	public static class TCOKitModified implements IMessage
	{

		private TCOKit kit;

		public TCOKitModified()
		{
		}

		public TCOKitModified(TCOKit kit)
		{
			if (Minecraft.getMinecraft().theWorld.isRemote)
			{
				System.out.println("Client waiting for kit " + kit.name + " to get its id");
			}
			this.kit = kit;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			NBTTagCompound nbt = ByteBufUtils.readTag(buf);

			NBTTagList list = (NBTTagList) nbt.getTag("data");
			kit = new TCOKit();
			kit.readFromNBT(list);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			NBTTagList list = new NBTTagList();
			kit.writeToNBT(list);

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("data", list);
			ByteBufUtils.writeTag(buf, nbt);
		}

	}

	public static class TCOKitApproved implements IMessage
	{
		public int id;

		public TCOKitApproved()
		{
		}

		public TCOKitApproved(int id)
		{
			this.id = id;
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(id);
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.id = buf.readInt();
		}

	}

}
