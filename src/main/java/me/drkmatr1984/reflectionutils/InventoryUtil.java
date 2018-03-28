package me.drkmatr1984.reflectionutils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.drkmatr1984.reflectionutils.NMSUtils;

import java.lang.reflect.Method;

/**
 * Created by Matthew on 03/04/2015.
 */
public class InventoryUtil extends NMSUtils{
    
    public static boolean inventorySetItem(Inventory inventory, int index, ItemStack item) {
        try {
            Method setItemMethod = class_CraftInventoryCustom.getMethod("setItem", Integer.TYPE, ItemStack.class);
            setItemMethod.invoke(inventory, index, item);
            return true;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean setInventoryResults(Inventory inventory, ItemStack item) {
        try {
            Method getResultsMethod = inventory.getClass().getMethod("getResultInventory");
            Object inv = getResultsMethod.invoke(inventory);
            Method setItemMethod = inv.getClass().getMethod("setItem", Integer.TYPE, class_ItemStack);
            setItemMethod.invoke(inv, 0, getHandle(item));
            return true;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void setCount(ItemStack stack, int count) {
        if (stack == null) return;
        try {
            Object handle = getHandle(stack);
            if (handle == null) return;
            class_ItemStack_count.set(handle, count);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static boolean hasItem(Inventory inventory, String itemName) {
        if (inventory == null) {
            return false;
        }
        ItemStack[] items = inventory.getContents();
        for (ItemStack item : items) {
            if (item != null && item.hasItemMeta()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName != null && displayName.equals(itemName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openSign(Player player, Location signBlock) {
        try {
            Object tileEntity = getTileEntity(signBlock);
            Object playerHandle = getHandle(player);
            if (tileEntity != null && playerHandle != null) {
                class_EntityPlayer_openSignMethod.invoke(playerHandle, tileEntity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
