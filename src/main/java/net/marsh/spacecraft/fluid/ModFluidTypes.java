package net.marsh.spacecraft.fluid;

import com.mojang.math.Vector3f;
import net.marsh.spacecraft.Spacecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation CRUDE_OIL_OVERLAY_RL = new ResourceLocation(Spacecraft.MOD_ID, "misc/in_crude_oil");
    public static final ResourceLocation FUEL_OVERLAY_RL = new ResourceLocation(Spacecraft.MOD_ID, "misc/in_fuel");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Spacecraft.MOD_ID);

    public static final RegistryObject<FluidType> CRUDE_OIL_FLUID_TYPE = register("crude_oil_fluid", FluidType.Properties.create().density(50).viscosity(50), CRUDE_OIL_OVERLAY_RL);
    public static final RegistryObject<FluidType> FUEL_FLUID_TYPE = register("fuel_fluid", FluidType.Properties.create().density(15).viscosity(15), FUEL_OVERLAY_RL);

    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties, ResourceLocation OVERLAY_RL) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, OVERLAY_RL,
                0xFF000000, new Vector3f(0f, 0f, 0f), properties));
    }

    public static void register(IEventBus eventBus) { FLUID_TYPES.register(eventBus); }
}
