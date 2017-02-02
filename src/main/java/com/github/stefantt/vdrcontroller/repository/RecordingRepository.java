package com.github.stefantt.vdrcontroller.repository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.DELR;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.parsers.RecordingListParser;
import org.hampelratte.svdrp.parsers.RecordingParser;
import org.hampelratte.svdrp.responses.R215;
import org.hampelratte.svdrp.responses.R250;
import org.hampelratte.svdrp.responses.R550;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.util.FolderUtils;
import com.github.stefantt.vdrcontroller.util.VdrUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;
import com.github.stefantt.vdrcontroller.vdr.VdrSessionTask;
import com.github.stefantt.vdrcontroller.vdr.VdrStatus;
import com.github.stefantt.vdrcontroller.vdr.commands.LSTRpath;

/**
 * A repository that holds the VDR recordings.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class RecordingRepository extends AbstractCachingComplexVdrRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingRepository.class);
    private static final String ROOT_FOLDER_NAME = "/";

    private Map<UUID, VdrRecording> recordings = new HashMap<>();
    private VirtualFolder<VdrRecording> rootFolder = new VirtualFolder<>(ROOT_FOLDER_NAME, null);
    private int reloadTries = 3;

    /**
     * Create a repository that holds the VDR recordings.
     *
     * @param vdr The VDR connection to use
     */
    public RecordingRepository(VdrConnection vdr)
    {
        super(vdr);
    }

    /**
     * Get the UUID of a recording which is specified by it's number.
     *
     * @param number The number of the recording
     * @return The recording's ID, null if not found
     */
    public UUID getId(int number)
    {
        ensureUpdated();

        for (VdrRecording rec : recordings.values())
        {
            if (rec.getNumber() == number)
                return rec.getId();
        }
        return null;
    }

    /**
     * Get a single recording. It is ensured that the returned recording contains details.
     *
     * @param id The ID of the recording
     * @return The detailed recording
     */
    public VdrRecording get(UUID id)
    {
        VdrRecording rec = recordings.get(id);
        if (rec != null && rec.isDetailed())
            return rec;

        return executeWithRetry((con) -> {
            VdrRecording recording = recordings.get(id);
            if (recording == null)
                throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Recording not found");

            int number = recording.getNumber();
            LOGGER.debug("Fetching details of recording #{}", number);

            Response pathRes = con.send(new LSTRpath(number));
            if (!(pathRes instanceof R250))
                throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Recording #" + number + " not found");
            String path = pathRes.getMessage().trim();

            if (!VdrUtils.isSimilar(recording.getRec(), path))
            {
                LOGGER.debug("Recording list entry: {}", recording.getRec());
                LOGGER.debug("Recording path: {}", path);
                return null;
            }

            Response detailedRes = con.send(new LSTR(recording.getNumber()));
            if (!(detailedRes instanceof R215))
                throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Recording #" + number + " not found");

            Recording detailedRec = new Recording();
            try
            {
                new RecordingParser().parseRecording(detailedRec, detailedRes.getMessage());
            }
            catch (ParseException e)
            {
                throw new VdrRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR_500,
                    "failed to parse detailed recording", e);
            }

            VdrRecording result = new VdrRecording(detailedRec);
            result.updateFromListEntry(recording);

            return result;
        });
    }

    /**
     * Delete a folder and all the recordings it contains.
     *
     * @param folder The folder to delete
     */
    public void delete(VirtualFolder<VdrRecording> folder)
    {
        executeWithRetry(Math.max(reloadTries, folder.getNumRecordings() * 3), (con) ->
        {
            List<VdrRecording> recordings = folder.getAllFiles();
            sortByNumber(recordings);

            for (VdrRecording rec : recordings)
            {
                if (!Boolean.TRUE.equals(delete(con, rec)))
                    return null;
            }

            VirtualFolder<VdrRecording> parentFolder = folder.getParent();
            if (parentFolder != null)
                parentFolder.remove(folder);

            return Boolean.TRUE;
        });
    }

    /**
     * Delete a recording.
     *
     * @param id The ID of the recording
     */
    public void delete(UUID id)
    {
        executeWithRetry((con) -> {
            VdrRecording recording = recordings.get(id);
            if (recording == null)
                throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Recording not found");

            return delete(con, recording);
        });
    }

    /**
     * Delete a recording using the given vdr connection.
     *
     * @param con The SVDRP connection
     * @param recording The recording to delete
     * @return True if successful, null if the list of recordings needs a reload
     * @throws IOException if the VDR communication fails
     */
    private Boolean delete(Connection con, VdrRecording recording) throws IOException
    {
        int number = recording.getNumber();

        LOGGER.debug("Deleting recording #{} {}", number, recording.getName());

        Response res = con.send(new LSTRpath(number));
        if (!(res instanceof R250))
            throw new VdrRuntimeException(HttpStatus.NOT_FOUND_404, "Recording #" + number + " not found");
        String path = res.getMessage().trim();

        if (!VdrUtils.isSimilar(recording.getRec(), path))
        {
            LOGGER.info("Recording list entry: {}", recording.getRec());
            LOGGER.debug("Recording path: {}", path);
            return null;
        }

        res = con.send(new DELR(number));
        if (!(res instanceof R250))
        {
            throw new VdrRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR_500,
                "Could not delete recording: " + res.getMessage());
        }

        return Boolean.TRUE;
    }

    /**
     * Get the folder structure.
     *
     * @return The root folder of the recordings
     */
    public VirtualFolder<VdrRecording> getRootFolder()
    {
        ensureUpdated();
        return rootFolder;
    }

    /**
     * Set the recordings. This replaces the contents of the repository with the new recordings.
     *
     * @param recs The recordings.
     */
    public void setRecordings(List<VdrRecording> recs)
    {
        markUpdated();

        this.recordings.clear();
        recs.forEach((rec) -> {
            recordings.put(rec.getId(), rec);
        });
    }

    /**
     * Execute the task. If it returns null then update the recordings and re-execute the task until
     * either the task returns a not null value or the maximum number of retries is reached.
     *
     * @param task The task to execute
     * @return The return value of the task
     */
    private <T> T executeWithRetry(VdrSessionTask<T> task)
    {
        return executeWithRetry(reloadTries, task);
    }

    /**
     * Execute the task. If it returns null then update the recordings and re-execute the task until
     * either the task returns a not null value or the maximum number of retries is reached.
     *
     * @param retries The maximum number of retries for executing the task
     * @param task The task to execute
     * @return The return value of the task
     */
    private <T> T executeWithRetry(int retries, VdrSessionTask<T> task)
    {
        return vdr.execute((con) -> {
            for (int tries = retries; tries >= 0; --tries)
            {
                if (isUpdateRequired())
                    update(con);

                T ret = task.execute(con);
                if (ret != null)
                    return ret;

                if (tries > 0)
                {
                    LOGGER.debug("List of recordings may have changed, reloading");
                    clearCache();
                }
            }

            throw new VdrRuntimeException(HttpStatus.CONFLICT_409, "recordings changed too often");
        });
    }

    @Override
    protected Void update(Connection con) throws IOException
    {
        LOGGER.debug("Updating recordings");

        Response res = con.send(new LSTR());
        if (res instanceof R550)
        {
            recordings.clear();
        }
        else if (res.getCode() != VdrStatus.OK)
        {
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, res.getMessage());
        }
        else
        {
            List<Recording> recs = RecordingListParser.parse(res.getMessage());
            Map<UUID, VdrRecording> newRecordings = new HashMap<>(recs.size());

            for (Recording rec : recs)
            {
                VdrRecording recording = new VdrRecording(rec);
                newRecordings.put(recording.getId(), recording);
            }

            recordings = newRecordings;
        }

        updateFolders();
        return null;
    }

    /**
     * Update the folder structure.
     */
    private void updateFolders()
    {
        VirtualFolder<VdrRecording> newRootFolder = new VirtualFolder<>(ROOT_FOLDER_NAME, null);

        for (VdrRecording rec : recordings.values())
        {
            VirtualFolder<VdrRecording> folder = FolderUtils.getOrCreateFolder(newRootFolder,
                FolderUtils.folderPath(rec.getTitle()), true);

            folder.add(rec);
        }

        compactRecordingFolders(newRootFolder);

        rootFolder = newRootFolder;
    }

    private VdrRecording compactRecordingFolders(VirtualFolder<VdrRecording> folder)
    {
        for (VirtualFolder<VdrRecording> childFolder : new ArrayList<>(folder.getFolders()))
        {
            VdrRecording file = compactRecordingFolders(childFolder);
            if (file != null)
            {
                //LOGGER.debug("Compacting recording {}", file.getTitle());

                file.setName(childFolder.getName() + " - " + file.getName());

                folder.remove(childFolder);
                folder.add(file);
            }
        }

        if (folder.getFolders().isEmpty() && folder.getFiles().size() == 1)
        {
            return folder.getFiles().iterator().next();
        }

        return null;
    }

    /**
     * Sort the given list of recordings descending by recording number.
     *
     * @param list The list of recordings to sort
     */
    private void sortByNumber(List<VdrRecording> list)
    {
        list.sort((VdrRecording a, VdrRecording b) -> Integer.compare(b.getNumber(), a.getNumber()));
    }

    /**
     * Set the number of times the list of recordings are reloaded if a change is detected during an
     * operation. Default is 3 times.
     *
     * @param num The number of times to reload
     */
    public void setReloadTimes(int num)
    {
        reloadTries = num;
    }
}
