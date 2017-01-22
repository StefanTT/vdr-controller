package com.github.stefantt.vdrcontroller.entity;

import java.util.List;

/**
 * An epgsearch auto timer.
 *
 * @see http://manpages.ubuntu.com/manpages/xenial/en/man5/epgsearch.conf.5.html
 *
 * @author Stefan Taferner
 */
public class EpgsearchTimer
{
    private int id;
    private String search;
    private boolean useTime;
    private Integer startTime; // minutes since midnight
    private Integer stopTime; // minutes since midnight
    private ChannelMode channelMode = ChannelMode.NO_CHANNEL;
    private String channel; // channel ID or ranges separated with '|'
    private boolean caseSensitive;
    private SearchMode searchMode = SearchMode.WHOLE_TERM;
    private boolean useTitle;
    private boolean useSubtitle;
    private boolean useDescription;
    private boolean useDuration;
    private Integer minDuration; // minimum duration in minutes
    private Integer maxDuration; // maximum duration in minutes
    private boolean useAsSearchTimer;
    private boolean useDayOfWeek;
    private int dayOfWeek; // 0 = Sunday, 1 = Monday...; -1 Sunday, -2 Monday, -4 Tuesday, ...; -7 Sun, Mon, Tue
    private boolean useSeriesRecording;
    private String directoryForRecording;
    private int priority;
    private int lifetime;
    private int timeMarginStart; // in minutes
    private int timeMarginStop; // in minutes
    private boolean useVPS;
    private Action action = Action.CREATE_TIMER;
    private boolean useExtendedEpgInfo;
    private String extendedEpgInfo;
    private boolean avoidRepeats;
    private int allowedRepeats;
    private boolean useTitleForRepeatsCheck;
    private Boolean useSubtitleForRepeatsCheck; // null = use if available
    private boolean useDescriptionForRepeatsCheck;
    private int useExtendedEpgInfoForRepeatsCheck; // This entry is a bit field of the category IDs
    private int acceptRepeatsWithinDays;
    private int autoDeleteAfterDays;
    private int keepNumberOfRecordings;
    private int minutesBeforeSwitch; // for action SWITCH_TO_CHANNEL
    private int pauseIfNumRecordingsExist;
    private BlacklistMode blacklistMode;
    private List<Integer> blacklistIDs;
    private int fuzzySearchToleranceValue = 90;
    private boolean useInFavoritesMenu;
    private int searchTemplateId;


    public EpgsearchTimer()
    {
    }


    public enum ChannelMode
    {
        NO_CHANNEL,
        CHANNEL_RANGE,
        CHANNEL_GROUP,
        CHANNEL_FTA_ONLY
    }

    public enum SearchMode
    {
        /**
         * The whole term must appear as substring
         */
        WHOLE_TERM,

        /**
         *  all single terms must exist as substrings (delimiters are blank ',' ';' '|' '~')
         */
        ALL_TERMS,

        /**
         * At least one term must exist as substring (delimiters are blank ',' ';' '|' '~')
         */
        ANY_TERM,

        /**
         * Matches exactly
         */
        EXACT_MATCH,

        /**
         * Regular expression match
         */
        REGEXP_MATCH
    }

    public enum Action
    {
        CREATE_TIMER,
        ANNOUNCE_OSD,
        SWITCH_TO_CHANNEL,
        ANNOUNCE_OSD_AND_SWITCH,
        ANNOUNCE_EMAIL
    }

    public enum BlacklistMode
    {
        SELECTION,
        ALL
    }
}
