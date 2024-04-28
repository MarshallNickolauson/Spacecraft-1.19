package net.marsh.spacecraft.block;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.custom.CoalGeneratorBlock;
import net.marsh.spacecraft.item.ModCreativeModTab;
import net.marsh.spacecraft.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Spacecraft.MOD_ID);

    public static final RegistryObject<Block> DEEPSLATE_ALUMINUM_ORE = registerBlock("deepslate_aluminum_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerBlock("deepslate_lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_METEORIC_IRON_ORE = registerBlock("deepslate_meteoric_iron_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_PALLADIUM_ORE = registerBlock("deepslate_palladium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> 9), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_SILICON_ORE = registerBlock("deepslate_silicon_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = registerBlock("deepslate_titanium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerBlock("deepslate_uranium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> 9), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);

    public static final RegistryObject<Block> ALUMINUM_ORE = registerBlock("aluminum_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> LEAD_ORE = registerBlock("lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> METEORIC_IRON_ORE = registerBlock("meteoric_iron_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> PALLADIUM_ORE = registerBlock("palladium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 9), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> SILICON_ORE = registerBlock("silicon_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> TIN_ORE = registerBlock("tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> TITANIUM_ORE = registerBlock("titanium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);
    public static final RegistryObject<Block> URANIUM_ORE = registerBlock("uranium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 9), UniformInt.of(3, 7)), ModCreativeModTab.SPACECRAFT_TAB);

    public static final RegistryObject<Block> COAL_GENERATOR = registerBlock("coal_generator", () -> new CoalGeneratorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3f).noOcclusion().lightLevel(state -> state.getValue(CoalGeneratorBlock.LIT) ? 5 : 0)), ModCreativeModTab.SPACECRAFT_TAB);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }
}
