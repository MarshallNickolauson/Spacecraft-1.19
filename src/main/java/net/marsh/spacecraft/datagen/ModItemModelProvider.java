package net.marsh.spacecraft.datagen;

import net.marsh.spacecraft.Spacecraft;
import net.marsh.spacecraft.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Spacecraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.CARBON_FRAGMENTS);
        simpleItem(ModItems.RAW_ALUMINUM);
        simpleItem(ModItems.RAW_LEAD);
        simpleItem(ModItems.RAW_METEORIC_IRON);
        simpleItem(ModItems.RAW_PALLADIUM);
        simpleItem(ModItems.RAW_SILICON);
        simpleItem(ModItems.RAW_TIN);
        simpleItem(ModItems.RAW_TITANIUM);
        simpleItem(ModItems.RAW_URANIUM);
        simpleItem(ModItems.ALUMINUM_INGOT);
        simpleItem(ModItems.LEAD_INGOT);
        simpleItem(ModItems.STEEL_INGOT);
        simpleItem(ModItems.TIN_INGOT);
        simpleItem(ModItems.TITANIUM_INGOT);
        simpleItem(ModItems.URANIUM_INGOT);
        simpleItem(ModItems.PALLADIUM_INGOT);
        simpleItem(ModItems.METEORIC_IRON_INGOT);
        simpleItem(ModItems.COMPRESSED_IRON);
        simpleItem(ModItems.COMPRESSED_ALUMINUM);
        simpleItem(ModItems.COMPRESSED_BRONZE);
        simpleItem(ModItems.COMPRESSED_COPPER);
        simpleItem(ModItems.COMPRESSED_METEORIC_IRON);
        simpleItem(ModItems.COMPRESSED_PALLADIUM);
        simpleItem(ModItems.COMPRESSED_STEEL);
        simpleItem(ModItems.COMPRESSED_TIN);
        simpleItem(ModItems.COMPRESSED_TITANIUM);
        simpleItem(ModItems.COMPRESSED_LEAD);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Spacecraft.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(Spacecraft.MOD_ID, "item/" + item.getId().getPath()));
    }
}
