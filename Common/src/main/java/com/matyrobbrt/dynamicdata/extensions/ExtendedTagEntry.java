package com.matyrobbrt.dynamicdata.extensions;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface ExtendedTagEntry {
    ResourceLocation getId();

    boolean getTag();

    default boolean dynamicdata$matches(boolean tag, ResourceLocation id) {
        return getTag() == tag && id.equals(getId());
    }
}
