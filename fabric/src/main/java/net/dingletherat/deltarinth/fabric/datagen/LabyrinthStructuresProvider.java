package net.dingletherat.deltarinth.fabric.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dingletherat.deltarinth.Delta;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public abstract class LabyrinthStructuresProvider implements DataProvider {

    public class LabyrinthStructureGenerators {
        final CachedOutput cache;
        private LabyrinthStructureGenerators(CachedOutput cache) {
            this.cache = cache;
        }

        /**
         * Generates a Delta labyrinth structure.
         * @param exits a double from 0 to 1 on how many exits the structure should have.
         * @param width how wide the structure rectangle should be - in how many corridors
         * @param height how wide the structure rectangle should be - in how many corridors
         **/
        public void generateSimple(String modid, String name, double exits, int width, int height, int offx, int offz) {
            JsonObject json = new JsonObject();
            json.addProperty("stack_chance", exits);
            json.addProperty("location", Identifier.fromNamespaceAndPath(modid, name).toString());
            JsonObject offset = new JsonObject();
            offset.addProperty("x", offx);
            offset.addProperty("z", offz);
            json.add("offset", offset);

            JsonArray cells = new JsonArray();
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < height; z++) {
                    JsonObject cell = new JsonObject();
                    cell.addProperty("x", x);
                    cell.addProperty("z", z);
                    cells.add(cell);
                }
            }
            json.add("cells", cells);

            futures.add(DataProvider.saveStable(cache, json, pathProvider.json(Delta.id(name))));
        }
    }

    private final PackOutput.PathProvider pathProvider;
    private final List<CompletableFuture<?>> futures = new ArrayList<>();

    public LabyrinthStructuresProvider(FabricPackOutput output) {
        pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, Delta.MOD_ID + "/labyrinth_structure");
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
