package com.troy.tco.game;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.crash.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.ReportedException;

public class TCOKit implements IInventory
{

	public String name;
	public int maxUsages;
	public int id = -1;

	public ItemStack[] inventory = new ItemStack[36];
	public ItemStack[] armor = new ItemStack[4];

	public TCOKit()
	{

	}

	public TCOKit(String name, int maxUsages)
	{
		this.name = name;
		this.maxUsages = maxUsages;
		armor[0] = new ItemStack(Items.diamond_helmet);
		armor[1] = new ItemStack(Items.diamond_chestplate);
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second
	 * arg) of items and returns them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count)
	{
		System.out.println("accessing index: " + index + " count " + count);
		ItemStack[] inv = this.inventory;
		if (index >= inventory.length)
		{
			index -= inventory.length;
			inv = this.armor;
		}
		if (inv[index].stackSize <= count)
		{
			ItemStack itemstack = inv[index];
			inv[index] = null;
			return itemstack;
		} else
		{
			ItemStack itemstack = inv[index].splitStack(count);
			if (inv[index].stackSize == 0)
			{
				inv[index] = null;
			}
			return itemstack;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench GUI.
	 * 
	 * Never drop any items
	 */
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return getStackInSlot(slot);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		// System.out.println("setting slot " + slot);
		ItemStack[] aitemstack = this.inventory;

		if (slot >= aitemstack.length)
		{
			slot -= aitemstack.length;
			aitemstack = this.armor;
		}

		aitemstack[slot] = itemStack;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory()
	{
		return this.inventory.length + 4;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int slot)
	{
		if (slot >= inventory.length)
		{
			return armor[slot - inventory.length];
		}

		return inventory[slot];
	}

	/**
	 * Returns the name of the inventory
	 */
	public String getInventoryName()
	{
		return "Kit Editor";
	}

	/**
	 * Returns if the inventory is named
	 */
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty()
	{

	}

	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return false;
	}

	public void openInventory()
	{
	}

	public void closeInventory()
	{
	}

	public boolean isItemValidForSlot(int slot, ItemStack item)
	{
		if (slot == 36)
			return item.getItem().getUnlocalizedName().contains("helmet");
		if (slot == 37)
			return item.getItem().getUnlocalizedName().contains("chestplate");
		if (slot == 38)
			return item.getItem().getUnlocalizedName().contains("leggings");
		if (slot == 39)
			return item.getItem().getUnlocalizedName().contains("boots");
		return true;
	}

	private static final String ID = "id", NAME = "name", MAX_USAGES = "max_usages";

	public NBTTagList writeToNBT(NBTTagList list)
	{
		NBTTagCompound header = new NBTTagCompound();
		header.setInteger(ID, id);
		header.setString(NAME, name);
		header.setInteger(MAX_USAGES, maxUsages);
		list.appendTag(header);

		for (int i = 0; i < this.inventory.length; i++)
		{
			if (this.inventory[i] != null)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(nbt);
				list.appendTag(nbt);
			}
		}

		for (int i = 0; i < this.armor.length; i++)
		{
			if (this.armor[i] != null)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) (i + 100));
				this.armor[i].writeToNBT(nbt);
				list.appendTag(nbt);
			}
		}

		return list;
	}

	/**
	 * Reads from the given tag list and fills the slots in the inventory with the
	 * correct items.
	 */
	public void readFromNBT(NBTTagList list)
	{

		NBTTagCompound header = list.getCompoundTagAt(0);
		this.id = header.getInteger(ID);
		this.name = header.getString(NAME);
		this.maxUsages = header.getInteger(MAX_USAGES);

		this.inventory = new ItemStack[36];
		this.armor = new ItemStack[4];

		for (int i = 1; i < list.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

			if (itemstack != null)
			{
				if (j >= 0 && j < this.inventory.length)
				{
					this.inventory[j] = itemstack;
				}

				if (j >= 100 && j < this.armor.length + 100)
				{
					this.armor[j - 100] = itemstack;
				}
			}
		}
	}

}
