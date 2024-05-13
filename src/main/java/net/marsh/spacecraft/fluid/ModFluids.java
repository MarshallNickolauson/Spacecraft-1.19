package net.marsh.spacecraft.fluid;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.block.ModBlocks;
import net.marsh.spacecraft.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Spacecraft.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_CRUDE_OIL = FLUIDS.register("crude_oil_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.CRUDE_OIL_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_CRUDE_OIL = FLUIDS.register("flowing_crude_oil",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.CRUDE_OIL_FLUID_PROPERTIES));

    public static final ForgeFlowingFluid.Properties CRUDE_OIL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.CRUDE_OIL_FLUID_TYPE, SOURCE_CRUDE_OIL, FLOWING_CRUDE_OIL)
            .slopeFindDistance(2).levelDecreasePerBlock(2)
            .block(ModBlocks.CRUDE_OIL_BLOCK)
            .bucket(ModItems.CRUDE_OIL_BUCKET);

    public static void register(IEventBus eventBus) { FLUIDS.register(eventBus); }
}
