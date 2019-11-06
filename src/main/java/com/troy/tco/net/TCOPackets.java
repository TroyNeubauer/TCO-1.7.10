package com.troy.tco.net;

import com.troy.tco.client.gui.GuiKitEditor;
import com.troy.tco.game.TCOKit;
import com.troy.tco.inventory.ContainerKitEditor;
import com.troy.tco.reference.Reference;
import com.troy.tco.utility.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;

public class TCOPackets
{

	public static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	private static int __TempID = 0;

	static
	{
		HANDLER.registerMessage(TCOKitCreatedHandler.class, TCOKitCreated.class, __TempID++, Side.SERVER);
		HANDLER.registerMessage(TCOKitGuiOpenedHandlerServer.class, TCOKitGuiOpened.class, __TempID++, Side.SERVER);
		HANDLER.registerMessage(TCOKitDeletedHandler.class, TCOKitDeleted.class, __TempID++, Side.SERVER);

		HANDLER.registerMessage(ApproveTCOKitHandler.class, ApproveTCOKit.class, __TempID++, Side.CLIENT);
		HANDLER.registerMessage(OpenTCOKitGuiHandler.class, OpenTCOKitGui.class, __TempID++, Side.CLIENT);

	}

	public static void init()
	{
		// Nop. Call static initializer
		System.out.println("Registered packets and packet handlers");
	}

	public static class TCOKitCreatedHandler implements IMessageHandler<TCOKitCreated, IMessage>
	{
		@Override
		public ApproveTCOKit onMessage(TCOKitCreated message, MessageContext ctx)
		{

			TCOKit kit = new TCOKit();
			kit.id = TCOStorage.get().nextKitID();
			kit.name = message.name;
			TCOStorage.get().updateKit(kit);
			System.out.println("Server assigning id: " + kit.id + " to kit name " + kit.name);
			/*
			 * for (Object player :
			 * MinecraftServer.getServer().getEntityWorld().playerEntities)// Send to the
			 * other players { if (!ctx.getServerHandler().playerEntity.equals(player))
			 * HANDLER.sendTo(new TCOKitCreated(message.kit), ((EntityPlayerMP) player)); }
			 */
			HANDLER.sendTo(new ApproveTCOKit(kit.id), ctx.getServerHandler().playerEntity);
			serverOpenTCOKitGui(ctx, kit);
			return null;
		}
	}

	public static class ApproveTCOKitHandler implements IMessageHandler<ApproveTCOKit, IMessage>
	{

		@Override
		public IMessage onMessage(ApproveTCOKit message, MessageContext ctx)
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

	public static class TCOKitGuiOpenedHandlerServer implements IMessageHandler<TCOKitGuiOpened, IMessage>
	{

		@Override
		public IMessage onMessage(TCOKitGuiOpened message, MessageContext ctx)
		{
			TCOKit kit = TCOStorage.get().getKit(message.id);
			serverOpenTCOKitGui(ctx, kit);
			return null;
		}

	}

	public static class OpenTCOKitGuiHandler implements IMessageHandler<OpenTCOKitGui, IMessage>
	{

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(OpenTCOKitGui message, MessageContext ctx)
		{
			TCOKit kit = TCOStorage.get().getKit(message.kitID);
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			Minecraft.getMinecraft().displayGuiScreen(new GuiKitEditor(player.inventory, kit));
			player.openContainer.windowId = message.windowID;
			return null;
		}

	}

	public static class TCOKitDeletedHandler implements IMessageHandler<TCOKitDeleted, IMessage>
	{

		@Override
		public IMessage onMessage(TCOKitDeleted message, MessageContext ctx)
		{
			TCOStorage.get().deleteKit(message.id);
			System.out.println("Server deleting kit " + message.id);
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

	public static class TCOKitDeleted implements IMessage
	{

		public int id;

		public TCOKitDeleted()
		{

		}

		public TCOKitDeleted(int id)
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

	public static class OpenTCOKitGui implements IMessage
	{

		public int kitID, windowID;

		public OpenTCOKitGui()
		{

		}

		public OpenTCOKitGui(int kitID, int windowID)
		{
			this.kitID = kitID;
			this.windowID = windowID;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.kitID = buf.readInt();
			this.windowID = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(kitID);
			buf.writeInt(windowID);
		}

	}

	public static class TCOKitCreated implements IMessage
	{

		public String name;

		public TCOKitCreated()
		{
		}

		public TCOKitCreated(String name)
		{
			this.name = name;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.name = ByteBufUtils.readUTF8String(buf);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			ByteBufUtils.writeUTF8String(buf, name);
		}

	}

	public static class ApproveTCOKit implements IMessage
	{
		public int id;

		public ApproveTCOKit()
		{
		}

		public ApproveTCOKit(int id)
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

	public static void serverOpenTCOKitGui(MessageContext ctx, TCOKit kit)
	{
		System.out.println("Server telling client to open ui");
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player.openContainer != player.inventoryContainer)
		{
			player.closeScreen();
		}

		player.getNextWindowId();
		HANDLER.sendTo(new OpenTCOKitGui(kit.id, player.currentWindowId), player);
		player.openContainer = new ContainerKitEditor(player.inventory, kit);
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

}
