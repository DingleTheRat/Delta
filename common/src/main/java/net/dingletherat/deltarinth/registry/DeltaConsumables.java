package net.dingletherat.deltarinth.registry;

import net.dingletherat.deltarinth.item.EitherStatusEffectConsumeEffect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;

public class DeltaConsumables {
    // Stats
    public static final int WITHERING_DURATION = 300;
    public static final int REGENERATION_DURATION = 300;
    public static final float FRESH_BLOOD_WITHERING_CHANCE = 0.5f;
    public static final float BLOOD_WITHERING_CHANCE = 0.7f;
    public static final float DRIED_BLOOD_WITHERING_CHANCE = 1f;

    public static final Consumable FRESH_BLOOD = drink().onConsume(new EitherStatusEffectConsumeEffect(new MobEffectInstance(MobEffects.WITHER, WITHERING_DURATION, 0), new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0), FRESH_BLOOD_WITHERING_CHANCE)).build();
    public static final Consumable BLOOD = drink().onConsume(new EitherStatusEffectConsumeEffect(new MobEffectInstance(MobEffects.WITHER, WITHERING_DURATION, 0), new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0), BLOOD_WITHERING_CHANCE)).build();
    public static final Consumable DRIED_BLOOD = drink().onConsume(new EitherStatusEffectConsumeEffect(new MobEffectInstance(MobEffects.WITHER, WITHERING_DURATION, 0), new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0), DRIED_BLOOD_WITHERING_CHANCE)).build();
    //public static final Consumable GOLDEN_BLOOD = drink().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0))).build();

    public static Consumable.Builder drink() {
	return Consumable.builder().consumeSeconds(1.6f).animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK).hasConsumeParticles(false);
    }
}
