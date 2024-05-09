package net.marsh.spacecraft.recipe;

import net.marsh.spacecraft.Spacecraft;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ALL")
public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Spacecraft.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ElectricCompressorRecipe>> ELECTRIC_COMPRESSOR_SERIALIZER =
            SERIALIZERS.register("electric_compressing", () -> ElectricCompressorRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) { SERIALIZERS.register(eventBus); }
}
