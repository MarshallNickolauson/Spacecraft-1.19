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

    public static final RegistryObject<RecipeSerializer<CompressingShapedRecipe>> COMPRESSING_SHAPED_SERIALIZER =
            SERIALIZERS.register("compressing_shaped", () -> CompressingShapedRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CircuitFabricatorRecipe>> CIRCUIT_FABRICATING_SERIALIZER =
            SERIALIZERS.register("circuit_fabricating", () -> CircuitFabricatorRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) { SERIALIZERS.register(eventBus); }
}
