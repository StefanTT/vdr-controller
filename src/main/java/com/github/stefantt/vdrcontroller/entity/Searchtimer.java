package com.github.stefantt.vdrcontroller.entity;

/**
 * An auto timer entry.
 *
 * @author Stefan Taferner
 */
public class Searchtimer
{
    /**
     * The search mode for auto timers.
     *
     * @author Stefan Taferner
     */
    public enum SearchMode
    {
        /**
         * The search string must match exact.
         */
        EXACT,

        /**
         * The whole search string must appear as substring.
         */
        SUBSTRING,

        /**
         * All words must appear, separators are blank ',' ';' '|' or '~'.
         */
        ALL_WORDS,

        /**
         * At least one word must appear, separators are blank ',' ';' '|' or '~'.
         */
        ANY_WORD
    }

    /**
     * The default priority.
     */
    public static final int DEFAULT_PRIORITY = 50;

    /**
     * The default lifetime.
     */
    public static final int DEFAULT_LIFETIME = 50;

    /**
     * The default minutes to add at the beginning of the recording.
     */
    public static final int DEFAULT_TIMEMARGIN_START = 2;

    /**
     * The default minutes to add after the end of the recording.
     */
    public static final int DEFAULT_TIMEMARGIN_STOP = 10;

    /**
     * The default percentage when comparing descriptions for repeats detection.
     */
    public static final int DEFAULT_REPEATS_MATCH_DESC_PERC = 90;

    private int id = -1;
    private boolean enabled = true;
    private String search;
    private SearchMode searchMode = SearchMode.ALL_WORDS;
    private boolean isCaseSensitive;
    private int startTimeMin; // in minutes since midnight
    private int startTimeMax; // in minutes since midnight, min=max=0 to disable check
    private int durationMin;  // in minutes
    private int durationMax;  // in minutes, 0 to disable check
    private int weekdays;     // bitfield: bit 0=sunday, 1=monday, ...
    private boolean searchTitle;
    private boolean searchSubtitle;
    private boolean searchDescription;
    private String fromChannel;
    private String toChannel;
    private String channelGroup;
    private int timeMarginStart = 2; // in minutes
    private int timeMarginStop = 10; // in minutes
    private String folder; // name of the directory for the recordings
    private int priority = DEFAULT_PRIORITY;
    private int lifetime = DEFAULT_LIFETIME;
    private boolean isSeries;
    private boolean avoidRepeats;
    private boolean repeatsCompareTitle;
    private boolean repeatsCompareSubtitle;
    private Integer repeatsMatchDescriptionPercent = null;
    private int deleteAfterDays;
    private int keepRecordings;

    public String getChannelGroup()
    {
        return channelGroup;
    }

    public int getDeleteAfterDays()
    {
        return deleteAfterDays;
    }

    public int getDurationMin()
    {
        return durationMin;
    }

    public int getDurationMax()
    {
        return durationMax;
    }

    public String getFolder()
    {
        return folder;
    }

    public String getFromChannel()
    {
        return fromChannel;
    }

    public int getId()
    {
        return id;
    }

    public int getKeepRecordings()
    {
        return keepRecordings;
    }

    public int getLifetime()
    {
        return lifetime;
    }

    public int getPriority()
    {
        return priority;
    }

    public int getRepeatsMatchDescriptionPercent()
    {
        if (repeatsMatchDescriptionPercent == null)
            return DEFAULT_REPEATS_MATCH_DESC_PERC;
        return repeatsMatchDescriptionPercent;
    }

    public String getSearch()
    {
        return search;
    }

    public SearchMode getSearchMode()
    {
        return searchMode;
    }

    /**
     * @return True if the start time checking shall be used, false if not
     */
    public boolean useStartTime()
    {
        return startTimeMin != 0 || startTimeMax != 0;
    }

    public int getStartTimeMin()
    {
        return startTimeMin;
    }

    public int getStartTimeMax()
    {
        return startTimeMax;
    }

    public int getTimeMarginStart()
    {
        return timeMarginStart;
    }

    public int getTimeMarginStop()
    {
        return timeMarginStop;
    }

    public String getToChannel()
    {
        return toChannel;
    }

    /**
     * The weekdays on which the timer shall be active. Each bit represents a weekday, with bit #0
     * being Sunday and bit #6 being Saturday.
     *
     * @return The bitfield of weekdays.
     */
    public int getWeekdays()
    {
        return weekdays;
    }

    public boolean isAvoidRepeats()
    {
        return avoidRepeats;
    }

    public boolean isCaseSensitive()
    {
        return isCaseSensitive;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isRepeatsCompareDescription()
    {
        return repeatsMatchDescriptionPercent != null;
    }

    public boolean isRepeatsCompareSubtitle()
    {
        return repeatsCompareSubtitle;
    }

    public boolean isRepeatsCompareTitle()
    {
        return repeatsCompareTitle;
    }

    public boolean isSearchDescription()
    {
        return searchDescription;
    }

    public boolean isSearchSubtitle()
    {
        return searchSubtitle;
    }

    public boolean isSearchTitle()
    {
        return searchTitle;
    }

    public boolean isSeries()
    {
        return isSeries;
    }

    public void setAvoidRepeats(boolean avoidRepeats)
    {
        this.avoidRepeats = avoidRepeats;
    }

    public void setCaseSensitive(boolean isCaseSensitive)
    {
        this.isCaseSensitive = isCaseSensitive;
    }

    public void setChannelGroup(String channelGroup)
    {
        this.channelGroup = channelGroup;
    }

    public void setDeleteAfterDays(int deleteAfterDays)
    {
        this.deleteAfterDays = deleteAfterDays;
    }

    public void setDurationRange(int min, int max)
    {
        this.durationMin = min;
        this.durationMax = max;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setFolder(String folder)
    {
        this.folder = folder;
    }

    public void setFromChannel(String fromChannel)
    {
        this.fromChannel = fromChannel;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setKeepRecordings(int num)
    {
        this.keepRecordings = num;
    }

    public void setLifetime(int lifetime)
    {
        this.lifetime = lifetime;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public void setRepeatsCompareSubtitle(boolean repeatsCompareSubtitle)
    {
        this.repeatsCompareSubtitle = repeatsCompareSubtitle;
    }

    public void setRepeatsCompareTitle(boolean repeatsCompareTitle)
    {
        this.repeatsCompareTitle = repeatsCompareTitle;
    }

    public void setRepeatsMatchDescriptionPercent(int repeatsMatchDescriptionPercent)
    {
        this.repeatsMatchDescriptionPercent = repeatsMatchDescriptionPercent;
    }

    public void setSearch(String search)
    {
        this.search = search;
    }

    public void setSearchDescription(boolean searchDescription)
    {
        this.searchDescription = searchDescription;
    }

    public void setSearchMode(SearchMode searchMode)
    {
        this.searchMode = searchMode;
    }

    public void setSearchSubtitle(boolean searchSubtitle)
    {
        this.searchSubtitle = searchSubtitle;
    }

    public void setSearchTitle(boolean searchTitle)
    {
        this.searchTitle = searchTitle;
    }

    public void setSeries(boolean isSeries)
    {
        this.isSeries = isSeries;
    }

    public void setStartTimeRange(int min, int max)
    {
        this.startTimeMin = min;
        this.startTimeMax = max;
    }

    public void setTimeMarginStart(int timeMarginStart)
    {
        this.timeMarginStart = timeMarginStart;
    }

    public void setTimeMarginStop(int timeMarginStop)
    {
        this.timeMarginStop = timeMarginStop;
    }

    public void setToChannel(String toChannel)
    {
        this.toChannel = toChannel;
    }

    /**
     * The weekdays on which the timer shall be active. Each bit represents a weekday, with bit #0
     * being Sunday and bit #6 being Saturday.
     *
     * @param weekdays The bitfield of weekdays.
     */
    public void setWeekdays(int weekdays)
    {
        this.weekdays = weekdays;
    }
}
