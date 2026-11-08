package net.dingletherat.deltarinth.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.registry.DeltaChunkGeneration;
import net.dingletherat.deltarinth.world.LabyrinthBiome;
import net.dingletherat.deltarinth.world.LabyrinthStructure;

public final class DeltaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Registring a data type is platform-dependent, so we do it here.
        DynamicRegistries.register(DeltaChunkGeneration.LABYRINTH_STRUCTURE, LabyrinthStructure.CODEC);
        DynamicRegistries.register(DeltaChunkGeneration.LABYRINTH_BIOME, LabyrinthBiome.CODEC);

        Delta.init();
    }
}
