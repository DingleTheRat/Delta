package net.dingletherat.deltarinth.registry;

public final class DeltaRegistries {
    public static void register() {
        DeltaConsumeEffects.CONSUME_EFFECT_TYPES.register();
        DeltaDataComponents.DATA_COMPONENTS.register();
        DeltaItems.ITEMS.register();
        DeltaTabs.TABS.register();
        DeltaBlocks.BLOCKS.register();
        DeltaChunkGeneration.CHUNK_GENERATORS.register();
    }
}
