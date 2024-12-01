package com.agricraft.agricraft.common.plugin.pneumaticcraft;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import me.desht.pneumaticcraft.api.drone.IDrone;
import me.desht.pneumaticcraft.api.harvesting.HarvestHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AgriCraftHarvestHandler extends HarvestHandler {
    public AgriCraftHarvestHandler() {
    }

    @Override
    public boolean canHarvest(Level level, BlockGetter chunkCache, BlockPos pos, BlockState state, IDrone drone) {
        return AgriApi.getCrop(level, pos).map(AgriCrop::canBeHarvested).orElse(false);
    }

    @Override
    public void harvest(Level level, BlockGetter chunkCache, BlockPos pos, BlockState state, IDrone drone) {
        AgriApi.getCrop(level, pos).ifPresent(crop -> crop.harvest(drop -> this.spawnEntity(level, pos, drop), null));
    }

    private void spawnEntity(Level world, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5 + 0.25 * world.getRandom().nextDouble();
        double y = pos.getY() + 0.5 + 0.25 * world.getRandom().nextDouble();
        double z = pos.getZ() + 0.5 + 0.25 * world.getRandom().nextDouble();
        world.addFreshEntity(new ItemEntity(world, x, y, z, stack));
    }
}
