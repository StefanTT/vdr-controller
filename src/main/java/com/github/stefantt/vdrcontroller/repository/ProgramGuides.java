package com.github.stefantt.vdrcontroller.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.hampelratte.svdrp.parsers.EPGParser;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;

/**
 * Repository for EPG entries. If the EPG data file is set then it is used, otherwise
 * VDR is queried for the EPG entries.
 *
 * @author Stefan Taferner
 */
public class ProgramGuides
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramGuides.class);

    private Map<String, List<EPGEntry>> epgData = new HashMap<>(200);
    private final VdrConnection vdr;
    private File epgDataFile;
    private long epgDataFileLastModified = 0;
    private long lastUpdated = 0;

    // The maximum age of the EPG data before they are re-read from VDR, in msec
    private static final int DATA_MAX_AGE_MSEC = 15 * 60000;

    /**
     * Create a repository that holds the EPG entries.
     *
     * @param vdr The VDR connection to use
     */
    public ProgramGuides(VdrConnection vdr)
    {
        this.vdr = vdr;
    }

    /**
     * Get the EPG data of a channel.
     *
     * @param channelId The ID of the channel
     * @return The EPG data of the channel, null if the channel has no EPG data
     */
    public List<EPGEntry> getEntries(String channelId)
    {
        ensureUpdated();
        return epgData.get(channelId);
    }

    /**
     * @return The time of the last update of the EPG data, in milliseconds.
     */
    public long getLastUpdated()
    {
        return lastUpdated;
    }

    /**
     * Clear cached data.
     */
    public void clearCache()
    {
        lastUpdated = 0;
    }

    /**
     * Ensure that the EPG data is up to date.
     */
    private void ensureUpdated()
    {
        if (System.currentTimeMillis() - DATA_MAX_AGE_MSEC > lastUpdated)
            update();
    }

    /**
     * Update the EPG data.
     */
    private synchronized void update()
    {
        if (epgDataFile != null && epgDataFile.exists())
            updateFromFile();
        else updateFromVdr();

        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * Add the EPG entry to the EPG data map.
     *
     * @param map The EPG data map
     * @param entry The EPG entry to add
     */
    private void addEntry(Map<String, List<EPGEntry>> map, EPGEntry entry)
    {
        String channelId = entry.getChannelID();

        List<EPGEntry> channelEntries = map.get(channelId);
        if (channelEntries == null)
        {
            channelEntries = new ArrayList<>(128);
            map.put(channelId, channelEntries);
        }

        channelEntries.add(entry);
    }

    /**
     * Update the EPG data by reading the epg.data file.
     */
    private void updateFromFile()
    {
        LOGGER.debug("Updating EPG data from {}", epgDataFile);

        if (epgDataFile.lastModified() == epgDataFileLastModified)
        {
            LOGGER.debug("EPG data file has not changed");
            return;
        }

        EPGLineParser parser = new EPGLineParser();
        List<EPGEntry> entries = new ArrayList<>();
        Map<String, List<EPGEntry>> newEpgData = new HashMap<>(256);

        InputStream in = null;
        BufferedReader bufferedReader = null;
        Reader reader = null;

        try
        {
            in = new FileInputStream(epgDataFile);
            reader = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(reader);

            while (true)
            {
                String line = bufferedReader.readLine();
                if (line == null) break;

                parser.parseLine(line, entries);
                if (!entries.isEmpty())
                {
                    addEntry(newEpgData, entries.get(0));
                    entries.clear();
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read " + epgDataFile, e);
        }
        finally
        {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Update the EPG data from VDR.
     */
    private void updateFromVdr()
    {
        LOGGER.debug("Updating EPG data from VDR");

        // TODO implement
    }

    /**
     * Set the location of VDR's epg.data file.
     *
     * @param epgDataFile The epg.data file
     */
    public void setEpgDataFile(File epgDataFile)
    {
        if (!Objects.equals(this.epgDataFile, epgDataFile))
        {
            this.epgDataFile = epgDataFile;
            this.epgDataFileLastModified = 0;
            this.lastUpdated = 0;
        }
    }

    private static class EPGLineParser extends EPGParser
    {
        @Override
        public void parseLine(String line, List<EPGEntry> list)
        {
            super.parseLine(line, list);
        }
    }
}
