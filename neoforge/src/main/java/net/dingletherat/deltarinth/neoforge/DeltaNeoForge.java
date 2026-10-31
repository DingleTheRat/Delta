package net.dingletherat.deltarinth.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.registry.DeltaChunkGenerators;
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
        event.worldRegistry(
                DeltaChunkGenerators.LABYRINTH_STRUCTURE,
                LabyrinthStructure.CODEC
        );
    }
}
