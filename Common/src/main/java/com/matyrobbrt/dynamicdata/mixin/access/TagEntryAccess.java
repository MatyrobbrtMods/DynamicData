package com.matyrobbrt.dynamicdata.mixin.access;

import com.matyrobbrt.dynamicdata.extensions.ExtendedTagEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryAccess extends ExtendedTagEntry {
    @Accessor @Override
    ResourceLocation getId();

    @Accessor @Override
    boolean getTag();
}
