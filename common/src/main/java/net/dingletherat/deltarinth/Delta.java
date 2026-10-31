package net.dingletherat.deltarinth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dingletherat.deltarinth.registry.DeltaBlocks;
import net.dingletherat.deltarinth.registry.DeltaConsumeEffects;
import net.dingletherat.deltarinth.registry.DeltaDataComponents;
import net.dingletherat.deltarinth.registry.DeltaItems;
import net.dingletherat.deltarinth.world.LabyrinthChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class Delta {
    public static final String MOD_ID = "deltarinth";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing " + MOD_ID);
        DeltaConsumeEffects.initialize();
        DeltaDataComponents.initialize();
        DeltaItems.initialize();
        DeltaBlocks.initialize();

        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Identifier.fromNamespaceAndPath(MOD_ID, "labyrinth"), LabyrinthChunkGenerator.CODEC);
    }
}
