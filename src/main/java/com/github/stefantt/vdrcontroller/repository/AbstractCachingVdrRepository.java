package com.github.stefantt.vdrcontroller.repository;

import java.io.IOException;

import org.hampelratte.svdrp.Connection;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;

/**
 * An abstract class for caching repositories that have VDR backed data.
 *
 * @author Stefan Taferner
 */
public abstract class AbstractCachingVdrRepository extends AbstractCachingRepository
{
    protected final VdrConnection vdr;

    /**
     * Create a caching repository that has VDR backed data.
     *
     * @param vdr The VDR connection to use
     */
    public AbstractCachingVdrRepository(VdrConnection vdr)
    {
        this.vdr = vdr;
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
