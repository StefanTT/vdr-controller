package com.github.stefantt.vdrcontroller.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.Timer;

import com.github.stefantt.vdrcontroller.dto.BriefRecording;
import com.github.stefantt.vdrcontroller.dto.BriefSearchtimer;
import com.github.stefantt.vdrcontroller.dto.BriefTimer;
import com.github.stefantt.vdrcontroller.dto.RecordingsFolder;
import com.github.stefantt.vdrcontroller.entity.Named;
import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;

/**
 * Collection of VDR utility functions.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public final class VdrUtils
{
    //private static final Logger LOGGER = LoggerFactory.getLogger(VdrUtils.class);

    // Separator character for the creation of recording IDs
    private static final char ID_SEPARATOR = '\01';

    // Unescaper for translating hex exscaped strings to normal strings
    private static final CharSequenceTranslator HEX_UNESCAPER = new HexUnescaper();

    private static final SimpleDateFormat RECTIME_FMT = new SimpleDateFormat("yyyy-MM-dd.HH.mm");
    private static final Pattern PATH_STRIP_PATTERN = Pattern.compile("\\.\\d+[-\\.]\\d+\\.rec$");

    private VdrUtils()
    {
    }

    /**
     * Test if the recordings list entry and the recording path are similar.
     *
     * @param rec The list representation of the recording
     * @param path The path to the recording
     * @return True if they are similar, false if not
     */
    public static boolean isSimilar(Recording rec, String path)
    {
        path = PATH_STRIP_PATTERN.matcher(path).replaceFirst("")
            .replace('/', ID_SEPARATOR).replace('~', '/');

        String title = rec.getTitle().replace(' ', '_').replace('~', ID_SEPARATOR);

        String startTimeStr = RECTIME_FMT.format(rec.getStartTime().getTime());

        String pathSuffix = ID_SEPARATOR + title + ID_SEPARATOR + startTimeStr;
        return path.endsWith(pathSuffix);
    }

    /**
     * Create a unique key for a recording's filesystem path.
     *
     * Example paths:
     * <pre>
     * /video/Reiten/Die_Pferdeprofis/2017.01.07-19:10-Sam/2017-01-07.19.08.14-0.rec
     * /video/Garfield/Garfield/Das_Kätzchen-Gesetz_~_Die_Vertauschten_~/2016-12-03.16.08.23-0.rec
     * /video/Dragons/Dragons_-_Auf_zu_neuen_Ufern/Folge_59:_'Lebenslange_Schuld'/2017-01-07.09.18.24-0.rec
     * </pre>
     *
     * @param path The filesystem path to the recording
     * @return The unique key for the recording
     */
    public static String createRecordingKey(String path)
    {
        String[] parts = HEX_UNESCAPER.translate(path).split("/");
        Validate.isTrue(parts.length >= 2);

        String startTime = parts[parts.length - 1].replaceFirst("\\.\\d{2}-\\d+\\.rec$", "");
        String title = parts[parts.length - 2];
        String subTitle = "";

        if (parts.length >= 3)
        {
            subTitle = title;
            title = parts[parts.length - 3];
        }

        return (title + ID_SEPARATOR + subTitle + ID_SEPARATOR + startTime).replace('~', '/');
    }

    /**
     * Create a unique key for a recording from the recording's {@link Recording#getTitle() list title}.
     * Only works for recordings list entries, not for recording details entries.
     *
     * @param rec The recording to process
     * @return The unique key for the recording
     */
    public static String createRecordingKey(Recording rec)
    {
        String[] parts = HEX_UNESCAPER.translate(rec.getTitle()).split("~");
        Validate.isTrue(parts.length >= 1);

        String startTime = RECTIME_FMT.format(rec.getStartTime().getTime());
        String title = parts[parts.length - 1];
        String subTitle = "";

        if (parts.length >= 2)
        {
            subTitle = title;
            title = parts[parts.length - 2];
        }

        return (title + ID_SEPARATOR + subTitle + ID_SEPARATOR + startTime).replace(' ', '_');
    }

    /**
     * Create a unique ID for a recording's filesystem path.
     *
     * @param path The filesystem path to the recording
     * @return The unique ID for the recording
     */
    public static UUID createRecordingId(String path)
    {
        return UUID.nameUUIDFromBytes(createRecordingKey(path).getBytes());
    }

    /**
     * Create a unique UUID for a recording from the recording's {@link Recording#getTitle() list title}.
     * Only works for recordings list entries, not for recording details entries.
     *
     * @param rec The recording to process
     * @return The unique ID for the recording
     */
    public static UUID createRecordingId(Recording rec)
    {
        return UUID.nameUUIDFromBytes(createRecordingKey(rec).getBytes());
    }

    /**
     * Convert the list of timers into a list of brief timers.
     *
     * @param timers The list of timers to convert
     * @return The converted list of brief timers
     */
    public static List<BriefTimer> toBriefTimers(Collection<Timer> timers)
    {
        List<BriefTimer> result = new ArrayList<>(timers.size());

        for (Timer timer : timers)
            result.add(new BriefTimer(timer));

        return result;
    }

    /**
     * Convert the list of recordings into a list of brief recordings.
     *
     * @param recordings The list of recordings to convert
     * @return The converted list of brief recordings
     */
    public static List<BriefRecording> toBriefRecordings(Collection<VdrRecording> recordings)
    {
        List<BriefRecording> result = new ArrayList<>(recordings.size());

        for (VdrRecording recording : recordings)
            result.add(new BriefRecording(recording));

        return result;
    }

    /**
     * Convert the virtual folder to a list for the recordings overview.
     *
     * @param folder The folder to convert
     * @return A list containing the child folders and recordings of the given folder
     */
    public static List<Named> toRecordingsOverviewList(VirtualFolder<VdrRecording> folder)
    {
        Validate.notNull(folder);
        List<Named> result = new ArrayList<>(folder.getFiles().size() + folder.getFolders().size());

        for (VirtualFolder<VdrRecording> childFolder : folder.getFolders())
            result.add(new RecordingsFolder(childFolder));

        for (VdrRecording rec : folder.getFiles())
            result.add(new BriefRecording(rec));

        return result;
    }

    /**
     * Convert the list of timers into a list of brief search timers.
     *
     * @param timers The list of search timers to convert
     * @return The converted list of brief search timers
     */
    public static List<BriefSearchtimer> toBriefSearchtimers(Collection<Searchtimer> timers)
    {
        List<BriefSearchtimer> result = new ArrayList<>(timers.size());

        for (Searchtimer timer : timers)
            result.add(new BriefSearchtimer(timer));

        return result;
    }
}
