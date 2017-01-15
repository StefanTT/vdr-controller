package com.github.stefantt.vdrcontroller;

import java.io.IOException;

import com.github.stefantt.vdrcontroller.test_utils.SvdrpEmulator;

/**
 * Opens a port and emulates VDR's SVDRP protocol on this port.
 *
 * @author Stefan Taferner
 */
public class SvdrpEmulatorMain
{
    private static final int DEFAULT_PORT = 64191;

    public static void main(String[] args) throws IOException
    {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        SvdrpEmulator emulator = new SvdrpEmulator(port, SvdrpEmulator.DEFAULT_REPLIES_DIR);
        emulator.run();
    }
}
