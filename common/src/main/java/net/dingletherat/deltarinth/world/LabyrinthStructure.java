package net.dingletherat.deltarinth.world;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LabyrinthStructure(String location, List<Maze.Pair> cells, double stack_chance, Maze.Pair offset) {
    public static final Codec<LabyrinthStructure> CODEC = RecordCodecBuilder.<LabyrinthStructure>create(instance -> instance.group(
                    Codec.STRING.fieldOf("location").forGetter(LabyrinthStructure::location),
                    Codec.list(Maze.Pair.CODEC).fieldOf("cells").forGetter(LabyrinthStructure::cells),
                    Codec.DOUBLE.fieldOf("stack_chance").forGetter(LabyrinthStructure::stack_chance),
                    Maze.Pair.CODEC.fieldOf("offset").forGetter(LabyrinthStructure::offset)
                ).apply(instance, LabyrinthStructure::new));
}
