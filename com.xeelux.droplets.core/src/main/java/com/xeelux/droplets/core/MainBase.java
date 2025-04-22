package com.xeelux.droplets.core;

import com.xeelux.droplets.core.common.ArgumentsParser;
import com.xeelux.droplets.core.configurations.ConfigurationProvider;
import com.xeelux.droplets.core.dependencyinjection.ServiceProvider;
import com.xeelux.droplets.core.threading.AsyncTask;
import com.xeelux.droplets.core.utilities.FileSystemUtilities;
import com.xeelux.droplets.core.utilities.StringUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class MainBase {

    private static final String DEFAULT_LOGS_DIRECTORY_PATH = "application-data/{{applicationName}}/{{instanceId}}/logs";

    protected String getDefaultLogsDirectoryPath() {
        return DEFAULT_LOGS_DIRECTORY_PATH;
    }

    protected String getLogsDirectoryPath() {
        // getting the logs directory path from command-line arguments...
        var logsDirectoryPath = ArgumentsParser.getArgument(
                "logsDirectoryPath", StringUtilities.getEmptyString());

        // if logs directory path is not found in the command-line arguments...
        if (StringUtilities.isEmpty(logsDirectoryPath)) {
            // we shall assign the default logs directory path...
            logsDirectoryPath = getDefaultLogsDirectoryPath();
        }

        // we'll retrieve the instance ID from command-line arguments...
        final var instanceId = ArgumentsParser.getArgument(
                "instanceId", StringUtilities.getEmptyString());
        // we'll also get the application name from command-line arguments...
        final var applicationName = ArgumentsParser.getArgument(
                "applicationName", StringUtilities.getEmptyString());

        // replacing all the placeholders...
        logsDirectoryPath = logsDirectoryPath
                .replace("{{applicationName}}", applicationName)
                .replace("{{instanceId}}", instanceId);
        // sanitizing the path because it may contain "//"....
        logsDirectoryPath = FileSystemUtilities.sanitizePath(logsDirectoryPath);

        // finally, we shall return the logs directory path...
        return logsDirectoryPath;
    }

    protected <Type extends Application> void run(final String[] args, final Class<Type> applicationClass) {
        // populates the arguments...
        ArgumentsParser.populateArguments(args);

        // getting the logs directory path...
        final var logsDirectoryPath = getLogsDirectoryPath();

        // setting system property to use the logs directory path by the logger...
        // NOTE 1: BEFORE SETTING THIS SYSTEM PROPERTY, LOGGER SHALL NOT BE INITIALIZED...
        // NOTE 2: WE TRIED TO PLACE LOGS DIRECTORY PATH IN CONFIGURATION. BUT IT WOULD
        // BE TOO MESSY. SO, WE FOLLOWED THIS APPROACH. WE WOULD HIGHLY APPRECIATE IF
        // YOU CAN PLACE THE LOGS DIRECTORY PATH IN CONFIGURATION (ALONG WITH THE
        // COMMAND-LINE ARGUMENTS SUPPORT).
        System.setProperty("log4j.saveDirectory", logsDirectoryPath);
        // enables log4j2 thread context map inheritance so that the child threads
        // inherit the thread context map...
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");

        // console logging is disabled if the command line argument
        // "consoleLoggingEnabled" is false or this application is
        // currently running on "production" mode/profile...
        final var consoleLoggingDisabled = !ArgumentsParser.getArgumentAsBoolean(
                "consoleLoggingEnabled", true)
                || ArgumentsParser.getProfile().equals("production");
        // placing "consoleLoggingDisabled" key to Log4j's thread context so that
        // console logging gets disabled (e.g. in production)...
        // NOTE: THIS PIECE OF CODE MUST APPEAR BEFORE INITIALIZING THE LOGGER...
        org.apache.logging.log4j.ThreadContext.put("consoleLoggingDisabled", "" + consoleLoggingDisabled);

        // file logging is disabled if the command line argument
        // "fileLoggingEnabled" is false...
        final var fileLoggingDisabled = !ArgumentsParser.getArgumentAsBoolean(
                "fileLoggingEnabled", true);
        // placing "fileLoggingDisabled" key to Log4j's thread context so that
        // file logging gets disabled...
        // NOTE: THIS PIECE OF CODE MUST APPEAR BEFORE INITIALIZING THE LOGGER...
        org.apache.logging.log4j.ThreadContext.put("fileLoggingDisabled", "" + fileLoggingDisabled);

        final var logger = LogManager.getLogger(MainBase.class);

        logger.log(Level.INFO, "Application has started up.");
        logger.log(Level.INFO, "Log files are placed in '" + logsDirectoryPath + "' directory.");

        // gets the service provider instance...
        final var serviceProvider = ServiceProvider.getSingleton();
        // getting the context...
        final var context = serviceProvider.get(Context.class);
        Application application = null;

        try {
            // loads profile specific configuration from resource (JSON file)...
            ConfigurationProvider.loadConfiguration();

            // instantiates the application...
            application = serviceProvider.get(applicationClass);
            // initializes the application...
            application.initialize();
            // resets the application...
            application.reset();
            // executes the application...
            application.execute();
        } catch (final Exception exception) {
            logger.log(Level.ERROR, "An unexpected exception occurred.", exception);
        } finally {
            if (application != null) {
                application.dispose();
            }

            // releases all the resources associated with the async task execution runtime...
            AsyncTask.dispose();

            // releases all the resources associated with the logger...
            // NOTE: THIS METHOD IS CALLED TO ENSURE THAT THE BUFFERED
            // CONTENT GETS FLUSHED TO DISK...
            LogManager.shutdown();
        }

        // getting the exit code from the context...
        final var exitCode = context.getExitCode();

        // if the exit code is zero (0), we shall not proceed any further...
        if (exitCode == 0) { return; }

        // otherwise, we shall call the system's
        // exit() method with the provided exit code...
        System.exit(exitCode);
    }
}
