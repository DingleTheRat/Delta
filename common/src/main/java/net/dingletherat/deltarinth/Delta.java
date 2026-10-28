package net.dingletherat.deltarinth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Delta {
    public static final String MOD_ID = "deltarinth";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
        LOGGER.info("Hello, World!");
    }
}
