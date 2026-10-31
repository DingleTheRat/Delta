package net.dingletherat.deltarinth.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DeltaTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Delta.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> DELTA_TAB = TABS.register("delta", () ->
        CreativeTabRegistry.create(
            Component.translatable("category." + Delta.MOD_ID),  // tab title
            () -> new ItemStack(DeltaItems.BLOOD_BOTTLE.get())   // tab icon
        ));
}
