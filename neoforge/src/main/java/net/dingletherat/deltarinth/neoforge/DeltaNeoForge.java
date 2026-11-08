package net.dingletherat.deltarinth.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.registry.DeltaChunkGeneration;
import net.dingletherat.deltarinth.world.LabyrinthBiome;
import net.dingletherat.deltarinth.world.LabyrinthStructure;

@Mod(Delta.MOD_ID)
public final class DeltaNeoForge {
    public DeltaNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        modEventBus.addListener(DeltaNeoForge::registerDatapackRegistries);
        Delta.init();
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(NewDatapackRegistryEvent event) {
        // Registring a data type is platform-dependent, so we do it here.
        event.worldRegistry(DeltaChunkGeneration.LABYRINTH_STRUCTURE, LabyrinthStructure.CODEC);
        event.worldRegistry(DeltaChunkGeneration.LABYRINTH_BIOME, LabyrinthBiome.CODEC);
    }
}
