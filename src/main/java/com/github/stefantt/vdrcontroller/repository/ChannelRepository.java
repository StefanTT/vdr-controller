package com.github.stefantt.vdrcontroller.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTC;
import org.hampelratte.svdrp.parsers.ChannelParser;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.DVBChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;

/**
 * Repository for VDR channels.
 *
 * @author Stefan Taferner
 */
public class ChannelRepository extends AbstractCachingRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelRepository.class);

    private Map<String, Channel> channels;
    private final VdrConnection vdr;
    private long channelsFileLastModified = 0;
    private File channelsFile;

    /**
     * Create a repository that holds the EPG entries.
     *
     * @param vdr The VDR connection to use
     */
    public ChannelRepository(VdrConnection vdr)
    {
        super();
        this.vdr = vdr;
    }

    /**
     * @return All channels
     */
    public Collection<Channel> getAll()
    {
        ensureUpdated();
        return channels.values();
    }

    /**
     * Set the location of VDR's channels.conf file.
     *
     * @param epgDataFile The channels.conf file
     */
    public void setChannelsFile(File channelsFile)
    {
        if (!Objects.equals(this.channelsFile, channelsFile))
        {
            this.channelsFile = channelsFile;
            clearCache();
        }
    }

    @Override
    public void clearCache()
    {
        super.clearCache();
        channelsFileLastModified = 0;
    }

    @Override
    protected void update()
    {
        if (channelsFile != null && channelsFile.exists())
        {
            long mtime = channelsFile.lastModified();
            if (mtime > channelsFileLastModified)
            {
                updateFromFile();
            }
            channelsFileLastModified = mtime;
        }
        else updateFromVdr();
    }

    /**
     * Update the channels from the channels file.
     */
    protected void updateFromFile()
    {
        LOGGER.debug("Updating channels from {}", channelsFile);

        InputStream in = null;
        try
        {
            in = new FileInputStream(channelsFile);
            parseChannels(IOUtils.toString(in, "UTF_8"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read " + channelsFile, e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Update the channels by querying VDR.
     */
    protected void updateFromVdr()
    {
        LOGGER.debug("Updating channels from VDR");

        LOGGER.debug("Updating channels");
        Response res = vdr.query(new LSTC());
        parseChannels(res.getMessage());
    }

    /**
     * Parse the channels string and populate the repositories' channels.
     *
     * @param channelsStr The string containing the channels
     */
    protected void parseChannels(String channelsStr)
    {
        Map<String, Channel> newChannels = new HashMap<>(1024);

        try
        {
            for (Channel channel : ChannelParser.parse(channelsStr, false, true))
            {
                newChannels.put(getChannelId(channel), channel);
            }
        }
        catch (ParseException e)
        {
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, "failed to get list of channels", e);
        }

        channels = newChannels;
    }

    /**
     * Get the ID of a channel. This is the ID of the channel for DVB channels, and
     * the number of the channel for all other channels.
     *
     * @param channel The channel to process
     * @return The channel's ID
     */
    protected String getChannelId(Channel channel)
    {
        if (channel instanceof DVBChannel)
            return ((DVBChannel) channel).getID();
        return Integer.toString(channel.getChannelNumber());
    }
}
