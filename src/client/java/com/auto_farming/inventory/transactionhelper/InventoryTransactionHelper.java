package com.auto_farming.inventory.transactionhelper;

import static com.auto_farming.misc.ThreadHelper.VERY_LONG_DURATION;
import static com.auto_farming.misc.ThreadHelper.MEDIUM_DURATION;
import static com.auto_farming.misc.ThreadHelper.SHORT_DURATION;
import static com.auto_farming.misc.ThreadHelper.randomSleep;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.actionwrapper.Actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public abstract class InventoryTransactionHelper {

    private static ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    private static PlayerInventory getInventory() {
        ClientPlayerEntity currentPlayer = getPlayer();

        if (currentPlayer == null)
            return null;

        return currentPlayer.getInventory();
    }

    protected static String getSkyblockId(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;

        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null)
            return null;

        NbtCompound nbt = custom.copyNbt();

        if (nbt.contains("id")) {
            String id = nbt.getString("id").get();
            if (id != null && !id.isEmpty())
                return id;
        }

        if (nbt.contains("ExtraAttributes")) {
            NbtCompound extra = nbt.getCompound("ExtraAttributes").get();
            if (extra.contains("id")) {
                String id = extra.getString("id").get();
                if (id != null && !id.isEmpty())
                    return id;
            }
        }

        return null;
    }

    protected static boolean isSkyblockItemInInventory(String targetId) {
        PlayerEntity player = getPlayer();

        if (player == null)
            return false;

        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
            String id = getSkyblockId(inventory.getStack(i));
            if (id != null && id.equalsIgnoreCase(targetId)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isSkyblockItemInHotBar(String targetId) {
        PlayerEntity player = getPlayer();

        if (player == null)
            return false;

        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < PlayerInventory.HOTBAR_SIZE; i++) {
            String id = getSkyblockId(inventory.getStack(i));
            if (id != null && id.equalsIgnoreCase(targetId)) {
                return true;
            }
        }
        return false;
    }

    protected static int moveItemToHotbar(int from) {

        if (from < 0 || from >= 36) {
            throw new IllegalArgumentException("Invalid source slot: " + from);
        }

        PlayerInventory inventory = getInventory();

        if (from < PlayerInventory.HOTBAR_SIZE && from != 8)
            return from;

        int to = -1;

        for (int i = 0; i < PlayerInventory.HOTBAR_SIZE - 1; i++) {
            if (inventory.getStack(i).isEmpty()) {
                to = i;
                break;
            }
        }

        if (to == -1)
            to = 7;

        moveItem(from, to);
        return to;
    }

    protected static void moveItem(int fromInvIndex, int toHotbarInvIndex) {

        if (toHotbarInvIndex < 0 || toHotbarInvIndex > 7) {
            throw new IllegalArgumentException("Invalid hotbar slot (must be 0–7): " + toHotbarInvIndex);
        }
        if (fromInvIndex < 0 || fromInvIndex >= PlayerInventory.MAIN_SIZE) {
            throw new IllegalArgumentException("Invalid source slot (must be 0–35): " + fromInvIndex);
        }

        int fromHandler = toHandlerSlotId(fromInvIndex);
        int toHandler = toHandlerSlotId(toHotbarInvIndex);

        AutofarmingClient.LOGGER.info("moving item from inv:" + fromInvIndex + " -> " + toHotbarInvIndex +
                " (handler " + fromHandler + " -> " + toHandler + ")");

        clickPickup(fromHandler);
        clickPickup(toHandler);
        clickPickup(fromHandler);
    }

    private static int toHandlerSlotId(int invIndex) {
        var mc = MinecraftClient.getInstance();
        var handler = mc.player.currentScreenHandler;

        for (int i = 0; i < handler.slots.size(); i++) {
            Slot s = handler.getSlot(i);
            if (s.inventory == mc.player.getInventory() && s.getIndex() == invIndex) {
                return i;
            }
        }
        throw new IllegalStateException("No handler slot for player inv index " + invIndex);
    }

    private static void clickPickup(int handlerSlotId) {
        var mc = MinecraftClient.getInstance();
        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                handlerSlotId,
                0,
                SlotActionType.PICKUP,
                mc.player);
    }

    protected static int slotOfSkyblockItem(String targetId) {
        PlayerInventory inventory = getInventory();

        for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
            String id = getSkyblockId(inventory.getStack(i));

            if (id == null)
                continue;

            AutofarmingClient.LOGGER.info("sbitem: " + id + " !");

            if (id.equalsIgnoreCase(targetId.trim()))
                return i;
        }

        return -1;
    }

    protected static void pickupItem(int invIndex) {
        ClientPlayerEntity player = getPlayer();
        ClientPlayerInteractionManager interaction = MinecraftClient.getInstance().interactionManager;
        if (player == null || interaction == null) {
            return;
        }

        int handlerSlotId = toHandlerSlotId(invIndex);

        int syncId = player.currentScreenHandler.syncId;

        randomSleep(VERY_LONG_DURATION);
        interaction.clickSlot(syncId, handlerSlotId, 0, SlotActionType.PICKUP, player);
        randomSleep(VERY_LONG_DURATION);
    }

    protected static void selectHotbarSlot(int slot) {
        if (!PlayerInventory.isValidHotbarIndex(slot))
            return;

        randomSleep(SHORT_DURATION);
        getPlayer().getInventory().setSelectedSlot(slot);
        randomSleep(SHORT_DURATION);
    }

    protected static void useItem() {
        randomSleep(SHORT_DURATION);
        Actions.RIGHT_CLICK.activate();
        randomSleep(MEDIUM_DURATION);
        Actions.RIGHT_CLICK.deactivate();
        randomSleep(SHORT_DURATION);
    }
}
