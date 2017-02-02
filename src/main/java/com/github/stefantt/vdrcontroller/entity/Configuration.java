package com.github.stefantt.vdrcontroller.entity;

/**
 * The global application configuration.
 *
 * @author Stefan Taferner
 */
public class Configuration
{
    private String channelsFile;
    private String epgDataFile;
    private String vdrHost;
    private int vdrPort;

    /**
     * Create a configuration.
     */
    public Configuration()
    {
        clear();
    }

    /**
     * Clear the configuration.
     */
    public void clear()
    {
        channelsFile = "/var/lib/vdr/channels.conf";
        epgDataFile = "/var/cache/vdr/epg.data";
        vdrHost = "localhost";
        vdrPort = 6419;
    }

    public String getChannelsFile()
    {
        return channelsFile;
    }

    public String getEpgDataFile()
    {
        return epgDataFile;
    }

    public String getVdrHost()
    {
        return vdrHost;
    }

    public int getVdrPort()
    {
        return vdrPort;
    }

    public void setChannelsFile(String channelsFile)
    {
        this.channelsFile = channelsFile;
    }

    public void setEpgDataFile(String epgDataFile)
    {
        this.epgDataFile = epgDataFile;
    }

    public void setVdrHost(String vdrHost)
    {
        this.vdrHost = vdrHost;
    }

    public void setVdrPort(int vdrPort)
    {
        this.vdrPort = vdrPort;
    }
}
