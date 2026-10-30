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
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.synth.Noise;


public class LabyrinthChunkGenerator extends ChunkGenerator {
    private float sinPeriod, sinMagnitude;

    public static final MapCodec<LabyrinthChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(LabyrinthChunkGenerator::getBiomeSource),
                Codec.FLOAT.fieldOf("sin_period").forGetter(LabyrinthChunkGenerator::getSinPeriod),
                Codec.FLOAT.fieldOf("sin_magnitude").forGetter(LabyrinthChunkGenerator::getSinMagnitude)
                ).apply(instance, LabyrinthChunkGenerator::new));

    public LabyrinthChunkGenerator(BiomeSource biomeSource, float sinPeriod, float sinMagnitude) {
        super(biomeSource);
        this.sinPeriod = sinPeriod;
        this.sinMagnitude = sinMagnitude;
    }

    @Override
    public void addDebugScreenInfo(List<String> result, RandomState randomState, BlockPos feetPos,
            SamplerContext samplerContext) {}

    private int getHeightAt(RandomState randomState, int x, int z) {
        Noise noise = randomState.getOrCreateNoise(Noises.GRAVEL);
        return noise.get(x*5, z*5) > 0.25 ? 0 : 0;
        // sine wave
        //return (int) ((
        //            Math.sin(x / sinPeriod + 3.2) +
        //            Math.sin(z / sinPeriod + 4.3)
        //        ) * sinMagnitude
        //        + sinMagnitude * 2 + 10
        //);
    }

    @Override
    public CompletableFuture<ChunkAccess> buildTerrain(ChunkAccess chunk, Blender blender, RandomState randomState,
            StructureManager structureManager, BiomeManager biomeManager, @Nullable WorldGenRegion carverBiomeRegion, Set<Holder<Biome>> possibleBiomes) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = chunk.getMinY();
        int maxY = minY + chunk.getHeight();

        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        // Rounded to 1 maze every 3 chunks
        int blocksPerMaze = 16 * 6;
        int mazeOriginX = Math.floorDiv(startX, blocksPerMaze) * blocksPerMaze;
        int mazeOriginZ = Math.floorDiv(startZ, blocksPerMaze) * blocksPerMaze;

        // How much to add to the coordinates to get to the position in the maze
        int mazeOffX = startX - mazeOriginX;
        int mazeOffZ = startZ - mazeOriginZ;

        RandomSource random = randomState.getOrCreateRandomFactory(Identifier.fromNamespaceAndPath(Delta.MOD_ID, "labyrinth"))
            .at(new BlockPos(mazeOriginX, 0, mazeOriginZ));
        Maze maze = new Maze(blocksPerMaze / 6, blocksPerMaze / 6, 3, 3, random);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (!maze.getAt(x + mazeOffX, z + mazeOffZ)) {
                    // TODO: change to making a wall
                    pos.set(x, minY + 1, z);
                    chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState());
                }
                pos.set(x, minY, z);
                chunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState());
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int minY = heightAccessor.getMinY();
        int maxY = minY + heightAccessor.getHeight();

        int height = getHeightAt(randomState, x, z);
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
        return (int) (Math.sin(x * sinPeriod) * sinMagnitude + sinMagnitude + 10);
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
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
        return;
    }

    public float getSinPeriod() {
        return sinPeriod;
    }

    public float getSinMagnitude() {
        return sinMagnitude;
    }
}
