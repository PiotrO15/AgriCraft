package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CropBlockEntity extends BlockEntity implements AgriCrop, MagnifyingInspectable {

	private final Map<Integer, VoxelShape> shapeByAge = new HashMap<>();
	private AgriGenome genome;
	private String plantId = "";
	private AgriPlant plant;
	private AgriGrowthStage growthStage = null;

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(ModBlockEntityTypes.CROP.get(), blockPos, blockState);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		boolean hasPlant = tag.getBoolean("hasPlant");
		if (hasPlant) {
			this.genome = AgriGenome.fromNBT(tag);
			if (this.genome == null) {
				this.plantId = "agricraft:unknown";
			} else {
				this.plantId = this.genome.getSpeciesGene().getDominant().trait();
			}
			int growthIndex = tag.getInt("growthIndex");
			int growthTotal = tag.getInt("growthTotal");
			this.growthStage = new AgriGrowthStage(growthIndex, growthTotal);
			if (plant == null && level != null) {
				this.plant = AgriApi.getPlant(this.plantId, this.level.registryAccess()).orElse(null);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (this.hasPlant()) {
			tag.putBoolean("hasPlant", true);
			genome.writeToNBT(tag);
			tag.putInt("growthIndex", this.growthStage.index());
			tag.putInt("growthTotal", this.growthStage.total());
		} else {
			tag.putBoolean("hasPlant", false);
		}
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		// when the block id deserialized the level is null so we can't load the plant from the registry yet
		// thus, we're doing it now, as soon as the level is present
		if (level != null) {
			if (!level.isClientSide) {
				this.plant = AgriApi.getPlant(this.plantId, level.registryAccess()).orElse(null);
				if (this.plant != null && this.growthStage == null) {
					this.growthStage = this.plant.getInitialGrowthStage();
				}
				level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
			}
		}
	}

	@Override
	public boolean removeGenome() {
		if (this.hasPlant()) {
			this.getPlant().onRemoved(this);
			this.genome = null;
			this.plant = null;
			this.plantId = "";
			this.growthStage = null;

			if (this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.PLANT_STICKS) {
				this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CropBlock.CROP_STATE, CropState.SINGLE_STICKS));
			} else {
				if (this.getBlockState().getValue(BlockStateProperties.WATERLOGGED)) {
					level.setBlockAndUpdate(this.getBlockPos(), Fluids.WATER.defaultFluidState().createLegacyBlock());
				} else {
					level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public AgriGenome getGenome() {
		return genome;
	}

	@Override
	public void plantGenome(AgriGenome genome, @Nullable LivingEntity entity) {
		if (genome == null) {
			return;
		}
		this.genome = genome;
		this.plantId = genome.getSpeciesGene().getDominant().trait();
		this.plant = AgriApi.getPlant(this.plantId, this.level.registryAccess()).orElse(null);
		if (this.plant != null) {
			this.growthStage = this.plant.getInitialGrowthStage();
		}
		level.setBlock(this.getBlockPos(), this.hasCropSticks() ?
				this.getBlockState().setValue(CropBlock.CROP_STATE, CropState.PLANT_STICKS).setValue(CropBlock.LIGHT, this.plant.getBrightness(this))
				: this.getBlockState().setValue(CropBlock.LIGHT, this.plant.getBrightness(this)), 3);
		this.plant.onPlanted(this, null);
	}

	@Override
	public boolean hasPlant() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE).hasPlant() && !this.plantId.isEmpty() && this.plant != null;
	}

	@Override
	public boolean hasCropSticks() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE).hasSticks();
	}

	@Override
	public boolean isCrossCropSticks() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.DOUBLE_STICKS;
	}

	public String getPlantId() {
		return this.plantId;
	}

	public AgriPlant getPlant() {
		return this.plant;
	}

	public AgriGrowthStage getGrowthStage() {
		return this.growthStage;
	}

	@Override
	public void setGrowthStage(AgriGrowthStage stage) {
		this.growthStage = stage;
		this.setChanged();
		this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState().setValue(CropBlock.LIGHT, this.plant.getBrightness(this)), Block.UPDATE_ALL);
	}

	@Override
	public AgriGrowthResponse getFertilityResponse() {
		// TODO: should we cache the fertility response ?
		// check growth space
		int height = this.plant.getPlantHeight(this.growthStage);
		while (height > 16) {
			if (!level.getBlockState(this.getBlockPos().above(height / 16)).isAir()) {
				return AgriGrowthResponse.INFERTILE;
			}
			height -= 16;
		}
		// if there is no condition registered
		if (AgriGrowthConditionRegistry.getInstance().isEmpty()) {
			return AgriGrowthResponse.FERTILE;
		}
		// check every growth condition registered and take the one with the highest priority
		int strength = this.genome.getStatGene(AgriStatRegistry.getInstance().strengthStat()).getTrait();
		Optional<AgriGrowthResponse> optional = AgriGrowthConditionRegistry.getInstance().stream()
				.map(condition -> condition.check(this, this.level, this.getBlockPos(), strength))
				.reduce((result, element) -> result.priority() >= element.priority() ? result : element);
		return optional.orElse(AgriGrowthResponse.FERTILE);
	}

	@Override
	public Optional<AgriSoil> getSoil() {
		return AgriApi.getSoil(this.level, getBlockPos().below(), this.level.registryAccess());
	}

	/**
	 * @return the block shape of the crop
	 */
	public VoxelShape getShape() {
		if (this.plant == null) {
			return Shapes.empty();
		}
		double yoffset = 0;
		if (level.getBlockState(this.getBlockPos().below()).is(Blocks.FARMLAND)) {
			// TODO: should we change this to allow other soil to offsets the plant shape ?
			yoffset = -1.0D / 16.0D;
		}
		return shapeByAge.computeIfAbsent(this.growthStage.index(), stage -> Block.box(0, 0, 0, 16, this.plant.getPlantHeight(this.growthStage), 16)).move(0, yoffset, 0);
	}

	/**
	 * Compute the harvest products for the crop
	 *
	 * @param addToHarvest consumer to add the products to
	 */
	public void getHarvestProducts(Consumer<ItemStack> addToHarvest) {
		if (!this.hasPlant() || !this.isFullyGrown()) {
			return;
		}
		for (int trials = (this.genome.getGain() + 3) / 3; trials > 0; --trials) {
			this.plant.getHarvestProducts(addToHarvest, this.growthStage, this.genome, this.level.random);
		}
	}

	@Override
	public void getClippingProducts(Consumer<ItemStack> addToClipping, ItemStack clipper) {
		if (!this.hasPlant() || !this.isFullyGrown()) {
			return;
		}
		this.plant.getClipProducts(addToClipping, clipper, this.growthStage, this.genome, this.level.random);
	}

	@Override
	public boolean acceptsFertilizer(AgriFertilizer fertilizer) {
		if (this.isCrossCropSticks()) {
			return CoreConfig.allowFertilizerMutation && fertilizer.canTriggerMutation();
		} else if (this.hasPlant()) {
			return !this.isFullyGrown() && fertilizer.canFertilize(this);
		} else {
			return fertilizer.canTriggerWeeds();
		}
	}

	@Override
	public void onApplyFertilizer(AgriFertilizer fertilizer, RandomSource rand) {

	}

	/**
	 * Apply a growth tick to the crop (plant+weed)
	 */
	@Override
	public void applyGrowthTick() {
		if (this.level == null || this.level.isClientSide()) {
			return;
		}
		if (this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.DOUBLE_STICKS) {
			// mutation tick
			AgriApi.getMutationHandler().getActiveCrossBreedEngine().handleCrossBreedTick(this, this.streamNeighbours(), this.level.random);
		} else {
			if (!this.hasPlant()) {
				return;
			}
			AgriGrowthResponse fertility = this.getFertilityResponse();
			if (fertility.isInstantKill()) {
				// kill plant
				fertility.onPlantKilled(this);
				this.removeGenome();
			} else if (fertility.isLethal()) {
				// reverse growth stage
				this.revertGrowthStage();
			} else if (fertility.isFertile()) {
				// plant growth tick
				this.executePlantGrowthTick();
			}
		}
	}

	public Stream<AgriCrop> streamNeighbours() {
		return Direction.Plane.HORIZONTAL.stream()
				.map(dir -> this.getBlockPos().relative(dir))
				.map(pos -> AgriApi.getCrop(this.level, pos))
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	protected void revertGrowthStage() {
		AgriGrowthStage current = this.growthStage;
		AgriGrowthStage prev = this.growthStage.getPrevious(this, this.level.random);
		if (current.equals(prev)) {
			this.removeGenome();
		} else {
			this.setGrowthStage(prev);
		}
	}

	/**
	 * Apply a growth tick to the plant
	 */
	protected void executePlantGrowthTick() {
		if (this.isFullyGrown()) {
			return;
		}
		double a = this.calculateGrowthRate();
		double b = this.level.random.nextDouble();
		if (a > b) {
			this.setGrowthStage(this.growthStage.getNext(this, this.level.random));
		}
	}

	protected double calculateGrowthRate() {
		int growth = this.genome.getStatGene(AgriStatRegistry.getInstance().growthStat()).getTrait();
		double soilFactor = this.getSoil().map(AgriSoil::growthModifier).orElse(1.0D);
		return soilFactor * (this.plant.getGrowthChance(this.growthStage) + growth * this.plant.getBonusGrowthChance(this.growthStage) * CoreConfig.growthMultiplier);
	}

	@Override
	public void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (!this.hasPlant()) {
			tooltip.add(Component.translatable("agricraft.tooltip.magnifying.no_plant"));
			return;
		}
		tooltip.add(Component.translatable("agricraft.tooltip.magnifying.crop"));
		tooltip.add(Component.literal("  ").plainCopy().append(Component.translatable("agricraft.tooltip.magnifying.species"))
				.append(LangUtils.plantName(genome.getSpeciesGene().getDominant().trait()))
				.append(Component.literal(" - ").plainCopy())
				.append(LangUtils.plantName(genome.getSpeciesGene().getRecessive().trait()))
		);
		AgriStatRegistry.getInstance().stream()
				.filter(stat -> !stat.isHidden())
				.map(stat -> this.genome.getStatGene(stat))
				.sorted(Comparator.comparing(p -> p.getGene().getId()))
				.map(genePair -> Component.translatable("agricraft.tooltip.magnifying.stat." + genePair.getGene().getId(),
						genePair.getDominant().trait(), genePair.getRecessive().trait()))
				.map(component -> Component.literal("  ").append(component))
				.forEach(tooltip::add);
		if (isPlayerSneaking) {
			tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", this.growthStage.index() + 1, this.growthStage.total())));
		}
		AgriGrowthResponse response = this.getFertilityResponse();
		tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.requirement." + (response.isLethal() ? "lethal" : response.isFertile() ? "fertile" : "not_fertile"))));
	}

}
