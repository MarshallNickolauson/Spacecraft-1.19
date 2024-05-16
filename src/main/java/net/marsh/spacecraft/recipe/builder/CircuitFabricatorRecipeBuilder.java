package net.marsh.spacecraft.recipe.builder;

import com.google.gson.JsonObject;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.recipe.ModRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class CircuitFabricatorRecipeBuilder {
    private final ItemLike result;
    private final int count;
    private final ItemLike ingredient;

    public CircuitFabricatorRecipeBuilder(ItemLike ingredient, ItemLike result) {
        this.ingredient = ingredient;
        this.result = result;
        this.count = 1;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("type", "spacecraft:circuit_fabricating");

                json.addProperty("ingredient", "minecraft:" + ingredient.asItem().toString());

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", Spacecraft.MOD_ID + ":" + result.asItem());
                if (count > 1) {
                    resultJson.addProperty("count", count);
                }
                json.add("result", resultJson);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return ModRecipes.CIRCUIT_FABRICATING_SERIALIZER.get();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }
}