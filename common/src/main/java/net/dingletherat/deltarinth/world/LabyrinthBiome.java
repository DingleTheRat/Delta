package net.dingletherat.deltarinth.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LabyrinthBiome(String temp) {
    public static final Codec<LabyrinthBiome> CODEC = RecordCodecBuilder.<LabyrinthBiome>create(instance -> instance.group(
                    Codec.STRING.fieldOf("temp").forGetter(LabyrinthBiome::temp)
                ).apply(instance, LabyrinthBiome::new));
}
