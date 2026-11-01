package net.dingletherat.deltarinth.world;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LabyrinthStructure(List<Maze.Pair> cells, int stack_chance) {
    public static final Codec<LabyrinthStructure> CODEC = RecordCodecBuilder.<LabyrinthStructure>create(instance -> instance.group(
                    Codec.list(Maze.Pair.CODEC).fieldOf("cells").forGetter(LabyrinthStructure::cells),
                    Codec.INT.fieldOf("stack_chance").forGetter(LabyrinthStructure::stack_chance)
                ).apply(instance, LabyrinthStructure::new));
}
