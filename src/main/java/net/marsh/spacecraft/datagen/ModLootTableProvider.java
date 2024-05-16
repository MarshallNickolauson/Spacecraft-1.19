package net.marsh.spacecraft.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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

        //TODO createOreDropLootTable();
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
}
