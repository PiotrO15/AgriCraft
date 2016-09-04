package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.misc.ISoilContainer;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

/**
 * Holds all the default soils and soil. Also holds all GrowthRequirements.
 */
public class GrowthRequirementHandler {

    public static IGrowthReqBuilder getNewBuilder() {
        return new GrowthReqBuilder();
    }

    /**
     * This list contains soils which pose as a default soil, meaning any
     * CropPlant which doesn't require a specific soil will be able to grown on
     * these This list can be modified with MineTweaker
     */
    public static List<FuzzyStack> defaultSoils = new ArrayList<>();

    /**
     * This list contains soils needed for certain CropPlants This list cannot
     * be modified externally
     */
    static List<FuzzyStack> soils = new ArrayList<>();

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(IBlockAccess world, BlockPos pos) {
        FuzzyStack soil = new FuzzyStack(world.getBlockState(pos));
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void init() {
        registerSoils();
        registerCustomEntries();
    }

    private static void registerSoils() {
        addDefaultSoil(new FuzzyStack(new ItemStack(Blocks.FARMLAND)));
    }

    private static void registerCustomEntries() {
        //reads custom entries
        AgriCore.getLogger("AgriCraft").info("Registering soils to whitelist:");
        // TODO Decide if to replace!
        String[] data = new String[]{"minecraft:dirt"};
        String total = " of " + data.length + ".";
        for (String line : data) {
            AgriCore.getLogger("AgriCraft").debug("  Parsing " + line + total);
            ItemStack stack = ((FuzzyStack) AgriCore.getConverter().toStack(line)).toStack();
            Block block = (stack != null && stack.getItem() instanceof ItemBlock) ? ((ItemBlock) stack.getItem()).block : null;

            if (block != null) {
                addDefaultSoil(new FuzzyStack(new ItemStack(block, stack.getItemDamage())));
            } else {
                AgriCore.getLogger("AgriCraft").info(" Error when adding block to soil whitelist: Invalid block (line: " + line + ")");
            }
        }
        soils.removeIf(TypeHelper::isNull);
        AgriCore.getLogger("AgriCraft").info("Completed soil whitelist:");
        for (FuzzyStack soil : soils) {
            AgriCore.getLogger("AgriCraft").info(" - " + Item.REGISTRY.getNameForObject(soil.getItem()) + ":" + soil.getMeta());
        }
    }

    public static void addAllToSoilWhitelist(Collection<? extends FuzzyStack> list) {
        for (FuzzyStack block : list) {
            addDefaultSoil(block);
        }
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends FuzzyStack> list) {
        defaultSoils.removeAll(list);
    }

    public static void addSoil(FuzzyStack block) {
        if (!soils.contains(block)) {
            soils.add(block);
        }
    }

    public static boolean addDefaultSoil(FuzzyStack block) {
        if (!defaultSoils.contains(block)) {
            defaultSoils.add(block);
            return true;
        }
        return false;
    }

}
