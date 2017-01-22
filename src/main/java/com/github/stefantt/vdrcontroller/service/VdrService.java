package com.github.stefantt.vdrcontroller.service;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.PLUG;
import org.hampelratte.svdrp.responses.R214;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.hampelratte.svdrp.responses.highlevel.Timer;

import com.github.stefantt.vdrcontroller.entity.Configuration;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.repository.ProgramGuides;
import com.github.stefantt.vdrcontroller.repository.Recordings;
import com.github.stefantt.vdrcontroller.repository.Timers;
import com.github.stefantt.vdrcontroller.util.FolderUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrCapabilities;
import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrOsdProxy;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;
import com.github.stefantt.vdrcontroller.vdr.parser.PluginListParser;

/**
 * The service for accessing VDR data and doing VDR related tasks.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class VdrService
{
    private final VdrConnection vdr;
    private final VdrOsdProxy osdProxy;
    private final Recordings recordings;
    private final ProgramGuides programGuides;
    private final Timers timers;

    private VdrCapabilities capabilities;

    /**
     * Create a service for accessing VDR data and doing VDR related tasks.
     *
     * @param host The name of the host that is running VDR
     * @param port The SVDRP port of vdr on the host
     */
    public VdrService(String host, int port)
    {
        this(new VdrConnection(host, port));
    }

    /**
     * Create a service for accessing VDR data and doing VDR related tasks.
     *
     * @param connection The VDR connection to use
     */
    public VdrService(VdrConnection connection)
    {
        this.vdr = connection;

        this.osdProxy = new VdrOsdProxy(vdr);
        this.recordings = new Recordings(vdr);
        this.programGuides = new ProgramGuides(vdr);
        this.timers = new Timers(vdr);
    }

    /**
     * Clear all cached data.
     */
    public void clearCaches()
    {
        recordings.clearCache();
        programGuides.clearCache();
    }

    /**
     * @return The proxy for OSD operations.
     */
    public VdrOsdProxy getOsdProxy()
    {
        return osdProxy;
    }

    /**
     * @return All timers.
     */
    public Collection<Timer> getTimers()
    {
        return timers.getAll();
    }

    /**
     * Enable a timer.
     *
     * @param id The ID of the timer
     */
    public void enableTimer(int id)
    {
        timers.enable(id);
    }

    /**
     * Disable a timer.
     *
     * @param id The ID of the timer
     */
    public void disableTimer(int id)
    {
        timers.disable(id);
    }

    /**
     * Get a specific EPG entry.
     *
     * @param channelId The ID of the channel
     * @param time The start time of the entry
     * @return The EPG entry, null if not found
     */
    public EPGEntry findEpgEntryByChannelTime(String channelId, long time)
    {
        return programGuides.findByChannelStart(channelId, time);
    }

    /**
     * Get a recording. It is ensured that the returned recording contains details.
     *
     * @param id The ID of the recording
     * @return The detailed recording
     */
    public VdrRecording getRecording(UUID id)
    {
        return recordings.get(id);
    }

    /**
     * Delete a recording.
     *
     * @param id The ID of the recording
     */
    public void deleteRecording(UUID id)
    {
        recordings.delete(id);
    }

    /**
     * Delete virtual folder including all recordings it contains.
     *
     * @param path The path to the folder to delete
     */
    public void deleteRecordings(String path)
    {
        VirtualFolder<VdrRecording> folder = FolderUtils.findFolder(recordings.getRootFolder(), path);
        if (folder == null)
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Folder not found: " + path);

        recordings.delete(folder);
    }

    /**
     * Get a virtual folder of the VDR recordings.
     *
     * @param path The path to the folder
     * @return The folder
     */
    public VirtualFolder<VdrRecording> getRecordingsFolder(String path)
    {
        if (StringUtils.isEmpty(path))
            return recordings.getRootFolder();

        return FolderUtils.findFolder(recordings.getRootFolder(), path);
    }

    /**
     * @return The VDR connection that is used.
     */
    public VdrConnection getConnection()
    {
        return vdr;
    }

    /**
     * @return The capabilities of the VDR server.
     */
    public synchronized VdrCapabilities getCapabilities()
    {
        if (capabilities != null)
            return capabilities;

        Response res = vdr.query(new PLUG());
        if (!(res instanceof R214))
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());

        capabilities = new VdrCapabilities(PluginListParser.parse(res.getMessage()));

        return capabilities;
    }

    /**
     * Configure the service from the configuration.
     *
     * @param config The configuration to use
     */
    public void setConfiguration(Configuration config)
    {
        setConnection(config.getVdrHost(), config.getVdrPort());

        String epgDataFile = config.getEpgDataFile();
        programGuides.setEpgDataFile(StringUtils.isEmpty(epgDataFile) ? null : new File(epgDataFile));
    }

    /**
     * Change the VDR host and port to connect to.
     *
     * @param host The name of the host that is running VDR
     * @param port The SVDRP port of vdr on the host
     */
    public void setConnection(String host, int port)
    {
        vdr.setHost(host, port);
        capabilities = null;
        clearCaches();
    }
}
