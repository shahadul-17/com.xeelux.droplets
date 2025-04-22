package com.xeelux.droplets.core;

/**
 * All application classes shall implement
 * this interface.
 */
public interface Application {

    /**
     * Performs any initialization operation (if needed).
     * @throws Exception
     */
    default void initialize() throws Exception { }

    /**
     * Performs any reset operation (if needed).
     * E.g., Resetting the entire database schema to the default state.
     * @throws Exception
     */
    default void reset() throws Exception { }

    /**
     * Executes service to perform necessary actions.
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * Releases resources.
     */
    default void dispose() { }
}
