package com.github.stefantt.vdrcontroller.repository;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.test_utils.SvdrpEmulator;
import com.github.stefantt.vdrcontroller.util.FolderUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;

/**
 * Tests for {@link Recordings}.
 *
 * @author Stefan Taferner
 */
public class RecordingsTest
{
    private static SvdrpEmulator EMULATOR = new SvdrpEmulator(0, SvdrpEmulator.DEFAULT_REPLIES_DIR);
    private static Thread EMULATOR_THREAD;

    @BeforeClass
    public static void startEmulator()
    {
        EMULATOR_THREAD = new Thread(EMULATOR);
        EMULATOR_THREAD.start();
    }

    @AfterClass
    public static void stopEmulator()
    {
        EMULATOR.close();
    }

    @Test
    public void getRootFolder()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));

        VirtualFolder<VdrRecording> root = target.getRootFolder();
        assertNotNull(root);
        assertEquals("/", root.getName());
    }

    @Test
    public void get()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);

        VdrRecording rec = target.get(target.getId(325));
        assertNotNull(rec);
    }

    @Test(expected = VdrRuntimeException.class)
    public void get_notExists()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);
        target.get(new UUID(0, 0));
    }

    @Test
    public void get_402()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);

        VdrRecording rec = target.get(target.getId(402));
        assertNotNull(rec);
    }

    @Test
    public void delete()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);
        target.delete(target.getId(402));
    }

    @Test(expected = VdrRuntimeException.class)
    public void delete_notExists()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);
        target.delete(target.getId(9999));
    }

    @Test
    public void deleteFolder()
    {
        Recordings target = new Recordings(new VdrConnection("localhost", EMULATOR.getPort()));
        target.setReloadTimes(0);
        VirtualFolder<VdrRecording> folder = FolderUtils.findFolder(target.getRootFolder(), "Barbie");
        assertNotNull(folder);
        target.delete(folder);

        assertNull(FolderUtils.findFolder(target.getRootFolder(), "Barbie"));
    }
}
