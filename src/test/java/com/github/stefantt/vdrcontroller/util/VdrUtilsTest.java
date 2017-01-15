package com.github.stefantt.vdrcontroller.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Version;
import org.hampelratte.svdrp.parsers.RecordingListParser;
import org.hampelratte.svdrp.parsers.RecordingParser;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VdrUtilsTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VdrUtilsTest.class);
    private static List<Recording> REC_LIST;

    @BeforeClass
    public static void beforeClass()
    {
        Connection.setVersion(new Version("2.2.0"));
        REC_LIST = RecordingListParser.parse(getStringResource("recordingsList.txt"));
    }

    /**
     * Compare unique recording keys for path, recording list, and recording details entry.
     * @throws ParseException
     */
    @Test
    public void createRecordingKey_pathListDetails() throws ParseException
    {
        String pathKey = VdrUtils.createRecordingKey(
            "/video/Garfield/Garfield/Das_Kätzchen-Gesetz_~_Die_Vertauschten_~/2016-12-03.16.08.23-0.rec");

        Recording recLst = REC_LIST.get(1);
        assertEquals(825, recLst.getNumber());
        String listKey = VdrUtils.createRecordingKey(recLst);

        Recording rec = new Recording();
        new RecordingParser().parseRecording(rec, getStringResource("recordingDetails_825.txt"));
        assertNotNull(rec);
        String detailsKey = VdrUtils.createRecordingKey(rec);

        assertEquals(pathKey, listKey);
        assertEquals(pathKey, detailsKey);
    }

    /**
     * Create the ID of a list entry with a subtitle.
     */
//    @Test
    public void createRecordingId_listEntry()
    {
        Recording rec = REC_LIST.get(0);

        UUID id = VdrUtils.createRecordingId(rec);
        assertNotNull(id);
    }

    /**
     * Create the ID of a details entry with a subtitle.
     *
     * @throws ParseException
     */
//    @Test
    public void createRecordingId_detailsEntry() throws ParseException
    {
        Recording rec = new Recording();
        new RecordingParser().parseRecording(rec, getStringResource("recordingDetails_825.txt"));

        UUID id = VdrUtils.createRecordingId(rec);
        assertNotNull(id);
    }

    /**
     * Compare IDs for a recording.
     *
     * Create the ID of a list entry with a subtitle.
     * Create the ID of a details entry with a subtitle, which is the same entry as the list entry.
     * Both IDs must be the same.
     *
     * @param idx The index of the recording in the test data list
     * @param detailsFileName The name of the details file
     */
    private void createRecordingId_compareLstDet(int idx, String detailsFileName) throws ParseException
    {
        Recording lstRec = REC_LIST.get(idx);
        UUID lstId = VdrUtils.createRecordingId(lstRec);

        Recording detRec = new Recording();
        new RecordingParser().parseRecording(detRec, getStringResource(detailsFileName));
        UUID detId = VdrUtils.createRecordingId(detRec);

        SimpleDateFormat dateFmt = new SimpleDateFormat();
        LOGGER.debug("list entry has start time {}", dateFmt.format(lstRec.getStartTime().getTime()));
        LOGGER.debug("details entry has start time {}", dateFmt.format(detRec.getStartTime().getTime()));

        // Pre check: compare both start times to have a chance for catching changed test data
        assertEquals(lstRec.getStartTime().getTimeInMillis(), detRec.getStartTime().getTimeInMillis());

        // The real test: compare list and details ID
        assertEquals(lstId, detId);
    }

    private int indexOfRecNum(int number)
    {
        for (int idx = 0; idx < REC_LIST.size(); ++idx)
        {
            if (REC_LIST.get(idx).getNumber() == number)
                return idx;
        }

        assertTrue("Recording #" + number + " not found in test data list", false);
        return -1;
    }

    /**
     * Compare a single, selected list and details representation of a test record.
     * @throws ParseException
     */
//    @Test
    public void createRecordingId_compare() throws ParseException
    {
        int number = 825;

        LOGGER.debug("Comparing list and details for recording #{}", number);
        createRecordingId_compareLstDet(indexOfRecNum(number), "recordingDetails_" + number + ".txt");
    }

    /**
     * Compare list and details representation of all test records.
     *
     * @throws ParseException
     */
//    @Test
    public void createRecordingId_compareAll() throws ParseException
    {
        for (int idx = 0; idx < REC_LIST.size(); ++idx)
        {
            int number = REC_LIST.get(idx).getNumber();
            LOGGER.debug("* Comparing list and details for recording #{}", number);
            createRecordingId_compareLstDet(idx, "recordingDetails_" + number + ".txt");
        }
        LOGGER.debug("* Comparing list done");
    }

    /**
     * Load a file and return it's contents as a string.
     *
     * @param fileName The name of the file to load
     * @return The file's contents
     */
    private static String getStringResource(String fileName)
    {
        InputStream in = VdrUtilsTest.class.getResourceAsStream(fileName);
        assertNotNull("test file not found: " + fileName, in);

        try
        {
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String result = writer.toString().trim();

            assertTrue("Test file empty: " + fileName, !result.isEmpty());
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
}
