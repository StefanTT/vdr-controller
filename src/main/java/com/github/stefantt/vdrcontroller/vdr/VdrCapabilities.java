package com.github.stefantt.vdrcontroller.vdr;

import java.util.Map;

/**
 * The capabilities of the VDR server.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class VdrCapabilities
{
    private String epgsearchVersion;
    private String femonVersion;
    private String streamdevServerVersion;
    private String svdrpOsdVersion;

    /**
     * Create an empty capabilities object.
     */
    public VdrCapabilities()
    {
    }

    /**
     * Create a capabilities object and initialize it from the given map that contains plugin names
     * and version.
     *
     * @param plugins The map of plugins with plugin name as key and version as value
     */
    public VdrCapabilities(Map<String, String> plugins)
    {
        epgsearchVersion = plugins.get("epgsearch");
        femonVersion = plugins.get("femon");
        streamdevServerVersion = plugins.get("streamdev-server");
        svdrpOsdVersion = plugins.get("svdrposd");
    }

    /**
     * @return True if the epgsearch plugin is installed
     */
    public boolean hasEpgsearch()
    {
        return epgsearchVersion != null;
    }

    /**
     * @return The version of the epgsearch plugin, null if not installed
     */
    public String getEpgsearchVersion()
    {
        return epgsearchVersion;
    }

    public void setEpgsearchVersion(String epgsearchVersion)
    {
        this.epgsearchVersion = epgsearchVersion;
    }

    /**
     * @return True if the femon plugin is installed
     */
    public boolean hasFemon()
    {
        return femonVersion != null;
    }

    /**
     * @return The version of the femon plugin, null if not installed
     */
    public String getFemonVersion()
    {
        return femonVersion;
    }

    public void setFemonVersion(String femonVersion)
    {
        this.femonVersion = femonVersion;
    }

    /**
     * @return True if the svdrposd plugin is installed
     */
    public boolean hasSvdrpOsd()
    {
        return svdrpOsdVersion != null;
    }

    /**
     * @return The version of the svdrposd plugin, null if not installed
     */
    public String getSvdrpOsdVersion()
    {
        return svdrpOsdVersion;
    }

    public void setSvdrpOsdVersion(String svdrpOsdPluginVersion)
    {
        this.svdrpOsdVersion = svdrpOsdPluginVersion;
    }

    /**
     * @return True if the streamdev-server plugin is installed
     */
    public boolean hasStreamdevServer()
    {
        return streamdevServerVersion != null;
    }

    /**
     * @return The version of the streamdev-server plugin, null if not installed
     */
    public String getStreamdevServerVersion()
    {
        return streamdevServerVersion;
    }

    public void setStreamdevServerVersion(String streamdevServerVersion)
    {
        this.streamdevServerVersion = streamdevServerVersion;
    }
}
