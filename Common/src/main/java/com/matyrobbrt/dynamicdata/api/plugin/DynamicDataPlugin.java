package com.matyrobbrt.dynamicdata.api.plugin;

import com.matyrobbrt.dynamicdata.api.recipe.RecipeMutator;

public interface DynamicDataPlugin {
    void mutateRecipes(RecipeMutator mutator);
}
