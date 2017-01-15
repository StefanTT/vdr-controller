package com.github.stefantt.vdrcontroller.m3u;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * A parser for m3u playlists.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class PlaylistParser
{
    /**
     * Parse a m3u playlist from the input stream.
     *
     * @param in The input stream to read
     * @return The read playlist
     */
    public List<PlaylistEntry> parse(InputStream in)
    {
        List<PlaylistEntry> list = new ArrayList<>();
        Reader reader = null;

        try
        {
            reader = new InputStreamReader(in);
            reader = new BufferedReader(reader);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }

        return list;
    }
}
