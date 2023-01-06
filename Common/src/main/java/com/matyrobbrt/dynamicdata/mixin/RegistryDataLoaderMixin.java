package com.matyrobbrt.dynamicdata.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.matyrobbrt.dynamicdata.Constants;
import com.matyrobbrt.dynamicdata.api.DatapackRegistryMutator;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;
import com.matyrobbrt.dynamicdata.impl.DatapackRegistryMutatorImpl;
import com.matyrobbrt.dynamicdata.impl.TagsMutatorImpl;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Inject(at = @At("TAIL"), method = "loadRegistryContents")
    private static <E> void dynamicdata$addCodeContents(RegistryOps.RegistryInfoLookup lookup, ResourceManager manager, ResourceKey<? extends Registry<E>> key, WritableRegistry<E> registry, Decoder<E> decoder, Map<ResourceKey<?>, Exception> exceptions, CallbackInfo ci) {
        try {
            final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
            final List<E> codeEntries = new ArrayList<>();
            DDAPIImpl.INSTANCE.get().fireMutation(new DatapackRegistryMutatorImpl<>(registry, lookup, (resourceLocation, element) -> {
                final E value = decoder.parse(ops, element).getOrThrow(false, ($$0x) -> {});
                Registry.register(registry, resourceLocation, value);
                codeEntries.add(value);
            }, codeEntries::add));
            Constants.LOG.debug("Added {} in-code entries to registry {}.", codeEntries.size(), key);
        } catch (Exception e) {
            exceptions.put(key, e);
        }
    }
}
