package net.marsh.spacecraft.world.feature;

import net.marsh.spacecraft.Spacecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Spacecraft.MOD_ID);

    public static final RegistryObject<PlacedFeature> ALUMINUM_ORE_PLACED = PLACED_FEATURES.register("aluminum_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.ALUMINUM_ORE.getHolder().get(), commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> LEAD_ORE_PLACED = PLACED_FEATURES.register("lead_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.LEAD_ORE.getHolder().get(), commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> METEORIC_IRON_ORE_PLACED = PLACED_FEATURES.register("meteoric_iron_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.METEORIC_IRON_ORE.getHolder().get(), commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> PALLADIUM_ORE_PLACED = PLACED_FEATURES.register("palladium_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.PALLADIUM_ORE.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> SILICON_ORE_PLACED = PLACED_FEATURES.register("silicon_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.SILICON_ORE.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> TIN_ORE_PLACED = PLACED_FEATURES.register("tin_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.TIN_ORE.getHolder().get(), commonOrePlacement(15, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> TITANIUM_ORE_PLACED = PLACED_FEATURES.register("titanium_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.TITANIUM_ORE.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    public static final RegistryObject<PlacedFeature> URANIUM_ORE_PLACED = PLACED_FEATURES.register("uranium_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.URANIUM_ORE.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}

