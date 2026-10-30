package net.dingletherat.deltarinth.world;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dingletherat.deltarinth.Delta;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.synth.Noise;


public class LabyrinthChunkGenerator extends ChunkGenerator {
    static final int WALL_HEIGHT = 16;
    static final int blocksPerMaze = 16 * 6;
    static final Maze.Settings settings = new Maze.Settings(blocksPerMaze / 6, blocksPerMaze / 6, 3, 3, 90);

    static PositionalRandomFactory randomFactory;

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
    public CompletableFuture<ChunkAccess> buildTerrain(ChunkAccess chunk, Blender blender, RandomState randomState,
            StructureManager structureManager, BiomeManager biomeManager, @Nullable WorldGenRegion carverBiomeRegion, Set<Holder<Biome>> possibleBiomes) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = chunk.getMinY();
        int maxY = minY + chunk.getHeight();

        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        // Rounded to 1 maze every 9 chunks
        int mazeOriginX = Math.floorDiv(startX, blocksPerMaze) * blocksPerMaze;
        int mazeOriginZ = Math.floorDiv(startZ, blocksPerMaze) * blocksPerMaze;

        // How much to add to the coordinates to get to the position in the maze
        int mazeOffX = startX - mazeOriginX + 1;
        int mazeOffZ = startZ - mazeOriginZ + 1;

        if (randomFactory == null) randomFactory = randomState.getOrCreateRandomFactory(Identifier.fromNamespaceAndPath(Delta.MOD_ID, "labyrinth"));
        RandomSource xRandom = randomFactory.at(new BlockPos(mazeOriginX + 1, 0, mazeOriginZ));
        RandomSource zRandom = randomFactory.at(new BlockPos(mazeOriginX, 0, mazeOriginZ + 1));
        RandomSource xEdgeRandom = randomFactory.at(new BlockPos(mazeOriginX + 1, 0, mazeOriginZ + blocksPerMaze));
        RandomSource zEdgeRandom = randomFactory.at(new BlockPos(mazeOriginX + blocksPerMaze, 0, mazeOriginZ + 1));

        Maze maze = new Maze(settings, xRandom, zRandom, xEdgeRandom, zEdgeRandom);

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
            chunk.setBlockState(pos, Blocks.QUARTZ_BRICKS.defaultBlockState());
        }
    }

    private void generateWall(BlockPos.MutableBlockPos pos, ChunkAccess chunk, int x, int z, int neighbours) {
        // neighbours: how many of neighbours in 3x3 around block are floor
        int minY = chunk.getMinY();

        for (int y = minY + 1; y < WALL_HEIGHT + 1; y++) {
            pos.set(x, y, z);
            if (neighbours == 5) {
                chunk.setBlockState(pos, Blocks.QUARTZ_PILLAR.defaultBlockState());
            } else {
                chunk.setBlockState(pos, Blocks.STONE_BRICKS.defaultBlockState());
            }
        }
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
