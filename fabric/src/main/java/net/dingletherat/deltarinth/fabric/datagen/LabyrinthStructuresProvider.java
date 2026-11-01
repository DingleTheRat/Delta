package net.dingletherat.deltarinth.fabric.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonObject;
import net.dingletherat.deltarinth.Delta;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public abstract class LabyrinthStructuresProvider implements DataProvider {

    public class LabyrinthStructureGenerators {
        final CachedOutput cache;
        private LabyrinthStructureGenerators(CachedOutput cache) {
            this.cache = cache;
        }

        public void register() {
            JsonObject json = new JsonObject();
            json.addProperty("hello", "world");

            // Resolves to data/<modid>/custom/example.json
            futures.add(DataProvider.saveStable(cache, json, pathProvider.json(Delta.id("example"))));
        }
    }

    private final PackOutput.PathProvider pathProvider;
    private final List<CompletableFuture<?>> futures = new ArrayList<>();

    public LabyrinthStructuresProvider(FabricPackOutput output) {
        pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "custom");
    }

    abstract void generateStructures(LabyrinthStructureGenerators generators);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        generateStructures(new LabyrinthStructureGenerators(cache));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Labyrinth Structure Provider";
    }
}
