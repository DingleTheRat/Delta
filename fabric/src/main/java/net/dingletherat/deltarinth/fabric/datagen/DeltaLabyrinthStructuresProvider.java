package net.dingletherat.deltarinth.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

public class DeltaLabyrinthStructuresProvider extends LabyrinthStructuresProvider {
    public DeltaLabyrinthStructuresProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    void generateStructures(LabyrinthStructureGenerators generators) {
        generators.register();
    }
}
