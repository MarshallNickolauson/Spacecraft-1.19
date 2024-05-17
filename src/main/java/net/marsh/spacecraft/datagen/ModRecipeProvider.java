package net.marsh.spacecraft.datagen;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.item.ModItems;
import net.marsh.spacecraft.recipe.builder.CircuitFabricatorRecipeBuilder;
import net.marsh.spacecraft.recipe.builder.CompressingShapedRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        oreCooking(consumer, List.of(ModItems.RAW_ALUMINUM.get(), ModBlocks.ALUMINUM_ORE.get(), ModBlocks.DEEPSLATE_ALUMINUM_ORE.get()), ModItems.ALUMINUM_INGOT.get(), 0.7F, 200, "aluminum");
        oreCooking(consumer, List.of(ModItems.RAW_LEAD.get(), ModBlocks.LEAD_ORE.get(), ModBlocks.DEEPSLATE_LEAD_ORE.get()), ModItems.LEAD_INGOT.get(), 0.7F, 200, "lead");
        oreCooking(consumer, List.of(ModItems.RAW_METEORIC_IRON.get(), ModBlocks.METEORIC_IRON_ORE.get(), ModBlocks.DEEPSLATE_METEORIC_IRON_ORE.get()), ModItems.METEORIC_IRON_INGOT.get(), 0.7F, 200, "meteoric_iron");
        oreCooking(consumer, List.of(ModItems.RAW_PALLADIUM.get(), ModBlocks.PALLADIUM_ORE.get(), ModBlocks.DEEPSLATE_PALLADIUM_ORE.get()), ModItems.PALLADIUM_INGOT.get(), 0.7F, 200, "palladium");
        oreCooking(consumer, List.of(ModBlocks.SILICON_ORE.get(), ModBlocks.DEEPSLATE_SILICON_ORE.get()), ModItems.RAW_SILICON.get(), 0.7F, 200, "silicon");
        oreCooking(consumer, List.of(ModItems.RAW_TIN.get(), ModBlocks.TIN_ORE.get(), ModBlocks.DEEPSLATE_TIN_ORE.get()), ModItems.TIN_INGOT.get(), 0.7F, 200, "tin");
        oreCooking(consumer, List.of(ModItems.RAW_TITANIUM.get(), ModBlocks.TITANIUM_ORE.get(), ModBlocks.DEEPSLATE_TITANIUM_ORE.get()), ModItems.TITANIUM_INGOT.get(), 0.7F, 200, "titanium");
        oreCooking(consumer, List.of(ModItems.RAW_URANIUM.get(), ModBlocks.URANIUM_ORE.get(), ModBlocks.DEEPSLATE_URANIUM_ORE.get()), ModItems.URANIUM_INGOT.get(), 0.7F, 200, "uranium");

        new CompressingShapedRecipeBuilder(ModItems.COMPRESSED_BRONZE.get(), 1)
                .pattern("  ")
                .pattern(" CC")
                .pattern(" II")
                .define('C', Ingredient.of(Items.COPPER_INGOT))
                .define('I', Ingredient.of(ModItems.ALUMINUM_INGOT.get()))
                .save(consumer, new ResourceLocation(Spacecraft.MOD_ID, "compressed_bronze_from_electric_compressor"));

        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.ALUMINUM_INGOT.get()), ModItems.COMPRESSED_ALUMINUM.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(Items.COPPER_INGOT), ModItems.COMPRESSED_COPPER.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(Items.IRON_INGOT), ModItems.COMPRESSED_IRON.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.LEAD_INGOT.get()), ModItems.COMPRESSED_LEAD.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.METEORIC_IRON_INGOT.get()), ModItems.COMPRESSED_METEORIC_IRON.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.PALLADIUM_INGOT.get()), ModItems.COMPRESSED_PALLADIUM.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.STEEL_INGOT.get()), ModItems.COMPRESSED_STEEL.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.TIN_INGOT.get()), ModItems.COMPRESSED_TIN.get());
        buildCompressingShapedRecipe(consumer, Ingredient.of(ModItems.TITANIUM_INGOT.get()), ModItems.COMPRESSED_TITANIUM.get());

        buildCircuitFabricatorRecipe(consumer, Items.REDSTONE_TORCH, ModItems.BASIC_WAFER.get());
        buildCircuitFabricatorRecipe(consumer, Items.COMPARATOR, ModItems.ADVANCED_WAFER.get());
        buildCircuitFabricatorRecipe(consumer, Items.LAPIS_BLOCK, ModItems.SOLAR_WAFER.get());

        ShapelessRecipeBuilder.shapeless(ModItems.CARBON_FRAGMENTS.get(), 4).requires(Items.COAL).unlockedBy(getHasName(Items.COAL), has(Items.COAL)).save(consumer, new ResourceLocation(Spacecraft.MOD_ID, "carbon_fragments_from_coal"));
        ShapelessRecipeBuilder.shapeless(ModItems.CARBON_FRAGMENTS.get(), 4).requires(Items.CHARCOAL).unlockedBy(getHasName(Items.COAL), has(Items.COAL)).save(consumer, new ResourceLocation(Spacecraft.MOD_ID, "carbon_fragments_from_charcoal"));
    }

    private void buildCircuitFabricatorRecipe(Consumer<FinishedRecipe> consumer, Item ingredient, Item result) {
        new CircuitFabricatorRecipeBuilder(ingredient, result).save(consumer, new ResourceLocation(Spacecraft.MOD_ID, result + "_from_circuit_fabricating"));
    }

    private void buildCompressingShapedRecipe(Consumer<FinishedRecipe> consumer, Ingredient ingredient, ItemLike result) {
        new CompressingShapedRecipeBuilder(result, 1)
                .pattern("  ")
                .pattern(" II")
                .pattern(" II")
                .define('I', ingredient)
                .save(consumer, new ResourceLocation(Spacecraft.MOD_ID, result.asItem() + "_from_electric_compressor"));
    }

    private static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }
}
