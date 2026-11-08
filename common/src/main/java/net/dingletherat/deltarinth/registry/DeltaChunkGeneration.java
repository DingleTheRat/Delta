package net.dingletherat.deltarinth.registry;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.world.LabyrinthBiome;
import net.dingletherat.deltarinth.world.LabyrinthChunkGenerator;
import net.dingletherat.deltarinth.world.LabyrinthStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DeltaChunkGeneration {

    // Data Types
    public static final ResourceKey<Registry<LabyrinthStructure>> LABYRINTH_STRUCTURE =
        ResourceKey.createRegistryKey(Delta.id("labyrinth_structure"));

    public static final ResourceKey<Registry<LabyrinthBiome>> LABYRINTH_BIOME =
        ResourceKey.createRegistryKey(Delta.id("labyrinth_biome"));


    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
        DeferredRegister.create(Delta.MOD_ID, Registries.CHUNK_GENERATOR);

    public static final RegistrySupplier<MapCodec<? extends ChunkGenerator>> LABYRINH_CHUNK_GENERATOR = 
        CHUNK_GENERATORS.register("labyrinth", () -> {
            ResourceKey<MapCodec<? extends ChunkGenerator>> key = ResourceKey.create(Registries.CHUNK_GENERATOR,
                Delta.id("labyrinth"));
            return LabyrinthChunkGenerator.CODEC;
        });
}
