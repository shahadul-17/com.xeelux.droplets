package com.xeelux.droplets.test;

import com.xeelux.droplets.core.Application;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TestApplication implements Application {

    private final Logger logger = LogManager.getLogger(TestApplication.class);

    private TestApplication() { }

    @Override
    public void initialize() throws Exception {
        logger.log(Level.INFO, "Initializing Test application.");
        logger.log(Level.INFO, "Test application initialization successful.");
    }

    @Override
    public void execute() throws Exception {
        logger.log(Level.INFO, "Executing Test application.");
        logger.log(Level.INFO, "The quick brown fox jumps over the lazy dog.");
        logger.log(Level.INFO, "Test application execution completed.");
    }
}
