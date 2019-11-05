package com.troy.tco.inventory;

import com.troy.tco.game.TCOKit;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;

public class ContainerKitEditor extends Container
{
	private TCOKit kit;
	private InventoryPlayer playerInv;

	public ContainerKitEditor(final InventoryPlayer inv, TCOKit kit)
	{
		this.kit = kit;
		this.playerInv = inv;

		kit.openInventory();

		bindPlayerInventory(kit, 8, 25);
		for (int i = 0; i < 4; i++)// Add armor
		{
			final int armorIndex = i;
			addSlotToContainer(new Slot(kit, 36 + i, 98 + i * 18, 5)
			{
				public int getSlotStackLimit()
				{
					return 1;
				}

				/**
				 * Check if the stack is a valid item for this slot. Always true beside for the
				 * armor slots.
				 */
				public boolean isItemValid(ItemStack item)
				{
					if (item == null)
						return false;
					return item.getItem().isValidArmor(item, armorIndex, inv.player);
				}

				/**
				 * Returns the icon index on items.png that is used as background image of the
				 * slot.
				 */
				@SideOnly(Side.CLIENT)
				public IIcon getBackgroundIconIndex()
				{
					return ItemArmor.func_94602_b(armorIndex);
				}
			});
		}

		bindPlayerInventory(inv, 8, 115);
	}

	protected void bindPlayerInventory(IInventory inv, int xOffset, int yOffset)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inv, i, xOffset + i * 18, yOffset + 58));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.kit.closeInventory();
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		if(Thread.currentThread().getName().contains("Server"))
			System.out.println("Server click");
		Slot slot = (Slot) this.inventorySlots.get(slotID);
		if (slot == null || !slot.getHasStack())
			return null;
		ItemStack sourceStack = slot.getStack();
		ItemStack copy = sourceStack.copy();

		if (slotID < kit.getSizeInventory())
		{
			if (!this.mergeItemStack(sourceStack, kit.getSizeInventory(), this.inventorySlots.size(), true))
			{
				return null;
			}
		} else if (!this.mergeItemStack(sourceStack, 0, kit.getSizeInventory(), false))
		{
			return null;
		}

		if (sourceStack.stackSize == 0)
		{
			slot.putStack((ItemStack) null);
		} else
		{
			slot.onSlotChanged();
		}
		slot.onPickupFromSlot(player, sourceStack);
		return copy;
	}

}
