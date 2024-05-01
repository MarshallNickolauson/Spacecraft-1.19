package net.marsh.spacecraft.datagen;

import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

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
    }

    private static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

}
