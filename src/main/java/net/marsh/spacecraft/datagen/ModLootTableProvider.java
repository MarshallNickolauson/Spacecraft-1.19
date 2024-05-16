package net.marsh.spacecraft.datagen;

import com.google.gson.*;
import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.ModBlocks;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.nio.file.Path;

public class ModLootTableProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public ModLootTableProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        createDropSelfLootTable("coal_generator", cachedOutput);
        createDropSelfLootTable("electric_furnace", cachedOutput);
        createDropSelfLootTable("electric_arc_furnace", cachedOutput);
        createDropSelfLootTable("solar_panel", cachedOutput);
        createDropSelfLootTable("circuit_fabricator", cachedOutput);
        createDropSelfLootTable("electric_compressor", cachedOutput);
        createDropSelfLootTable("steel_foundry", cachedOutput);
        createDropSelfLootTable("wire_block", cachedOutput);

        createOreDropLootTable("aluminum_ore", "raw_aluminum", cachedOutput);
        createOreDropLootTable("lead_ore", "raw_lead", cachedOutput);
        createOreDropLootTable("meteoric_iron_ore", "raw_meteoric_iron", cachedOutput);
        createOreDropLootTable("palladium_ore", "raw_palladium", cachedOutput);
        createOreDropLootTable("silicon_ore", "raw_silicon", cachedOutput);
        createOreDropLootTable("tin_ore", "raw_tin", cachedOutput);
        createOreDropLootTable("titanium_ore", "raw_titanium", cachedOutput);
        createOreDropLootTable("uranium_ore", "raw_uranium", cachedOutput);
        createOreDropLootTable("deepslate_aluminum_ore", "raw_aluminum", cachedOutput);
        createOreDropLootTable("deepslate_lead_ore", "raw_lead", cachedOutput);
        createOreDropLootTable("deepslate_meteoric_iron_ore", "raw_meteoric_iron", cachedOutput);
        createOreDropLootTable("deepslate_palladium_ore", "raw_palladium", cachedOutput);
        createOreDropLootTable("deepslate_silicon_ore", "raw_silicon", cachedOutput);
        createOreDropLootTable("deepslate_tin_ore", "raw_tin", cachedOutput);
        createOreDropLootTable("deepslate_titanium_ore", "raw_titanium", cachedOutput);
        createOreDropLootTable("deepslate_uranium_ore", "raw_uranium", cachedOutput);
    }

    @Override
    public String getName() {
        return Spacecraft.MOD_ID + " Loot Tables";
    }

    public void createDropSelfLootTable(String name, CachedOutput cachedOutput) {
        LootTableJson lootTableJson = new LootTableJson(name);
        JsonElement json = GSON.toJsonTree(lootTableJson);

        Path outputPath = generator.getOutputFolder().resolve("data/" + Spacecraft.MOD_ID + "/loot_tables/blocks/" + name + ".json");

        try {
            DataProvider.saveStable(cachedOutput, json, outputPath);
            System.out.println("Loot table for " + name + " created at " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class LootTableJson {
        private final String type;
        private final Pool[] pools;

        public LootTableJson(String name) {
            this.type = "minecraft:block";
            this.pools = new Pool[]{new Pool(name)};
        }

        private static class Pool {
            private final float bonus_rolls = 0.0f;
            private final Condition[] conditions = {new Condition()};
            private final Entry[] entries;
            private final float rolls = 1.0f;

            public Pool(String name) {
                this.entries = new Entry[]{new Entry(name)};
            }
        }

        private static class Condition {
            private final String condition = "minecraft:survives_explosion";
        }

        private static class Entry {
            private final String type = "minecraft:item";
            private final Function[] functions = {new Function()};
            private final String name;

            public Entry(String name) {
                this.name = Spacecraft.MOD_ID + ":" + name;
            }
        }

        private static class Function {
            private final String function = "minecraft:copy_name";
            private final String source = "block_entity";
        }
    }

    public void createOreDropLootTable(String oreName, String rawMaterialName, CachedOutput cachedOutput) {
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:block");

        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);

        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:alternatives");

        JsonArray children = new JsonArray();

        // Child 1
        JsonObject child1 = new JsonObject();
        child1.addProperty("type", "minecraft:item");
        JsonArray conditionsArray1 = new JsonArray(); // Corrected to array
        JsonObject conditionObject1 = new JsonObject();
        conditionObject1.addProperty("condition", "minecraft:match_tool");
        JsonObject predicate1 = new JsonObject();
        JsonArray enchantments1 = new JsonArray();
        JsonObject enchantment1 = new JsonObject();
        enchantment1.addProperty("enchantment", "minecraft:silk_touch");
        JsonObject levels1 = new JsonObject();
        levels1.addProperty("min", 1);
        enchantment1.add("levels", levels1);
        enchantments1.add(enchantment1);
        predicate1.add("enchantments", enchantments1);
        conditionObject1.add("predicate", predicate1);
        conditionsArray1.add(conditionObject1); // Adding the condition object to array
        child1.add("conditions", conditionsArray1); // Adding conditions to child1
        child1.addProperty("name", Spacecraft.MOD_ID + ":" + oreName);

        // Child 2
        JsonObject child2 = new JsonObject();
        child2.addProperty("type", "minecraft:item");
        JsonArray functions = new JsonArray();
        JsonObject setCountFunction = new JsonObject();
        setCountFunction.addProperty("function", "minecraft:set_count");
        JsonObject count = new JsonObject();
        count.addProperty("type", "minecraft:uniform");
        count.addProperty("min", 2.0);
        count.addProperty("max", 5.0);
        setCountFunction.add("count", count);
        setCountFunction.addProperty("add", false);
        functions.add(setCountFunction);
        JsonObject applyBonusFunction = new JsonObject();
        applyBonusFunction.addProperty("function", "minecraft:apply_bonus");
        applyBonusFunction.addProperty("enchantment", "minecraft:fortune");
        applyBonusFunction.addProperty("formula", "minecraft:ore_drops");
        functions.add(applyBonusFunction);
        JsonObject explosionDecayFunction = new JsonObject();
        explosionDecayFunction.addProperty("function", "minecraft:explosion_decay");
        functions.add(explosionDecayFunction);
        child2.add("functions", functions);
        child2.addProperty("name", Spacecraft.MOD_ID + ":" + rawMaterialName);

        children.add(child1);
        children.add(child2);

        entry.add("children", children);
        entries.add(entry);

        pool.add("entries", entries);
        pools.add(pool);
        lootTable.add("pools", pools);

        Path outputPath = generator.getOutputFolder().resolve("data/" + Spacecraft.MOD_ID + "/loot_tables/blocks/" + oreName + ".json");

        try {
            DataProvider.saveStable(cachedOutput, lootTable, outputPath);
            System.out.println("Ore drop loot table for " + oreName + " created at " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
