package net.marsh.spacecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class CircuitFabricatorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public CircuitFabricatorRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {

        for (int i = 0; i < 5; i++) {
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CircuitFabricatorRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "circuit_fabricating";
    }

    public static class Serializer implements RecipeSerializer<CircuitFabricatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Spacecraft.MOD_ID, "circuit_fabricating");

        @Override
        public CircuitFabricatorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            String ingredient = GsonHelper.getAsString(pSerializedRecipe, "ingredient");
            Item ingredientItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredient));

            Ingredient[] staticIngredients = {
                    Ingredient.of(new ItemStack(Items.DIAMOND)),
                    Ingredient.of(new ItemStack(ModItems.RAW_SILICON.get())),
                    Ingredient.of(new ItemStack(ModItems.RAW_SILICON.get())),
                    Ingredient.of(new ItemStack(Items.REDSTONE))
            };

            NonNullList<Ingredient> inputs = NonNullList.withSize(5, Ingredient.EMPTY);
            inputs.set(0, staticIngredients[0]);
            inputs.set(1, staticIngredients[1]);
            inputs.set(2, staticIngredients[2]);
            inputs.set(3, staticIngredients[3]);
            inputs.set(4, Ingredient.of(ingredientItem));

            return new CircuitFabricatorRecipe(pRecipeId, output, inputs);
        }

        @Override
        public @Nullable CircuitFabricatorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i <= 5; i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new CircuitFabricatorRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CircuitFabricatorRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }
    }
}
