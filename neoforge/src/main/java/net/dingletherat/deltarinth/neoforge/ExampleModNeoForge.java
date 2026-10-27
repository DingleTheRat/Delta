package net.dingletherat.deltarinth.neoforge;

import net.neoforged.fml.common.Mod;

import net.dingletherat.deltarinth.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
