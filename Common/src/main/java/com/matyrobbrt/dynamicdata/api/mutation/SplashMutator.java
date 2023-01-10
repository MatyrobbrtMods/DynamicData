package com.matyrobbrt.dynamicdata.api.mutation;

import java.util.List;

/**
 * A <strong>client-only</strong> mutator for main menu splashes.
 *
 * @see net.minecraft.client.resources.SplashManager
 */
public interface SplashMutator extends DataMutator {
    /**
     * Adds a splash text.
     *
     * @param splash the text of the splash
     */
    void addSplash(String splash);

    /**
     * Removes a splash text
     *
     * @param splash the text of the splash to remove
     */
    void removeSlash(String splash);

    /**
     * {@return all currently known splashes}
     */
    List<String> getSplashes();

    /**
     * Clears all currently known splashes.
     */
    void clear();
}
