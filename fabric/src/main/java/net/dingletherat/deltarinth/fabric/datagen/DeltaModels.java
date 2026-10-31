package net.dingletherat.deltarinth.fabric.datagen;

import net.dingletherat.deltarinth.registry.DeltaItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class DeltaModels extends FabricModelProvider {
    public DeltaModels(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) { }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(DeltaItems.FRESH_BLOOD_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(DeltaItems.BLOOD_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(DeltaItems.DRIED_BLOOD_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
    }
}
