package com.github.stefantt.vdrcontroller.vdr.parser;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the list of plugins.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class PluginListParser
{
    private static final Pattern PLUGIN_LINE_PATTERN = Pattern.compile("^([^\\s]+)\\s+v([^\\s]+)\\s+-\\s+.*$");

    /**
     * Parse the plugin list from the given VDR response text as it is returned from the SVDRP
     * command "PLUG".
     *
     * @param text The text to parse
     * @return A map with plugin name as key and version as value
     */
    public static Map<String, String> parse(String text)
    {
        Map<String, String> plugins = new TreeMap<>();

        for (String line: text.split("\n"))
        {
            Matcher matcher = PLUGIN_LINE_PATTERN.matcher(line);
            if (matcher.find())
            {
                plugins.put(matcher.group(1), matcher.group(2));
            }
        }

        return plugins;
    }
}
