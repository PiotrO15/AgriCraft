package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriFluidCondition;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizerParticle;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizerVariant;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilVariant;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.*;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.*;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.*;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.*;
import static com.agricraft.agricraft.api.requirement.AgriSeason.*;

@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(AgriApi.AGRIPLANTS, DatagenEventHandler::registerPlants)
								.add(AgriApi.AGRISOILS, DatagenEventHandler::registerSoils)
								.add(AgriApi.AGRIMUTATIONS, DatagenEventHandler::registerMutations)
								.add(AgriApi.AGRIFERTILIZERS, DatagenEventHandler::registerFertilizers),
						// Generate dynamic registry objects for this mod
						Set.of("minecraft", AgriApi.MOD_ID)
				)
		);
		generator.addProvider(event.includeClient(), new MinecraftCropModelProvider(event.getGenerator(), event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new MinecraftSeedModelProvider(event.getGenerator(), event.getExistingFileHelper()));
	}

	private static void registerMinecraftPlant(BootstapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		context.register(
				ResourceKey.create(AgriApi.AGRIPLANTS, new ResourceLocation("minecraft", plantId)),
				plant
		);
	}

	private static void registerAgriCraftPlant(BootstapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		context.register(
				ResourceKey.create(AgriApi.AGRIPLANTS, new ResourceLocation(AgriApi.MOD_ID, plantId)),
				plant
		);
	}

	private static void registerSoil(BootstapContext<AgriSoil> context, String soilId, AgriSoil soil) {
		context.register(
				ResourceKey.create(AgriApi.AGRISOILS, new ResourceLocation("minecraft", soilId)),
				soil
		);
	}

	private static void registerMinecraftMutation(BootstapContext<AgriMutation> context, String mutationId, AgriMutation mutation) {
		context.register(
				ResourceKey.create(AgriApi.AGRIMUTATIONS, new ResourceLocation("minecraft", mutationId)),
				mutation
		);
	}

	private static void registerAgricraftMutation(BootstapContext<AgriMutation> context, String mutationId, AgriMutation mutation) {
		context.register(
				ResourceKey.create(AgriApi.AGRIMUTATIONS, new ResourceLocation(AgriApi.MOD_ID, mutationId)),
				mutation
		);
	}

	private static void registerPlants(BootstapContext<AgriPlant> context) {
		registerMinecraftPlant(context, "allium", new AgriPlant.Builder()
				.seeds()  // AgriSeed.builder().chances(0.0, 1.0, 0.0).build()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:magenta_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:allium").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "azure_bluet", new AgriPlant.Builder()
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:azure_bluet").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "bamboo", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:bamboo").chances(0.0, 1.0, 0.0).build())
				.stages(3, 6, 9, 12, 15, 18, 21, 24)
				.chances(0.9, 0.01, 0.1)
				.products(AgriProduct.builder().item("minecraft:bamboo").count(1, 2, 1000.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.4)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.4)
						.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.4)
						.light(10, 16, 0.5)
						.seasons(SPRING, SUMMER)
						.build())
				.build());
		registerMinecraftPlant(context, "beetroot", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:beetroot_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:beetroot").count(1, 3, 0.95).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(AUTUMN)
						.build())
				.build());
		registerMinecraftPlant(context, "blue_orchid", new AgriPlant.Builder()
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:light_blue_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:blue_orchid").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "brown_mushroom", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:brown_mushroom").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:brown_mushroom").count(2, 5, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.15)
						.acidity(NEUTRAL, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2)
						.light(0, 10, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "cactus", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:cactus").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:cactus").count(1, 3, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(ARID, EQUAL, 0.34)
						.acidity(NEUTRAL, EQUAL, 0.2)
						.nutrients(LOW, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SUMMER)
						.build())
				.build());
		registerMinecraftPlant(context, "carrot", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:carrot").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 4, 5, 6, 7, 8, 9)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:carrot").count(1, 4, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SPRING, AUTUMN)
						.build())
				.build());
		registerMinecraftPlant(context, "cornflower", new AgriPlant.Builder()
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:blue_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:cornflower").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "crimson_fungus", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:crimson_fungus").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:crimson_fungus").count(2, 5, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(ARID, EQUAL, 0.15)
						.acidity(HIGHLY_ACIDIC, EQUAL, 0.2)
						.nutrients(LOW, EQUAL_OR_LOWER, 0.1)
						.light(0, 10, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "dandelion", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:yellow_dye").count(1, 1, 0.5).build())
				.clips(AgriProduct.builder().item("minecraft:dandelion").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "kelp", new AgriPlant.Builder()
				.seeds()
				.stages(6, 6, 12, 12, 12, 18, 18, 24)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:kelp").count(1, 1, 0.75).build())
				.requirement(AgriRequirement.builder()
						.humidity(WATERY, EQUAL_OR_HIGHER, 0.4)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1)
						.light(5, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.fluid(AgriFluidCondition.builder().fluid("minecraft:water").build())
						.build())
				.build());
		registerMinecraftPlant(context, "lily_of_the_valley", new AgriPlant.Builder()
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:white_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:lily_of_the_valley").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "melon", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:melon_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:melon").count(3, 5, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "nether_wart", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:nether_wart").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:nether_wart").count(1, 3, 0.95).build())
				.requirement(AgriRequirement.builder()
						.humidity(ARID, EQUAL, 0.15)
						.acidity(NEUTRAL, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "orange_tulip", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:orange_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:orange_tulip").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "oxeye_daisy", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:oxeye_daisy").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "pink_tulip", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:pink_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:pink_tulip").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "poppy", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:red_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:poppy").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "potato", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:potato").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 4, 5, 6, 7, 8, 9)
				.chances(0.75, 0.025, 0.1)
				.products(
						AgriProduct.builder().item("minecraft:potato").count(1, 4, 0.95).build(),
						AgriProduct.builder().item("minecraft:poisonous_potato").count(1, 2, 0.02).build()
				)
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "pumpkin", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:pumpkin_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:pumpkin").count(1, 2, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SUMMER, AUTUMN)
						.build())
				.build());
		registerMinecraftPlant(context, "red_mushroom", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:red_mushroom").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:red_mushroom").count(2, 4, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.15)
						.acidity(NEUTRAL, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2)
						.light(0, 10, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "red_tulip", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:red_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:red_tulip").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "seagrass", new AgriPlant.Builder()
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:seagrass").count(1, 1, 0.75).build())
				.requirement(AgriRequirement.builder()
						.humidity(WATERY, EQUAL_OR_HIGHER, 0.4)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1)
						.light(5, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.fluid(AgriFluidCondition.builder().fluid("minecraft:water").build())
						.build())
				.build());
		registerMinecraftPlant(context, "sea_pickle", new AgriPlant.Builder()
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:sea_pickle").count(1, 1, 0.75).build())
				.requirement(AgriRequirement.builder()
						.humidity(WATERY, EQUAL_OR_HIGHER, 0.4)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1)
						.light(0, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.fluid(AgriFluidCondition.builder().fluid("minecraft:water").build())
						.build())
				.build());
		registerMinecraftPlant(context, "sugar_cane", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:sugar_cane").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:sugar_cane").count(1, 2, 1000.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SUMMER)
						.build())
				.build());
		registerMinecraftPlant(context, "sweet_berries", new AgriPlant.Builder()
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:sweet_berries").count(1, 1, 0.75).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN)
						.build())
				.build());
		registerMinecraftPlant(context, "warped_fungus", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:warped_fungus").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 5, 6, 8, 9, 11, 12)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:warped_fungus").count(2, 5, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(ARID, EQUAL, 0.15)
						.acidity(HIGHLY_ALKALINE, EQUAL, 0.2)
						.nutrients(LOW, EQUAL_OR_LOWER, 0.1)
						.light(0, 10, 0.5)
						.seasons(SPRING, SUMMER, AUTUMN, WINTER)
						.build())
				.build());
		registerMinecraftPlant(context, "wheat", new AgriPlant.Builder()
				.seeds(AgriSeed.builder().item("minecraft:wheat_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:wheat").count(1, 3, 0.95).build())
				.requirement(AgriRequirement.builder()
						.humidity(WET, EQUAL, 0.15)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(HIGH, EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons(SUMMER, AUTUMN)
						.build())
				.build());
		registerMinecraftPlant(context, "white_tulip", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:white_tulip").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());
		registerMinecraftPlant(context, "wither_rose", new AgriPlant.Builder()
				.stages16()
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:black_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:wither_rose").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(DAMP, EQUAL, 0.2)
						.acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2)
						.nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons(SPRING)
						.build())
				.build());

	}

	private static void registerSoils(BootstapContext<AgriSoil> context) {
		registerSoil(context, "farmland", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:farmland").build())
				.humidity(WET)
				.acidity(SLIGHTLY_ACIDIC)
				.nutrients(HIGH)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "sand", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().tag("forge:sand").build(),
						AgriSoilVariant.builder().tag("minecraft:sand").build())
				.humidity(ARID)
				.acidity(NEUTRAL)
				.nutrients(LOW)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "clay", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:clay").build())
				.humidity(FLOODED)
				.acidity(SLIGHTLY_ACIDIC)
				.nutrients(MEDIUM)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "podzol", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:podzol").build())
				.humidity(DAMP)
				.acidity(SLIGHTLY_ACIDIC)
				.nutrients(VERY_HIGH)
				.growthModifier(1.1)
				.build());
		registerSoil(context, "mycelium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:mycelium").build())
				.humidity(DAMP)
				.acidity(NEUTRAL)
				.nutrients(LOW)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "soul_sand", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:soul_sand").build())
				.humidity(ARID)
				.acidity(NEUTRAL)
				.nutrients(HIGH)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "soul_soil", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:soul_soil").build())
				.humidity(ARID)
				.acidity(NEUTRAL)
				.nutrients(VERY_HIGH)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "crimson_nylium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:crimson_nylium").build())
				.humidity(ARID)
				.acidity(HIGHLY_ACIDIC)
				.nutrients(LOW)
				.growthModifier(0.75)
				.build());
		registerSoil(context, "warped_nylium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:warped_nylium").build())
				.humidity(ARID)
				.acidity(HIGHLY_ALKALINE)
				.nutrients(LOW)
				.growthModifier(0.75)
				.build());
	}

	private static void registerMutations(BootstapContext<AgriMutation> context) {
		registerMinecraftMutation(context, "allium", AgriMutation.builder().child("minecraft:allium").parents("minecraft:poppy", "minecraft:blue_orchid").chance(0.75).build());
		registerMinecraftMutation(context, "azure_bluet", AgriMutation.builder().child("minecraft:azure_bluet").parents("minecraft:dandelion", "minecraft:lily_of_the_valley").chance(0.75).build());
		registerMinecraftMutation(context, "bamboo", AgriMutation.builder().child("minecraft:bamboo").parents("minecraft:sugar_cane", "minecraft:cactus").chance(0.5).build());
		registerMinecraftMutation(context, "blue_orchid", AgriMutation.builder().child("minecraft:blue_orchid").parents("minecraft:poppy", "minecraft:dandelion").chance(0.75).build());
		registerMinecraftMutation(context, "brown_mushroom", AgriMutation.builder().child("minecraft:brown_mushroom").parents("minecraft:potato", "minecraft:carrot").chance(0.5).build());
		registerMinecraftMutation(context, "cactus", AgriMutation.builder().child("minecraft:cactus").parents("minecraft:sugar_cane", "minecraft:potato").chance(0.5).build());
		registerMinecraftMutation(context, "carrot", AgriMutation.builder().child("minecraft:carrot").parents("minecraft:wheat", "minecraft:potato").chance(0.75).build());
		registerMinecraftMutation(context, "cornflower", AgriMutation.builder().child("minecraft:cornflower").parents("minecraft:blue_orchid", "minecraft:lily_of_the_valley").chance(0.75).build());
		registerMinecraftMutation(context, "crimson_fungus", AgriMutation.builder().child("minecraft:crimson_fungus").parents("minecraft:nether_wart", "minecraft:poppy").chance(0.75).build());
		registerMinecraftMutation(context, "dandelion", AgriMutation.builder().child("minecraft:dandelion").parents("minecraft:pumpkin", "minecraft:wheat").chance(0.75).build());
		registerMinecraftMutation(context, "kelp", AgriMutation.builder().child("minecraft:kelp").parents("minecraft:seagrass", "minecraft:bamboo").chance(0.5).build());
		registerMinecraftMutation(context, "lily_of_the_valley", AgriMutation.builder().child("minecraft:lily_of_the_valley").parents("minecraft:white_tulip", "minecraft:oxeye_daisy").chance(0.75).build());
		registerMinecraftMutation(context, "melon", AgriMutation.builder().child("minecraft:melon").parents("minecraft:sugar_cane", "minecraft:pumpkin").chance(0.25).build());
		registerMinecraftMutation(context, "orange_tulip", AgriMutation.builder().child("minecraft:orange_tulip").parents("minecraft:oxeye_daisy", "minecraft:blue_orchid").chance(0.75).build());
		registerMinecraftMutation(context, "oxeye_daisy", AgriMutation.builder().child("minecraft:oxeye_daisy").parents("minecraft:dandelion", "minecraft:blue_orchid").chance(0.75).build());
		registerMinecraftMutation(context, "pink_tulip", AgriMutation.builder().child("minecraft:pink_tulip").parents("minecraft:allium", "minecraft:dandelion").chance(0.75).build());
		registerMinecraftMutation(context, "poppy", AgriMutation.builder().child("minecraft:poppy").parents("minecraft:sugar_cane", "minecraft:melon").chance(0.75).build());
		registerMinecraftMutation(context, "pumpkin", AgriMutation.builder().child("minecraft:pumpkin").parents("minecraft:carrot", "minecraft:potato").chance(0.25).build());
		registerMinecraftMutation(context, "red_mushroom", AgriMutation.builder().child("minecraft:red_mushroom").parents("minecraft:potato", "minecraft:carrot").chance(0.5).build());
		registerMinecraftMutation(context, "red_tulip", AgriMutation.builder().child("minecraft:red_tulip").parents("minecraft:poppy", "minecraft:allium").chance(0.75).build());
		registerMinecraftMutation(context, "seagrass", AgriMutation.builder().child("minecraft:seagrass").parents("minecraft:wheat", "minecraft:bamboo").chance(0.5).build());
		registerMinecraftMutation(context, "sea_pickle", AgriMutation.builder().child("minecraft:sea_pickle").parents("minecraft:seagrass", "minecraft:carrot").chance(0.5).build());
		registerMinecraftMutation(context, "sugar_cane", AgriMutation.builder().child("minecraft:sugar_cane").parents("minecraft:wheat", "minecraft:beetroot").chance(0.5).build());
		registerMinecraftMutation(context, "sweet_berries", AgriMutation.builder().child("minecraft:sweet_berries").parents("minecraft:sugar_cane", "minecraft:beetroot").chance(0.75).build());
		registerMinecraftMutation(context, "warped_fungus", AgriMutation.builder().child("minecraft:warped_fungus").parents("minecraft:nether_wart", "minecraft:blue_orchid").chance(0.75).build());
		registerMinecraftMutation(context, "white_tulip", AgriMutation.builder().child("minecraft:white_tulip").parents("minecraft:oxeye_daisy", "minecraft:dandelion").chance(0.75).build());
		registerMinecraftMutation(context, "wither_rose", AgriMutation.builder().child("minecraft:wither_rose").parents("minecraft:cornflower", "minecraft:poppy").chance(0.75).build());
	}

	private static void registerMinecraftFertilizer(BootstapContext<AgriFertilizer> context, String fertilizerId, AgriFertilizer fertilizer) {
		context.register(
				ResourceKey.create(AgriApi.AGRIFERTILIZERS, new ResourceLocation("minecraft", fertilizerId)),
				fertilizer
		);
	}
	private static void registerFertilizers(BootstapContext<AgriFertilizer> context) {
		registerMinecraftFertilizer(context, "bone_meal", new AgriFertilizer.Builder()
				.variants(new AgriFertilizerVariant.Builder().item("minecraft:bone_meal").build())
				.particles(new AgriFertilizerParticle("minecraft:happy_villager", 0.6, 0.4, 0.6, 2, List.of("positive")),
						new AgriFertilizerParticle("minecraft:smoke", 0.6, 0.4, 0.6, 2, List.of("negative")))
				.build());
	}

	private static class MinecraftCropModelProvider extends ModelProvider<BlockModelBuilder> {

		public MinecraftCropModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator.getPackOutput(), "minecraft", "crop", BlockModelBuilder::new, existingFileHelper);
		}

		@Override
		protected void registerModels() {
			this.with4TexturesIn8stage("allium", "agricraft:crop/crop_hash", "agricraft:plant/allium");
			this.withExistingParent("azure_bluet_stage0", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
			this.withExistingParent("azure_bluet_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
			this.withExistingParent("azure_bluet_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
			this.withExistingParent("azure_bluet_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage1");
			this.withExistingParent("azure_bluet_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage1");
			this.withExistingParent("azure_bluet_stage5", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage2");
			this.withExistingParent("azure_bluet_stage6", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage2");
			this.withExistingParent("azure_bluet_stage7", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/azure_bluet");
			this.withExistingParent("bamboo_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/bamboo_stage0");
			this.withExistingParent("bamboo_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage1");
			this.withExistingParent("bamboo_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage2");
			this.withExistingParent("bamboo_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage3");
			this.withExistingParent("bamboo_stage4", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage4");
			this.withExistingParent("bamboo_stage5", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage5");
			this.withExistingParent("bamboo_stage6", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage6");
			this.withExistingParent("bamboo_stage7", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage7");
			this.with4TexturesIn8stage("beetroot", "agricraft:crop/crop_hash", "minecraft:block/beetroots");
			this.withExistingParent("blue_orchid_stage0", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
			this.withExistingParent("blue_orchid_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
			this.withExistingParent("blue_orchid_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
			this.withExistingParent("blue_orchid_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage1");
			this.withExistingParent("blue_orchid_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage1");
			this.withExistingParent("blue_orchid_stage5", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage2");
			this.withExistingParent("blue_orchid_stage6", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage2");
			this.withExistingParent("blue_orchid_stage7", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/blue_orchid");
			this.with4TexturesIn8stage("brown_mushroom", "agricraft:crop/crop_hash", "agricraft:plant/brown_mushroom");
			this.with4TexturesIn8stage("cactus", "agricraft:crop/crop_hash", "agricraft:plant/cactus");
			this.with4TexturesIn8stage("carrot", "agricraft:crop/crop_hash", "minecraft:block/carrots");
			this.with4TexturesIn8stage("cornflower", "agricraft:crop/crop_plus", "agricraft:plant/cornflower");
			this.with4TexturesIn8stage("crimson_fungus", "agricraft:crop/crop_plus", "agricraft:plant/crimson_fungus");
			this.with4TexturesIn8stage("dandelion", "agricraft:crop/crop_hash", "agricraft:plant/dandelion");
			this.withExistingParent("kelp_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage1", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
			this.withExistingParent("kelp_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
			this.withExistingParent("kelp_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
			this.withExistingParent("kelp_stage5", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage6", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage7", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "agricraft:plant/kelp");
			this.with4TexturesIn8stage("lily_of_the_valley", "agricraft:crop/crop_plus", "agricraft:plant/lily_of_the_valley");
			this.withExistingParent("melon_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
			this.withExistingParent("melon_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
			this.withExistingParent("melon_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
			this.withExistingParent("melon_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage1");
			this.withExistingParent("melon_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage1");
			this.withExistingParent("melon_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage2");
			this.withExistingParent("melon_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage2");
			this.withExistingParent("melon_stage7", "agricraft:crop/crop_gourd").texture("crop", "agricraft:plant/melon_stage3").texture("gourd", "minecraft:block/melon_side");
			this.withExistingParent("nether_wart_stage0", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
			this.withExistingParent("nether_wart_stage1", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
			this.withExistingParent("nether_wart_stage2", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
			this.withExistingParent("nether_wart_stage3", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
			this.withExistingParent("nether_wart_stage4", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
			this.withExistingParent("nether_wart_stage5", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
			this.withExistingParent("nether_wart_stage6", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
			this.withExistingParent("nether_wart_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage2");
			this.with4TexturesIn8stage("orange_tulip", "agricraft:crop/crop_hash", "agricraft:plant/orange_tulip");
			this.with4TexturesIn8stage("oxeye_daisy", "agricraft:crop/crop_hash", "agricraft:plant/oxeye_daisy");
			this.with4TexturesIn8stage("pink_tulip", "agricraft:crop/crop_hash", "agricraft:plant/pink_tulip");
			this.with4TexturesIn8stage("poppy", "agricraft:crop/crop_hash", "agricraft:plant/poppy");
			this.with4TexturesIn8stage("potato", "agricraft:crop/crop_hash", "minecraft:block/potatoes");
			this.withExistingParent("pumpkin_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
			this.withExistingParent("pumpkin_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
			this.withExistingParent("pumpkin_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
			this.withExistingParent("pumpkin_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage1");
			this.withExistingParent("pumpkin_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage1");
			this.withExistingParent("pumpkin_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage2");
			this.withExistingParent("pumpkin_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage2");
			this.withExistingParent("pumpkin_stage7", "agricraft:crop/crop_gourd").texture("crop", "agricraft:plant/pumpkin_stage3").texture("gourd", "minecraft:block/pumpkin_side");
			this.with4TexturesIn8stage("red_mushroom", "agricraft:crop/crop_hash", "agricraft:plant/red_mushroom");
			this.with4TexturesIn8stage("red_tulip", "agricraft:crop/crop_hash", "agricraft:plant/red_tulip");
			this.withExistingParent("seagrass_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
			this.withExistingParent("seagrass_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
			this.withExistingParent("seagrass_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
			this.withExistingParent("seagrass_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage1");
			this.withExistingParent("seagrass_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage1");
			this.withExistingParent("seagrass_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage2");
			this.withExistingParent("seagrass_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage2");
			this.withExistingParent("seagrass_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/seagrass");
			this.with4TexturesIn8stage("sea_pickle", "agricraft:crop/crop_hash", "agricraft:plant/sea_pickle");
			this.with4TexturesIn8stage("sugar_cane", "agricraft:crop/crop_hash", "agricraft:plant/sugar_cane");
			this.with4TexturesIn8stage("sweet_berries", "agricraft:crop/crop_hash", "agricraft:plant/sweet_berries");
			this.with4TexturesIn8stage("warped_fungus", "agricraft:crop/crop_plus", "agricraft:plant/warped_fungus");
			this.withExistingParent("wheat_stage0", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage0");
			this.withExistingParent("wheat_stage1", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage1");
			this.withExistingParent("wheat_stage2", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage2");
			this.withExistingParent("wheat_stage3", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage3");
			this.withExistingParent("wheat_stage4", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage4");
			this.withExistingParent("wheat_stage5", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage5");
			this.withExistingParent("wheat_stage6", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage6");
			this.withExistingParent("wheat_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage7");
			this.with4TexturesIn8stage("white_tulip", "agricraft:crop/crop_hash", "agricraft:plant/white_tulip");
			this.with4TexturesIn8stage("wither_rose", "agricraft:crop/crop_hash", "agricraft:plant/wither_rose");
		}

		private void with4TexturesIn8stage(String name, String parent, String baseTexture) {
			this.withExistingParent(name + "_stage0", parent).texture("crop", baseTexture + "_stage0");
			this.withExistingParent(name + "_stage1", parent).texture("crop", baseTexture + "_stage0");
			this.withExistingParent(name + "_stage2", parent).texture("crop", baseTexture + "_stage0");
			this.withExistingParent(name + "_stage3", parent).texture("crop", baseTexture + "_stage1");
			this.withExistingParent(name + "_stage4", parent).texture("crop", baseTexture + "_stage1");
			this.withExistingParent(name + "_stage5", parent).texture("crop", baseTexture + "_stage2");
			this.withExistingParent(name + "_stage6", parent).texture("crop", baseTexture + "_stage2");
			this.withExistingParent(name + "_stage7", parent).texture("crop", baseTexture + "_stage3");
		}

		@Override
		public String getName() {
			return "Crop Models: " + this.modid;
		}

	}

	private static class MinecraftSeedModelProvider extends ModelProvider<ItemModelBuilder> {

		public MinecraftSeedModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator.getPackOutput(), "minecraft", "seed", ItemModelBuilder::new, existingFileHelper);
		}


		@Override
		protected void registerModels() {
			List.of("allium", "azure_bluet", "bamboo", "blue_orchid", "brown_mushroom", "cactus", "carrot",
							"cornflower", "crimson_fungus", "dandelion", "kelp", "lily_of_the_valley", "nether_wart",
							"orange_tulip", "oxeye_daisy", "pink_tulip", "poppy", "potato", "red_mushroom",
							"red_tulip", "seagrass", "sea_pickle", "sugar_cane", "sweet_berries", "warped_fungus",
							"white_tulip", "wither_rose", "unknown")
					.forEach(this::seed);
			this.withExistingParent("beetroot", "minecraft:item/beetroot_seeds");
			this.withExistingParent("melon", "minecraft:item/melon_seeds");
			this.withExistingParent("pumpkin", "minecraft:item/pumpkin_seeds");
			this.withExistingParent("wheat", "minecraft:item/wheat_seeds");
		}

		private void seed(String name) {
			this.withExistingParent(name, "minecraft:item/generated").texture("layer0", "agricraft:seed/" + name);

		}

		@Override
		public String getName() {
			return "Seed Models: " + this.modid;
		}

	}

}
