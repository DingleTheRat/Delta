package net.dingletherat.deltarinth.fabric.datagen;

import net.dingletherat.deltarinth.Delta;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

public class DeltaLabyrinthStructuresProvider extends LabyrinthStructuresProvider {
    public DeltaLabyrinthStructuresProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    void generateStructures(LabyrinthStructureGenerators generators) {
        generators.generateSimple(Delta.MOD_ID, "tall_tower",
                0.25,
                2, 3,
                1, 1);
    }
}
