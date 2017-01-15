package com.github.stefantt.vdrcontroller.entity;

/**
 * The global application configuration.
 *
 * @author Stefan Taferner
 */
public class Configuration
{
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
        epgDataFile = "/var/cache/vdr/epg.data";
        vdrHost = "localhost";
        vdrPort = 6419;
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
