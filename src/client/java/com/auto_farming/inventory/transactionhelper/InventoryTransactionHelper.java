package com.auto_farming.inventory.transactionhelper;

import static com.auto_farming.misc.ThreadHelper.MEDIUM_DURATION;
import static com.auto_farming.misc.ThreadHelper.SHORT_DURATION;
import static com.auto_farming.misc.ThreadHelper.randomSleep;

import java.util.Optional;

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

        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return null;
        }

        NbtCompound nbt = nbtComponent.copyNbt();
        if (nbt.contains("ExtraAttributes")) {
            Optional<NbtCompound> extra = nbt.getCompound("ExtraAttributes");

            if (extra.isEmpty())
                return null;

            if (extra.get().contains("id")) {
                Optional<String> id = extra.get().getString("id");

                if (id.isPresent())
                    return id.get();
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
        PlayerInventory inventory = getInventory();

        if (from < PlayerInventory.HOTBAR_SIZE)
            return from;

        int to = -1;

        for (int i = 0; i < PlayerInventory.HOTBAR_SIZE - 1; i++) {
            ItemStack currentHotbarStack = inventory.getStack(i);

            if (currentHotbarStack.isEmpty()) {
                to = i;
                break;
            }
        }

        if (to == -1)
            to = 7;

        moveItem(from, to);
        return to;
    }

    protected static void moveItem(int from, int to) {
        pickupItem(from);
        pickupItem(to);
        pickupItem(from);
    }

    protected static int slotOfSkyblockItem(String targetId) {
        PlayerInventory inventory = getInventory();

        for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
            String id = getSkyblockId(inventory.getStack(i));

            if (id == null)
                continue;

            if (id.equals(targetId))
                return i;
        }

        return -1;
    }

    protected static void pickupItem(int slot) {
        ClientPlayerInteractionManager interaction = MinecraftClient.getInstance().interactionManager;
        ClientPlayerEntity player = getPlayer();
        int syncId = player.currentScreenHandler.syncId;

        if (player == null || interaction == null) {
            return;
        }

        randomSleep(MEDIUM_DURATION);
        interaction.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
        randomSleep(MEDIUM_DURATION);
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
