package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.File;

public final class FileSystemUtilities {

    // NOTE: WE CAN USE THIS DIRECTORY SEPARATOR IN BOTH WINDOWS
    // AND UNIX (LINUX, MAC OS etc.) BASED SYSTEMS...
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String DIRECTORY_SEPARATOR_SANITIZATION_REGULAR_EXPRESSION = "/+";
    private static final String[] PLATFORM_DEPENDENT_DIRECTORY_SEPARATORS = new String[] {
        "\\"
    };

    public static String getDirectorySeparator() {
        return DIRECTORY_SEPARATOR;
    }

    public static String sanitizePath(final String path) {
        var sanitizedPath = path;
        final var directorySeparator = getDirectorySeparator();

        // replaces all the platform dependent directory separators with the common directory separator...
        for (var i = 0; i < PLATFORM_DEPENDENT_DIRECTORY_SEPARATORS.length; ++i) {
            final var platformDependentDirectorySeparator = PLATFORM_DEPENDENT_DIRECTORY_SEPARATORS[i];

            sanitizedPath = sanitizedPath.replace(
                    platformDependentDirectorySeparator,
                    directorySeparator);
        }

        // replaces multiple occurrences of directory separators
        // with a single common directory separator...
        sanitizedPath = sanitizedPath.replaceAll(
                DIRECTORY_SEPARATOR_SANITIZATION_REGULAR_EXPRESSION,
                directorySeparator);

        return sanitizedPath;
    }

    public static boolean exists(String path) {
        return exists(path, false);
    }

    public static boolean exists(String path, boolean directory) {
        // sanitizes the path...
        path = StringUtilities.getDefaultIfNullOrWhiteSpace(
                path, StringUtilities.getEmptyString(), true);

        // if the path is empty, we shall return false...
        if (StringUtilities.isEmpty(path)) { return false; }

        // instantiating file instance from path...
        final var file = new File(path);
        var exists = false;

        try {
            // checks if exists...
            exists = file.exists()
                    && ((directory && file.isDirectory())       // <-- if directory flag is true, checks if the given path belongs to a directory...
                    || (!directory && file.isFile()));          // <-- otherwise, checks if the given path belongs to a file...
        } catch (Exception exception) {
            final var logger = LogManager.getLogger(FileSystemUtilities.class);

            logger.log(Level.WARN, "An exception occurred while checking if '" + path + "' exists.", exception);
        }

        // returns true if the path exists...
        return exists;
    }

    public static String extractDirectoryPath(String filePath) {
        // instantiating file instance from file name...
        final var file = new File(filePath);
        // retrieving and sanitizing the path of the directory
        // that shall contain the file...
        final var directoryPath = StringUtilities.getDefaultIfNullOrWhiteSpace(
                file.getParent(), StringUtilities.getEmptyString(), true);

        // returning the directory path...
        return directoryPath;
    }

    public static String getAbsolutePath(String path) {
        return new File(path).getAbsolutePath();
    }

    public static boolean createDirectoryIfDoesNotExist(String directoryPath) {
        // sanitizes the directory path...
        directoryPath = StringUtilities.getDefaultIfNullOrWhiteSpace(
                directoryPath, StringUtilities.getEmptyString(), true);

        // if the directory path is empty, we shall return false...
        if (StringUtilities.isEmpty(directoryPath)) { return false; }

        // instantiating directory instance from directory name...
        final var directory = new File(directoryPath);
        var directoryCreated = false;

        try {
            // creates directory along with subdirectories...
            // NOTE: RETURNS FALSE IF DIRECTORY ALREADY EXISTS AND DOES NOT OVERWRITE...
            directory.mkdirs();

            // setting 'directoryCreated' flag to true...
            directoryCreated = true;
        } catch (Exception exception) {
            final var logger = LogManager.getLogger(FileSystemUtilities.class);

            logger.log(Level.WARN, "An exception occurred while creating directory, '" + directoryPath + "'.", exception);
        }

        // returns if the directory is created...
        return directoryCreated;
    }
}
