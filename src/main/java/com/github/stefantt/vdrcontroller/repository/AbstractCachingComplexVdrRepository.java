package com.github.stefantt.vdrcontroller.repository;

import java.io.IOException;

import org.hampelratte.svdrp.Connection;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;

/**
 * An abstract class for caching repositories that have VDR backed data that needs a complex update
 * process with multiple VDR calls.
 *
 * @author Stefan Taferner
 */
public abstract class AbstractCachingComplexVdrRepository extends AbstractCachingVdrRepository
{
    /**
     * Create a caching repository that has VDR backed data.
     *
     * @param vdr The VDR connection to use
     */
    public AbstractCachingComplexVdrRepository(VdrConnection vdr)
    {
        super(vdr);
    }

    @Override
    protected final synchronized void update()
    {
        vdr.execute((con) -> update(con));
    }

    /**
     * Update the cached data using the supplied VDR connection.
     *
     * @param con The VDR connection to use for updating
     * @return null
     * @throws IOException in case of communication problems
     */
    protected abstract Void update(Connection con) throws IOException;
}
