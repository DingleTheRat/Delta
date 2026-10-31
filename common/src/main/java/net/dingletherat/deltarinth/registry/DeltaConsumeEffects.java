package net.dingletherat.deltarinth.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.item.EitherStatusEffectConsumeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public class DeltaConsumeEffects {
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECT_TYPES = DeferredRegister.create(Delta.MOD_ID, Registries.CONSUME_EFFECT_TYPE);

    public static final RegistrySupplier<ConsumeEffect.Type<EitherStatusEffectConsumeEffect>> EITHER_EFFECT = CONSUME_EFFECT_TYPES.register("either_effect",
        () -> new ConsumeEffect.Type<>(EitherStatusEffectConsumeEffect.CODEC, EitherStatusEffectConsumeEffect.STREAM_CODEC));
}
