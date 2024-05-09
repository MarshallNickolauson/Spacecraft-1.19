package net.marsh.spacecraft.block;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Spacecraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR = BLOCK_ENTITIES.register("coal_generator", () -> BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new, ModBlocks.COAL_GENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = BLOCK_ENTITIES.register("electric_furnace", () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricArcFurnaceBlockEntity>> ELECTRIC_ARC_FURNACE = BLOCK_ENTITIES.register("electric_arc_furnace", () -> BlockEntityType.Builder.of(ElectricArcFurnaceBlockEntity::new, ModBlocks.ELECTRIC_ARC_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL = BLOCK_ENTITIES.register("solar_panel", () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR = BLOCK_ENTITIES.register("circuit_fabricator", () -> BlockEntityType.Builder.of(CircuitFabricatorBlockEntity::new, ModBlocks.CIRCUIT_FABRICATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricCompressorBlockEntity>> ELECTRIC_COMPRESSOR = BLOCK_ENTITIES.register("electric_compressor", () -> BlockEntityType.Builder.of(ElectricCompressorBlockEntity::new, ModBlocks.ELECTRIC_COMPRESSOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<SteelFoundryBlockEntity>> STEEL_FOUNDRY = BLOCK_ENTITIES.register("steel_foundry", () -> BlockEntityType.Builder.of(SteelFoundryBlockEntity::new, ModBlocks.STEEL_FOUNDRY.get()).build(null));

    public static void register(IEventBus eventBus) { BLOCK_ENTITIES.register(eventBus); }
}
