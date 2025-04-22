package com.xeelux.droplets.core.configurations;

import com.xeelux.droplets.core.common.ArgumentsParser;
import com.xeelux.droplets.core.text.JsonSerializer;
import com.xeelux.droplets.core.utilities.FileSystemUtilities;
import com.xeelux.droplets.core.utilities.StreamUtilities;
import com.xeelux.droplets.core.utilities.StringUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConfigurationProvider {

    private static final Logger logger = LogManager.getLogger(ConfigurationProvider.class);
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();
    private static Configuration configuration;

    private ConfigurationProvider() { }

    /**
     * Retrieves input stream (based on profile) to read configuration.
     * @return Input stream to read configuration.
     */
    private static InputStream getConfigurationAsStream() {
        final var profile = ArgumentsParser.getProfile();
        var externalConfigurationFilePath = ArgumentsParser.getArgument(
                "externalConfigurationFilePath", StringUtilities.getEmptyString());

        // if external configuration file path is provided...
        if (!StringUtilities.isEmpty(externalConfigurationFilePath)) {
            // we shall replace all the placeholders...
            externalConfigurationFilePath = externalConfigurationFilePath
                    .replace("{{profile}}", profile);
            // sanitizing the path because it may contain "//"....
            externalConfigurationFilePath = FileSystemUtilities.sanitizePath(externalConfigurationFilePath);

            logger.log(Level.INFO, "Loading external configuration, '" + externalConfigurationFilePath + "'.");

            // trying to create new input stream from the given configuration file path...
            final var inputStream = StreamUtilities.tryCreateInputStream(externalConfigurationFilePath);

            // if the input stream is null...
            if (inputStream == null) {
                logger.log(Level.WARN, "Failed to read external configuration from the given file path, \"" + externalConfigurationFilePath + "\".");
            } else {
                // otherwise, we shall return the input stream...
                return inputStream;
            }
        }

        final var configurationFileName = "configuration." + profile + ".json";

        logger.log(Level.INFO, "Loading configuration, '" + configurationFileName + "'.");

        final var inputStream = ConfigurationProvider.class.getClassLoader()
                .getResourceAsStream(configurationFileName);

        return inputStream;
    }

    /**
     * Overwrites certain configuration values
     * from command-line arguments.
     * @param configuration Configuration to be overwritten.
     * @return The configuration.
     */
    private static Configuration overwriteConfigurationFromArguments(Configuration configuration) {
        // gets the instance ID from command-line arguments...
        var instanceId = ArgumentsParser.getArgument("instanceId", StringUtilities.getEmptyString());
        // we shall set the instance ID...
        configuration.setInstanceId(instanceId);

        // gets the host from command-line arguments...
        var host = ArgumentsParser.getArgument("host", configuration.getHost());
        // we shall set the host...
        configuration.setHost(host);

        // gets the port from command-line arguments...
        var port = ArgumentsParser.getArgumentAsInteger("port", configuration.getPort());
        // we shall set the port...
        configuration.setPort(port);

        // gets the 'includeStackTrace' flag value from command-line arguments...
        var includeStackTrace = ArgumentsParser.getArgumentAsBoolean(
                "includeStackTrace", configuration.shallIncludeStackTrace());
        // we shall set the value of the 'includeStackTrace' flag...
        configuration.setIncludeStackTrace(includeStackTrace);

        // gets the application name from command-line arguments...
        var applicationName = ArgumentsParser.getArgument("applicationName", StringUtilities.getEmptyString());
        // we shall set the application name...
        configuration.setApplicationName(applicationName);

        // gets a unique value from command-line arguments...
        final var uniqueValue = ArgumentsParser.getArgument("uniqueValue", StringUtilities.getEmptyString());
        // we shall set the unique value...
        configuration.setUniqueValue(uniqueValue);

        // retrieves the virtual thread scheduler configuration...
        var virtualThreadSchedulerConfiguration
                = configuration.getVirtualThreadScheduler();

        // if configuration is not found...
        if (virtualThreadSchedulerConfiguration == null) {
            // we'll create a new instance...
            virtualThreadSchedulerConfiguration = new VirtualThreadSchedulerConfiguration();
            // and set it to the configuration...
            configuration.setVirtualThreadScheduler(virtualThreadSchedulerConfiguration);
        }

        // retrieves the available platform thread count from command-line arguments...
        var virtualThreadSchedulerAvailablePlatformThreadCount = ArgumentsParser.getArgumentAsInteger(
                "virtualThreadSchedulerAvailablePlatformThreadCount",
                virtualThreadSchedulerConfiguration.getAvailablePlatformThreadCount());
        // overwrites the value...
        virtualThreadSchedulerConfiguration.setAvailablePlatformThreadCount(virtualThreadSchedulerAvailablePlatformThreadCount);

        // retrieves the maximum platform thread pool size from command-line arguments...
        var maximumPlatformThreadPoolSize = ArgumentsParser.getArgumentAsInteger(
                "virtualThreadSchedulerMaximumPlatformThreadPoolSize",
                virtualThreadSchedulerConfiguration.getMaximumPlatformThreadPoolSize());
        // overwrites the value...
        virtualThreadSchedulerConfiguration.setMaximumPlatformThreadPoolSize(maximumPlatformThreadPoolSize);

        // retrieves the minimum unblocked platform thread count from command-line arguments...
        var minimumUnblockedPlatformThreadCount = ArgumentsParser.getArgumentAsInteger(
                "virtualThreadSchedulerMinimumUnblockedPlatformThreadCount",
                virtualThreadSchedulerConfiguration.getMinimumUnblockedPlatformThreadCount());
        // overwrites the value...
        virtualThreadSchedulerConfiguration.setMinimumUnblockedPlatformThreadCount(minimumUnblockedPlatformThreadCount);

        return configuration;
    }

    /**
     * Sets system properties from the configuration.
     * @param configuration Configuration from which the system properties shall be set.
     * @return The configuration.
     */
    private static Configuration setSystemProperties(Configuration configuration) {
        var properties = System.getProperties();

        // this property makes sure that the embedded apache tomcat throws
        // exceptions rather than handling them silently or just showing logs...
        properties.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

        // gets the virtual thread scheduler configuration...
        var virtualThreadSchedulerConfiguration
                = configuration.getVirtualThreadScheduler();

        // if the virtual thread scheduler configuration is not null...
        if (virtualThreadSchedulerConfiguration != null) {
            // sets virtual thread scheduler properties...
            // NOTE: IF SETTING THESE PROPERTIES PROGRAMMATICALLY DOESN'T WORK,
            // YOU MUST PASS THE FOLLOWING VM ARGUMENTS WHILE RUNNING THIS APPLICATION-
            // -Djdk.virtualThreadScheduler.parallelism=16
            // -Djdk.virtualThreadScheduler.maxPoolSize=192
            // -Djdk.virtualThreadScheduler.minRunnable=4
            properties.setProperty("jdk.virtualThreadScheduler.parallelism",
                    "" + virtualThreadSchedulerConfiguration.getAvailablePlatformThreadCount());
            properties.setProperty("jdk.virtualThreadScheduler.maxPoolSize",
                    "" + virtualThreadSchedulerConfiguration.getMaximumPlatformThreadPoolSize());
            properties.setProperty("jdk.virtualThreadScheduler.minRunnable",
                    "" + virtualThreadSchedulerConfiguration.getMinimumUnblockedPlatformThreadCount());
        }

        return configuration;
    }

    public static Configuration loadConfiguration() throws Exception {
        logger.log(Level.INFO, "Loading configuration.");

        // retrieves configuration as stream from resources...
        final var inputStream = getConfigurationAsStream();
        // reads JSON content from the input stream...
        final var content = StreamUtilities.readString(inputStream);
        // parses the JSON content as configuration...
        var configuration = JsonSerializer.deserialize(content, Configuration.class);

        // if configuration is null, we shall throw exception...
        if (configuration == null) {
            throw new Exception("Configuration could not be loaded. Please check previous logs for more details.");
        }

        // this method shall overwrite the configuration values
        // from command-line arguments...
        configuration = overwriteConfigurationFromArguments(configuration);
        // this method shall set configuration values
        // to system properties...
        configuration = setSystemProperties(configuration);

        // if configuration is loaded successfully,
        // we'll set that to our static variable...
        setConfiguration(configuration);

        logger.log(Level.INFO, "Successfully loaded configuration for \"" + configuration.getProfile() + "\" profile.");

        return ConfigurationProvider.configuration;
    }

    public static Configuration tryLoadConfiguration() {
        try {
            return loadConfiguration();
        } catch (Exception exception) {
            logger.log(Level.ERROR, "An exception occurred while loading configuration.", exception);
        }

        return null;
    }

    public static Configuration getConfiguration() {
        Configuration configuration;

        readLock.lock();        // <-- thread synchronization starts here...

        configuration = ConfigurationProvider.configuration;

        readLock.unlock();      // <-- thread synchronization ends here...

        return configuration;
    }

    private static void setConfiguration(Configuration configuration) {
        writeLock.lock();        // <-- thread synchronization starts here...

        ConfigurationProvider.configuration = configuration;

        writeLock.unlock();      // <-- thread synchronization ends here...
    }
}
