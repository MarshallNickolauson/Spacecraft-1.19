package net.marsh.spacecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.marsh.spacecraft.Spacecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class ElectricCompressorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public ElectricCompressorRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if (simpleContainer.getContainerSize() <= 10) {
            return false;
        }

        for (int i = 0; i < 9; i++) {
            Ingredient ingredient = recipeItems.get(i);
            ItemStack itemStack = simpleContainer.getItem(i + 1);

            if (!ingredient.test(itemStack)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ElectricCompressorRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "electric_compressing";
    }

    public static class Serializer implements RecipeSerializer<ElectricCompressorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Spacecraft.MOD_ID, "electric_compressing"); //type

        @Override
        public ElectricCompressorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            // Parse the ingredients
            JsonArray pattern = GsonHelper.getAsJsonArray(pSerializedRecipe, "pattern");
            JsonElement keyElement = GsonHelper.getAsJsonObject(pSerializedRecipe, "key");
            JsonObject key = keyElement.getAsJsonObject();

            // Create a map to store character-to-ingredient mappings
            Map<Character, Ingredient> ingredientMap = new HashMap<>();

            // Iterate over the keys in the key object
            for (Map.Entry<String, JsonElement> entry : key.entrySet()) {
                Ingredient ingredient = Ingredient.fromJson(entry.getValue());
                // Get the character associated with the ingredient
                char keyChar = entry.getKey().charAt(0); // Assuming each key is a single character
                ingredientMap.put(keyChar, ingredient);
            }

            // Create a list to store the ingredients based on the pattern
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);
            int index = 0; // Index for the pattern

            // Iterate over the pattern and fill in the ingredients list
            for (JsonElement patternRow : pattern) {
                String patternRowString = patternRow.getAsString();
                for (char c : patternRowString.toCharArray()) {
                    // Skip if the character is a space
                    if (c != ' ') {
                        // Get the ingredient associated with the character from the map
                        Ingredient ingredient = ingredientMap.get(c);
                        if (ingredient != null) {
                            inputs.set(index++, ingredient);
                        } else {
                            throw new IllegalStateException("Invalid pattern character: " + c);
                        }
                    } else {
                        inputs.set(index++, Ingredient.EMPTY);
                    }
                }
            }

            return new ElectricCompressorRecipe(pRecipeId, output, inputs);
        }

        @Override
        public @Nullable ElectricCompressorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < 9; i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new ElectricCompressorRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElectricCompressorRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }
    }
}
