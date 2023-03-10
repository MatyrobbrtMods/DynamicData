package com.matyrobbrt.dynamicdata.mixin.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccess {
    @Accessor("recipes")
    void setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes);

    @Accessor("byName")
    Map<ResourceLocation, Recipe<?>> getByName();

    @Accessor("byName")
    void setByName(Map<ResourceLocation, Recipe<?>> byName);
}
