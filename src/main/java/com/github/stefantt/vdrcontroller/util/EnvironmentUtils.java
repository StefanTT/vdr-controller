package com.github.stefantt.vdrcontroller.util;

/**
 * Environment utility functions.
 *
 * @author Stefan Taferner
 */
public final class EnvironmentUtils
{
    private static final String OS = (System.getProperty("os.name")).toLowerCase();

    private EnvironmentUtils()
    {
    }

    /**
     * @return The directory for application configurations.
     */
    public static String getAppConfigDir()
    {
        if (OS.contains("linux"))
            return System.getProperty("user.home") + "/.config";
        if (OS.contains("win"))
            return System.getenv("LOCALAPPDATA");
        // Assume MacOS
        return System.getProperty("user.home") + "/Library/Application Support";
    }
}
