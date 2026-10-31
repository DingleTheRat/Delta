package net.dingletherat.deltarinth.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LabyrinthStructure(int width, int height) {
    public static final Codec<LabyrinthStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("width").forGetter(LabyrinthStructure::width),
                    Codec.INT.fieldOf("height").forGetter(LabyrinthStructure::width)
                ).apply(instance, LabyrinthStructure::new));
}
