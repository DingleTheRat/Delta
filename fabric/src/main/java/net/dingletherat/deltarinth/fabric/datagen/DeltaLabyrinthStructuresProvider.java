package net.dingletherat.deltarinth.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

public class DeltaLabyrinthStructuresProvider extends LabyrinthStructuresProvider {
    public DeltaLabyrinthStructuresProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    void generateStructures(LabyrinthStructureGenerators generators) {
        generators.generateSimple("test1", 0.25, 2, 2);
        generators.generateSimple("test2", 0.25, 3, 3);
        generators.generateSimple("test3", 0.25, 4, 5);
        generators.generateSimple("test4", 0.25, 6, 6);
    }
}
