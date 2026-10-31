package net.dingletherat.deltarinth.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dingletherat.deltarinth.registry.DeltaConsumeEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public record EitherStatusEffectConsumeEffect(MobEffectInstance first, MobEffectInstance second, float chance) implements ConsumeEffect {
    public static final MapCodec<EitherStatusEffectConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                MobEffectInstance.CODEC.fieldOf("first").forGetter(EitherStatusEffectConsumeEffect::first),
                MobEffectInstance.CODEC.fieldOf("second").forGetter(EitherStatusEffectConsumeEffect::second),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 0.5F).forGetter(EitherStatusEffectConsumeEffect::chance)
            )
            .apply(instance, EitherStatusEffectConsumeEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, EitherStatusEffectConsumeEffect> STREAM_CODEC = StreamCodec.composite(
        MobEffectInstance.STREAM_CODEC,
        EitherStatusEffectConsumeEffect::first,
        MobEffectInstance.STREAM_CODEC,
        EitherStatusEffectConsumeEffect::second,
        ByteBufCodecs.FLOAT,
        EitherStatusEffectConsumeEffect::chance,
        EitherStatusEffectConsumeEffect::new
    );

    @Override
    public ConsumeEffect.Type<EitherStatusEffectConsumeEffect> getType() {
        return DeltaConsumeEffects.EITHER_EFFECT.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity user) {
        MobEffectInstance effect = user.getRandom().nextFloat() < this.chance ? this.first : this.second;
        return user.addEffect(new MobEffectInstance(effect));
    }
}
