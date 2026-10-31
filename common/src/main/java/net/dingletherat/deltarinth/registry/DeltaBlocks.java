package net.dingletherat.deltarinth.registry;

import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DeltaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Delta.MOD_ID, Registries.BLOCK);

    public static <T extends Block> RegistrySupplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        RegistrySupplier<T> block = registerBlockNoItem(name, function);
        registerBlockItem(name, block);
        return block;
    }

    public static <T extends Block> RegistrySupplier<T> registerBlockNoItem(String name, Function<BlockBehaviour.Properties, T> function) {
        return BLOCKS.register(name, () -> {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Delta.MOD_ID, name));
            return function.apply(BlockBehaviour.Properties.of().setId(key));
        });
    }

    public static RegistrySupplier<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        return DeltaItems.registerItem(name, CreativeModeTabs.BUILDING_BLOCKS, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }


    public static void initialize() {
        Delta.LOGGER.info("Initializing blocks for " + Delta.MOD_ID);
        BLOCKS.register();
    }
}
