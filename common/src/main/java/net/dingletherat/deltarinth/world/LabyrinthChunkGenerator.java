package net.dingletherat.deltarinth.world;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.registry.DeltaChunkGenerators;
import net.dingletherat.deltarinth.world.Maze.StructurePlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class LabyrinthChunkGenerator extends ChunkGenerator {

    List<LabyrinthStructure> structures;
    Cache<Maze.Pair, Maze> chunkToMaze = CacheBuilder.newBuilder().maximumSize(128).build();
    PositionalRandomFactory randomFactory;

    static final int WALL_HEIGHT = 16;
    static final int blocksPerMaze = 16 * 9;
    static final Maze.Settings settings = new Maze.Settings(blocksPerMaze / 6, blocksPerMaze / 6, 3, 3, 0.80, 0.10);


    public static final MapCodec<LabyrinthChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(LabyrinthChunkGenerator::getBiomeSource)
                ).apply(instance, LabyrinthChunkGenerator::new));

    public LabyrinthChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    public void addDebugScreenInfo(List<String> result, RandomState randomState, BlockPos feetPos,
            SamplerContext samplerContext) {}

    @Override
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState state,
            StructureManager structureManager, ChunkAccess centerChunk,
            StructureTemplateManager structureTemplateManager, ResourceKey<Level> level) {

        super.createStructures(registryAccess, state, structureManager, centerChunk, structureTemplateManager, level);

        if (structures == null) {
            structures = registryAccess.lookupOrThrow(DeltaChunkGenerators.LABYRINTH_STRUCTURE).stream().toList();
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> buildTerrain(ChunkAccess chunk, Blender blender, RandomState randomState,
            StructureManager structureManager, BiomeManager biomeManager, @Nullable WorldGenRegion carverBiomeRegion, Set<Holder<Biome>> possibleBiomes) {

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = chunk.getMinY();

        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        int mazeOriginX = Math.floorDiv(startX, blocksPerMaze) * blocksPerMaze;
        int mazeOriginZ = Math.floorDiv(startZ, blocksPerMaze) * blocksPerMaze;

        // How much to add to the coordinates to get to the position in the maze
        int mazeOffX = startX - mazeOriginX + 1;
        int mazeOffZ = startZ - mazeOriginZ + 1;

        if (randomFactory == null) randomFactory = randomState.getOrCreateRandomFactory(Delta.id("labyrinth"));
        Maze maze = getMaze(mazeOriginX, mazeOriginZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int mazeX = x + mazeOffX;
                int mazeZ = z + mazeOffZ;
                int neighbours = 0;
                if (maze.getAt(mazeX + 1, mazeZ + 1)) neighbours++;
                if (maze.getAt(mazeX + 1, mazeZ)) neighbours++;
                if (maze.getAt(mazeX + 1, mazeZ - 1)) neighbours++;
                if (maze.getAt(mazeX, mazeZ + 1)) neighbours++;
                if (maze.getAt(mazeX, mazeZ - 1)) neighbours++;
                if (maze.getAt(mazeX - 1, mazeZ + 1)) neighbours++;
                if (maze.getAt(mazeX - 1, mazeZ)) neighbours++;
                if (maze.getAt(mazeX - 1, mazeZ - 1)) neighbours++;

                if (maze.getAt(mazeX, mazeZ)) {
                    generateFloor(pos, chunk, x, z, neighbours);
                } else {
                    generateWall(pos, chunk, x, z, neighbours);
                }
                pos.set(x, minY, z);
                chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState());
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    private void generateFloor(BlockPos.MutableBlockPos pos, ChunkAccess chunk, int x, int z, int neighbours) {
        // neighbours: how many of neighbours in 3x3 around block are floor
        int minY = chunk.getMinY();

        pos.set(x, minY + 1, z);
        if (neighbours < 8) {
            chunk.setBlockState(pos, Blocks.STONE_BRICKS.defaultBlockState());
        } else {
            chunk.setBlockState(pos, Blocks.STONE_BRICKS.defaultBlockState());
        }
    }

    private void generateWall(BlockPos.MutableBlockPos pos, ChunkAccess chunk, int x, int z, int neighbours) {
        // neighbours: how many of neighbours in 3x3 around block are floor
        int minY = chunk.getMinY();

        for (int y = minY + 1; y < WALL_HEIGHT + 1; y++) {
            pos.set(x, y, z);
            if (neighbours == 5) {
                chunk.setBlockState(pos, Blocks.STONE_BRICKS.defaultBlockState());
            } else {
                chunk.setBlockState(pos, Blocks.STONE_BRICKS.defaultBlockState());
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk,
                                     StructureManager structureManager) {
        super.applyBiomeDecoration(level, chunk, structureManager); // or skip if you want no vanilla features

        ChunkPos cp = chunk.getPos();
        int chunkMinX = cp.getMinBlockX();
        int chunkMinZ = cp.getMinBlockZ();

        int mazeOriginX = Math.floorDiv(chunkMinX, blocksPerMaze) * blocksPerMaze;
        int mazeOriginZ = Math.floorDiv(chunkMinZ, blocksPerMaze) * blocksPerMaze;

        Maze maze = getMaze(mazeOriginX, mazeOriginZ);

        BoundingBox chunkBox = new BoundingBox(
                chunkMinX, level.getMinY(), chunkMinZ,
                cp.getMaxBlockX(), level.getMaxY(), cp.getMaxBlockZ());

        StructureTemplateManager templates = level.getLevel().getStructureTemplateManager();

        for (int i = 0; i < maze.getPlacedStructures().size(); i++) {
            StructurePlacement s = maze.getPlacedStructures().get(i);
            Optional<StructureTemplate> opt = templates.get(Identifier.parse(s.structure().location()));
            if (opt.isEmpty()) continue;
            StructureTemplate template = opt.get();

            BlockPos origin = new BlockPos(
                    mazeOriginX + s.pos().x() - 1 + s.structure().offset().x(),
                    level.getMinY() + 2,           // adjust to sit on your floor
                    mazeOriginZ + s.pos().z() - 1 + s.structure().offset().z());

            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setBoundingBox(chunkBox)      // clips placement to this chunk
                    .setIgnoreEntities(true);

            // Skip early if it can't touch this chunk
            BoundingBox templateBox = template.getBoundingBox(settings, origin);
            if (!templateBox.intersects(chunkBox)) continue;

            RandomSource random = RandomSource.create(Mth.getSeed(origin));
            template.placeInWorld(level, origin, origin, settings, random, Block.UPDATE_CLIENTS);
        }
    }

    private Maze getMaze(int mazeOriginX, int mazeOriginZ) {
        if (structures == null) {
            throw new IllegalStateException("Labyrinth structure registry not accessible");
        }

        Maze.Pair key = new Maze.Pair(mazeOriginX, mazeOriginZ);
        if (chunkToMaze.getIfPresent(key) == null) {
            RandomSource xRandom = randomFactory.at(new BlockPos(mazeOriginX + 1, 0, mazeOriginZ));
            RandomSource zRandom = randomFactory.at(new BlockPos(mazeOriginX, 0, mazeOriginZ + 1));
            RandomSource xEdgeRandom = randomFactory.at(new BlockPos(mazeOriginX + 1, 0, mazeOriginZ + blocksPerMaze));
            RandomSource zEdgeRandom = randomFactory.at(new BlockPos(mazeOriginX + blocksPerMaze, 0, mazeOriginZ + 1));

            Maze maze = new Maze(settings, structures, xRandom, zRandom, xEdgeRandom, zEdgeRandom);
            chunkToMaze.put(key, maze);
            return maze;
        }
        return chunkToMaze.getIfPresent(key);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int minY = heightAccessor.getMinY();
        int maxY = minY + heightAccessor.getHeight();

        int height = WALL_HEIGHT + 1;
        BlockState[] column = new BlockState[maxY - minY];
        for (int y = minY; y < maxY; y++) {
            column[y - minY] =
                y == minY
                ? Blocks.BEDROCK.defaultBlockState()
                : y < height
                ? Blocks.STONE.defaultBlockState()
                : Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(0, column);
    }

    @Override
    public int getBaseHeight(int x, int z, Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return WALL_HEIGHT + 1;
    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {}
}
