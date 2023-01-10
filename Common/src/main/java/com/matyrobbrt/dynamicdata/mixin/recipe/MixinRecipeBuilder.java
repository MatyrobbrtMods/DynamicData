package com.matyrobbrt.dynamicdata.mixin.recipe;

import com.matyrobbrt.dynamicdata.extensions.ExtendedRecipeBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin({ShapedRecipeBuilder.class, ShapelessRecipeBuilder.class, SimpleCookingRecipeBuilder.class, SingleItemRecipeBuilder.class, UpgradeRecipeBuilder.class})
public abstract class MixinRecipeBuilder implements ExtendedRecipeBuilder {
    @Unique
    private boolean dynamicdata$runtimeGenerated = false;

    @Unique
    @Override
    public void dynamicdata$markAsRuntimeGenerated() {
        dynamicdata$runtimeGenerated = true;
    }

    @Override
    public boolean dynamicdata$isRuntimeGenerated() {
        return dynamicdata$runtimeGenerated;
    }
}

@Mixin(ShapedRecipeBuilder.class)
abstract class ShapedRecipe {
    @Redirect(
            method = "ensureValid(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")
    )
    private boolean dynamicdata$noAdvancements(Map<ResourceLocation, Criterion> self) {
        return !((ExtendedRecipeBuilder) this).dynamicdata$isRuntimeGenerated() && self.isEmpty();
    }
}
@Mixin(ShapelessRecipeBuilder.class)
abstract class ShapelessRecipe {
    @Redirect(
            method = "ensureValid(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")
    )
    private boolean dynamicdata$noAdvancements(Map<ResourceLocation, Criterion> self) {
        return !((ExtendedRecipeBuilder) this).dynamicdata$isRuntimeGenerated() && self.isEmpty();
    }
}
@Mixin(SimpleCookingRecipeBuilder.class)
abstract class Cooking {
    @Redirect(
            method = "ensureValid(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")
    )
    private boolean dynamicdata$noAdvancements(Map<ResourceLocation, Criterion> self) {
        return !((ExtendedRecipeBuilder) this).dynamicdata$isRuntimeGenerated() && self.isEmpty();
    }
}
@Mixin(SingleItemRecipeBuilder.class)
abstract class SingleItem {
    @Redirect(
            method = "ensureValid(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")
    )
    private boolean dynamicdata$noAdvancements(Map<ResourceLocation, Criterion> self) {
        return !((ExtendedRecipeBuilder) this).dynamicdata$isRuntimeGenerated() && self.isEmpty();
    }
}
@Mixin(UpgradeRecipeBuilder.class)
abstract class UpgradeRecipe {
    @Redirect(
            method = "ensureValid(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z")
    )
    private boolean dynamicdata$noAdvancements(Map<ResourceLocation, Criterion> self) {
        return !((ExtendedRecipeBuilder) this).dynamicdata$isRuntimeGenerated() && self.isEmpty();
    }
}