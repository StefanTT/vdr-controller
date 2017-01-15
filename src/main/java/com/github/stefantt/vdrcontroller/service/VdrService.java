package com.github.stefantt.vdrcontroller.service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTT;
import org.hampelratte.svdrp.commands.PLUG;
import org.hampelratte.svdrp.parsers.TimerParser;
import org.hampelratte.svdrp.responses.R214;
import org.hampelratte.svdrp.responses.R550;
import org.hampelratte.svdrp.responses.highlevel.Timer;

import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.repository.Recordings;
import com.github.stefantt.vdrcontroller.util.FolderUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrCapabilities;
import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrOsdProxy;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;
import com.github.stefantt.vdrcontroller.vdr.VdrStatus;
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
    }

    /**
     * Clear all cached data.
     */
    public void clearCaches()
    {
        recordings.clearCache();
    }

    /**
     * @return The proxy for OSD operations.
     */
    public VdrOsdProxy getOsdProxy()
    {
        return osdProxy;
    }

    /**
     * @return The list of timers.
     */
    public List<Timer> getTimers()
    {
        Response res = vdr.query(new LSTT());

        if (res instanceof R550)
            return new LinkedList<Timer>();

        if (res.getCode() != VdrStatus.OK)
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());

        return TimerParser.parse(res.getMessage());
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
