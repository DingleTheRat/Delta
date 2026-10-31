package net.dingletherat.deltarinth.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.registry.DeltaChunkGenerators;
import net.dingletherat.deltarinth.world.LabyrinthStructure;

public final class DeltaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        DynamicRegistries.register(
                DeltaChunkGenerators.LABYRINTH_STRUCTURE,
                LabyrinthStructure.CODEC
        );

        System.out.println(
            DynamicRegistries.getWorldRegistries().stream()
                .filter(data -> data.key().equals(
                    DeltaChunkGenerators.LABYRINTH_STRUCTURE
                ))
                .toList()
        );

        // Run our common setup.
        Delta.init();
    }
}
