package com.agricraft.agricraft.common.plugin.pneumaticcraft;

import com.agricraft.agricraft.api.AgriApi;
import me.desht.pneumaticcraft.common.core.ModHarvestHandlers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PneumaticCraftPlugin {

    @SubscribeEvent
    public static void registerAgriCraftHarvestHandler(RegisterEvent event) {
        if (ModList.get().isLoaded("pneumaticcraft")) {
            event.register(ModHarvestHandlers.HARVEST_HANDLERS_DEFERRED.getRegistryKey(),
                    helper -> helper.register("agricraft", new AgriCraftHarvestHandler())
            );
        }
    }
}
