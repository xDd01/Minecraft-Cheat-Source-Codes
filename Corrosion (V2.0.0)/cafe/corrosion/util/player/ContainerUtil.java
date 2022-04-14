/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;

public class ContainerUtil {
    public static boolean isEmpty(Container container) {
        boolean isEmpty = true;
        int maxSlot = container.inventorySlots.size() == 90 ? 54 : 27;
        for (int i2 = 0; i2 < maxSlot; ++i2) {
            if (!container.getSlot(i2).getHasStack()) continue;
            isEmpty = false;
        }
        return isEmpty;
    }

    public static boolean isNameValid(Container container) {
        ContainerChest containerChest = (ContainerChest)container;
        String name = ChatFormatting.stripFormatting(containerChest.getLowerChestInventory().getName());
        return name.equals("Chest") || name.equals("Large Chest");
    }
}

