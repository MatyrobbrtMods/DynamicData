package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.AnyHolderSet;

import java.util.List;

@RegisterPlugin
public class ForgeTestPlugin implements DynamicDataPlugin {

    @Override
    public void collectMutatorListeners(MutatorCollector collector) {
        collector.acceptDatapackRegistryMutator(ForgeRegistries.Keys.BIOME_MODIFIERS, mutator -> {
            mutator.register(new ResourceLocation("test:test_modifier"), new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                    new AnyHolderSet<>(mutator.registryLookup(Registries.BIOME).orElseThrow()),
                    List.of(new MobSpawnSettings.SpawnerData(EntityType.BEE, 10000, 10, 20))
            ));
        });
    }
}
