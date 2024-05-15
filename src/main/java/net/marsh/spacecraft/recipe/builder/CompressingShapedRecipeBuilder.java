package net.marsh.spacecraft.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.recipe.CompressingShapedRecipe;
import net.marsh.spacecraft.recipe.ModRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CompressingShapedRecipeBuilder {
    private final ItemLike result;
    private final int count;
    private final Map<Character, Ingredient> key = new HashMap<>();
    private final String[] pattern = new String[3];
    private int patternIndex = 0;

    public CompressingShapedRecipeBuilder(ItemLike result, int count) {
        this.result = result;
        this.count = count;
    }

    public CompressingShapedRecipeBuilder pattern(String line) {
        if (patternIndex  >= pattern.length) {
            throw new IllegalArgumentException("Compressing Recipe Builder pattern index out of bounds");
        }
        this.pattern[patternIndex++] = line;
        return this;
    }

    public CompressingShapedRecipeBuilder define(char symbol, Ingredient ingredient) {
        key.put(symbol, ingredient);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("type", "spacecraft:compressing_shaped");

                JsonObject keyJson = new JsonObject();
                for (Map.Entry<Character, Ingredient> entry : key.entrySet()) {
                    keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
                }
                json.add("key", keyJson);

                JsonArray patternJson = new JsonArray();
                for (String line : pattern) {
                    patternJson.add(line);
                }
                json.add("pattern", patternJson);

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
                return ModRecipes.COMPRESSING_SHAPED_SERIALIZER.get();
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
