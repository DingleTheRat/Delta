package net.dingletherat.deltarinth.registry;

import java.util.function.Function;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.deltarinth.Delta;
import net.dingletherat.deltarinth.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DeltaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Delta.MOD_ID, Registries.ITEM);

    /*
     * Bit of context: Blood Bottles can either give you regeneration or withering.
     * Blood dries overtime as it's in an inventory (view stats in BloodBottle)
     * The drier the blood, the bigger chance of withering (view stats in DeltaConsumables)
     * Ignore golden blood for now (I'm too lazy to make a texture for it lol)
     */
    public static final RegistrySupplier<BloodBottle> FRESH_BLOOD_BOTTLE = registerItem("fresh_blood_bottle", (properties) -> new BloodBottle(properties.food(DeltaFoods.BLOOD, DeltaConsumables.FRESH_BLOOD)));
    public static final RegistrySupplier<BloodBottle> BLOOD_BOTTLE = registerItem("blood_bottle", (properties) -> new BloodBottle(properties.food(DeltaFoods.BLOOD, DeltaConsumables.BLOOD)));
    public static final RegistrySupplier<BloodBottle> DRIED_BLOOD_BOTTLE = registerItem("dried_blood_bottle", (properties) -> new BloodBottle(properties.food(DeltaFoods.BLOOD, DeltaConsumables.DRIED_BLOOD)));
    //public static final RegistrySupplier<Item> GOLDEN_BLOOD_BOTTLE = registerItem("golden_blood_bottle", CreativeModeTabs.FOOD_AND_DRINKS, (properties) -> new Item(properties.stacksTo(16).food(DeltaFoods.GOLDEN_BLOOD, DeltaConsumables.GOLDEN_BLOOD).usingConvertsTo(Items.GLASS_BOTTLE)));

    public static <T extends Item> RegistrySupplier<T> registerItem(String name,  Function<Item.Properties, T> function) {
        return ITEMS.register(name, () -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Delta.id(name));
            return function.apply(new Item.Properties().setId(key).arch$tab(DeltaTabs.DELTA_TAB));
        });
    }
}
