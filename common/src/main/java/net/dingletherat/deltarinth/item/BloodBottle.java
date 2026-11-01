package net.dingletherat.deltarinth.item;

import net.dingletherat.deltarinth.registry.DeltaDataComponents;
import net.dingletherat.deltarinth.registry.DeltaItems;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BloodBottle extends Item {
    // Stats
    public static final int TICK_GOAL_BLOOD = 1200; // 1 minute
    public static final int TICK_GOAL_DRIED = 2400; // 2 minutes
    public static final int BAR_COLOR_FRESH_BLOOD = 0xE01010; // bright red
    public static final int BAR_COLOR_BLOOD = 0x8B0000; // dark red

    public BloodBottle(Properties properties) {
        super(properties.stacksTo(16).usingConvertsTo(Items.GLASS_BOTTLE).arch$tab(CreativeModeTabs.FOOD_AND_DRINKS));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (itemStack.is(DeltaItems.DRIED_BLOOD_BOTTLE.get())) return;
        if (!itemStack.has(DeltaDataComponents.COUNTER.get())) itemStack.set(DeltaDataComponents.COUNTER.get(), 0);

        int counter = itemStack.get(DeltaDataComponents.COUNTER.get());

        ItemStack replacement = ItemStack.EMPTY;
        if (itemStack.is(DeltaItems.FRESH_BLOOD_BOTTLE.get()) && counter >= TICK_GOAL_BLOOD) replacement = new ItemStack(DeltaItems.BLOOD_BOTTLE.get(), itemStack.getCount());
        if (itemStack.is(DeltaItems.BLOOD_BOTTLE.get()) && counter >= TICK_GOAL_DRIED) replacement = new ItemStack(DeltaItems.DRIED_BLOOD_BOTTLE.get(), itemStack.getCount());

        if (replacement == ItemStack.EMPTY) {
            int newCounter = counter + 1;
            itemStack.set(DeltaDataComponents.COUNTER.get(), newCounter);
            return;
        }

        itemStack.remove(DeltaDataComponents.COUNTER.get());
        replaceStack(itemStack, replacement, owner, slot);
    }

    private static void replaceStack(ItemStack itemStack, ItemStack replacement, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner instanceof Player player) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i) != itemStack) continue;
                inventory.setItem(i, replacement);
                break;
            }
        }
        if (slot != null && owner instanceof LivingEntity living && living.getItemBySlot(slot) == itemStack)
            living.setItemSlot(slot, replacement);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(DeltaDataComponents.COUNTER.get()) && !stack.is(DeltaItems.DRIED_BLOOD_BOTTLE.get());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int counter = stack.getOrDefault(DeltaDataComponents.COUNTER.get(), 0);
        int goal = stack.is(DeltaItems.FRESH_BLOOD_BOTTLE.get()) ? TICK_GOAL_BLOOD : TICK_GOAL_DRIED;
        return Math.min(Math.round(13.0f * counter / goal), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.is(DeltaItems.FRESH_BLOOD_BOTTLE.get()) ? BAR_COLOR_FRESH_BLOOD : BAR_COLOR_BLOOD;
    }
}
