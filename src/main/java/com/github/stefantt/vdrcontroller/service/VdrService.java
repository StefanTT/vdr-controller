package com.github.stefantt.vdrcontroller.service;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.PLUG;
import org.hampelratte.svdrp.responses.R214;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.hampelratte.svdrp.responses.highlevel.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.entity.Configuration;
import com.github.stefantt.vdrcontroller.entity.EpgsearchEvent;
import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.repository.ChannelRepository;
import com.github.stefantt.vdrcontroller.repository.ProgramGuideRepository;
import com.github.stefantt.vdrcontroller.repository.RecordingRepository;
import com.github.stefantt.vdrcontroller.repository.SearchtimerRepository;
import com.github.stefantt.vdrcontroller.repository.TimerRepository;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(VdrService.class);

    private final VdrConnection vdr;

    private final ChannelRepository channelRepo;
    private final VdrOsdProxy osdProxy;
    private final ProgramGuideRepository programGuideRepo;
    private final RecordingRepository recordingRepo;
    private final SearchtimerRepository searchtimerRepo;
    private final TimerRepository timerRepo;

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
     * @param vdr The VDR connection to use
     */
    public VdrService(VdrConnection vdr)
    {
        this.vdr = vdr;

        this.channelRepo = new ChannelRepository(vdr);
        this.osdProxy = new VdrOsdProxy(vdr);
        this.programGuideRepo = new ProgramGuideRepository(vdr);
        this.recordingRepo = new RecordingRepository(vdr);
        this.searchtimerRepo = new SearchtimerRepository(vdr);
        this.timerRepo = new TimerRepository(vdr);
    }

    /**
     * Clear all cached data.
     */
    public void clearCaches()
    {
        channelRepo.clearCache();
        programGuideRepo.clearCache();
        recordingRepo.clearCache();
        searchtimerRepo.clearCache();
        timerRepo.clearCache();
    }

    /**
     * @return The proxy for OSD operations.
     */
    public VdrOsdProxy getOsdProxy()
    {
        return osdProxy;
    }

    /**
     * @return All search timers.
     */
    public Collection<Searchtimer> getSearchtimers()
    {
        return searchtimerRepo.getAll();
    }

    /**
     * Get a search timer. Returns a new search timer if the supplied ID is -1.
     *
     * @param id The ID of the search timer
     *
     * @return The search timer
     */
    public Searchtimer getSearchtimer(int id)
    {
        if (id == -1)
            return new Searchtimer();

        Searchtimer timer = searchtimerRepo.get(id);
        if (timer == null)
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "search timer not found");
        return timer;
    }

    /**
     * Search events using the given search timer.
     *
     * @param timer The search timer to use
     * @return The found events
     */
    public Collection<EpgsearchEvent> search(Searchtimer timer)
    {
        return searchtimerRepo.search(timer);
    }

    /**
     * Save a search timer.
     *
     * @param timer The timer to save
     */
    public void save(Searchtimer timer)
    {
        LOGGER.debug("TODO Saving search timer #{}", timer.getId());

        // TODO
    }

    /**
     * Enable a search timer.
     *
     * @param id The ID of the search timer
     */
    public void enableSearchtimer(int id)
    {
        LOGGER.info("Enabling searchtimer #{}", id);

        Searchtimer timer = searchtimerRepo.get(id);
        if (timer == null)
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "search timer not found");

        timer.setEnabled(true);
        searchtimerRepo.update(timer);
    }

    /**
     * Disable a search timer.
     *
     * @param id The ID of the search timer
     */
    public void disableSearchtimer(int id)
    {
        LOGGER.info("Disabling searchtimer #{}", id);

        Searchtimer timer = searchtimerRepo.get(id);
        if (timer == null)
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "search timer not found");

        timer.setEnabled(false);
        searchtimerRepo.update(timer);
    }

    /**
     * @return All timers.
     */
    public Collection<Timer> getTimers()
    {
        return timerRepo.getAll();
    }

    /**
     * @return All channels
     */
    public Collection<Channel> getChannels()
    {
        return channelRepo.getAll();
    }

    /**
     * Enable a timer.
     *
     * @param id The ID of the timer
     */
    public void enableTimer(int id)
    {
        timerRepo.enable(id);
    }

    /**
     * Disable a timer.
     *
     * @param id The ID of the timer
     */
    public void disableTimer(int id)
    {
        timerRepo.disable(id);
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
        return programGuideRepo.findByChannelStart(channelId, time);
    }

    /**
     * Get a recording. It is ensured that the returned recording contains details.
     *
     * @param id The ID of the recording
     * @return The detailed recording
     */
    public VdrRecording getRecording(UUID id)
    {
        return recordingRepo.get(id);
    }

    /**
     * Delete a recording.
     *
     * @param id The ID of the recording
     */
    public void deleteRecording(UUID id)
    {
        recordingRepo.delete(id);
    }

    /**
     * Delete virtual folder including all recordings it contains.
     *
     * @param path The path to the folder to delete
     */
    public void deleteRecordings(String path)
    {
        VirtualFolder<VdrRecording> folder = FolderUtils.findFolder(recordingRepo.getRootFolder(), path);
        if (folder == null)
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Folder not found: " + path);

        recordingRepo.delete(folder);
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
            return recordingRepo.getRootFolder();

        return FolderUtils.findFolder(recordingRepo.getRootFolder(), path);
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
        programGuideRepo.setEpgDataFile(StringUtils.isEmpty(epgDataFile) ? null : new File(epgDataFile));

        String channelsFile = config.getChannelsFile();
        channelRepo.setChannelsFile(StringUtils.isEmpty(channelsFile) ? null : new File(channelsFile));
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
