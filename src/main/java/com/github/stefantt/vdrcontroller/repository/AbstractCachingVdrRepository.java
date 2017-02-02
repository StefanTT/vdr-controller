package com.github.stefantt.vdrcontroller.repository;

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
}
