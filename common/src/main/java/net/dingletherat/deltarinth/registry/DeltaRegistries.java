package net.dingletherat.deltarinth.registry;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.world.LabyrinthChunkGenerator;
import net.dingletherat.deltarinth.world.LabyrinthStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;

public final class DeltaRegistries {
    public static final ResourceKey<Registry<LabyrinthStructure>>
        LABYRINTH_STRUCTURE = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(
                "deltarinth",
                "labyrinth_structure"
            )
        );

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
        DeferredRegister.create(Delta.MOD_ID, Registries.CHUNK_GENERATOR);

    public static final RegistrySupplier<MapCodec<? extends ChunkGenerator>> LABYRINH_CHUNK_GENERATOR = 
        CHUNK_GENERATORS.register("labyrinth", () -> {
            ResourceKey<MapCodec<? extends ChunkGenerator>> key = ResourceKey.create(Registries.CHUNK_GENERATOR,
                Identifier.fromNamespaceAndPath(Delta.MOD_ID, "labyrinth"));
            return LabyrinthChunkGenerator.CODEC;
        });

    public static void register() {
        DeltaConsumeEffects.initialize();
        DeltaDataComponents.initialize();
        DeltaItems.initialize();
        DeltaBlocks.initialize();

        CHUNK_GENERATORS.register();
    }
}
