package net.dingletherat.deltarinth.neoforge;

import net.neoforged.fml.common.Mod;

import net.dingletherat.deltarinth.Delta;

@Mod(Delta.MOD_ID)
public final class DeltaNeoForge {
    public DeltaNeoForge() {
        // Run our common setup.
        Delta.init();
    }
}
