package com.github.stefantt.vdrcontroller.vdr;

import java.io.IOException;

import org.hampelratte.svdrp.Connection;

/**
 * A task that is executed within a SVDRP connection.
 *
 * @author Stefan Taferner
 */
public interface VdrSessionTask<T>
{
    /**
     * Task to run with an open SVDRP connection. The connection is closed
     * when the task ends.
     *
     * @param con The SVDRP connection
     * @throws IOException if the VDR communication fails
     */
    T execute(Connection con) throws IOException;
}
