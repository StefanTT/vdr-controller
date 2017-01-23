package com.github.stefantt.vdrcontroller.repository;

/**
 * Abstract base class for repositories that cache data.
 *
 * @author Stefan Taferner
 */
public abstract class AbstractCachingRepository
{
    public static final int DATA_MAX_AGE_MSEC = 60000;
    private long lastUpdated = 0;

    /**
     * Clear all cached data.
     */
    public final void clearCache()
    {
        lastUpdated = 0;
    }

    /**
     * Ensure that the cached data is up to date.
     */
    protected final void ensureUpdated()
    {
        if (System.currentTimeMillis() - DATA_MAX_AGE_MSEC > lastUpdated)
        {
            update();
            lastUpdated = System.currentTimeMillis();
        }
    }

    /**
     * @return True if the cached data needs to be updated.
     */
    protected final boolean isUpdateRequired()
    {
        return System.currentTimeMillis() - DATA_MAX_AGE_MSEC > lastUpdated;
    }

    /**
     * Mark as updated.
     */
    protected final void markUpdated()
    {
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * Update the cached data.
     */
    protected abstract void update();
}