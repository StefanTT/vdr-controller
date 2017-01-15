package com.github.stefantt.vdrcontroller.m3u;

import java.net.URL;

/**
 * A m3u playlist entry.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class PlaylistEntry
{
    private final URL url;
    private final String extendedInfo;

    public PlaylistEntry(URL url, String extendedInfo)
    {
        this.url = url;
        this.extendedInfo = extendedInfo;
    }

    public URL getUrl()
    {
        return url;
    }

    public String getExtendedInfo()
    {
        return extendedInfo;
    }
}
