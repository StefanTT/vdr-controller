package com.github.stefantt.vdrcontroller.util;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;

/**
 * Collection of VDR utility functions for the epgsearch plugin.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public final class VdrEpgsearchUtils
{
    /**
     * Get the epgsearch string representation of the timer.
     *
     * @param timer The timer to convert
     * @return The epgsearch string entry
     */
    public static String toString(Searchtimer timer)
    {
        String[] fields = new String[54];

        fields[0] = Integer.toString(timer.getId());
        fields[1] = escape(timer.getSearch());

        Range<Integer> startTimeRange = timer.getStartTimeRange();
        if (startTimeRange == null)
        {
            fields[2] = "0";
        }
        else
        {
            fields[2] = "1";
            fields[3] = toHHMM(startTimeRange.getMinimum());
            fields[4] = toHHMM(startTimeRange.getMaximum());
        }

        String fromChannel = timer.getFromChannel();
        String toChannel = timer.getToChannel();
        String channelGroup = timer.getChannelGroup();
        if (fromChannel != null)
        {
            fields[5] = "1";
            if (toChannel == null)
                fields[6] = fromChannel;
            else fields[6] = fromChannel + '|' + toChannel;
        }
        else if (channelGroup != null)
        {
            fields[5] = "2";
            fields[6] = escape(channelGroup);
        }
        else
        {
            fields[5] = "0";
        }

        fields[7] = timer.isCaseSensitive() ? "1" : "0";
        fields[8] = searchModeStr(timer.getSearchMode());
        fields[9] = timer.isSearchTitle() ? "1" : "0";
        fields[10] = timer.isSearchSubtitle() ? "1" : "0";
        fields[11] = timer.isSearchDescription() ? "1" : "0";

        Range<Integer> durationRange = timer.getDurationRange();
        if (durationRange == null)
        {
            fields[12] = "0";
        }
        else
        {
            fields[12] = "1";
            fields[13] = Integer.toString(durationRange.getMinimum());
            fields[14] = Integer.toString(durationRange.getMaximum());
        }

        fields[15] = timer.isEnabled() ? "1" : "0";

        int weekdays = timer.getWeekdays();
        fields[16] = weekdays == 0 ? "0" : "1";
        fields[17] = Integer.toString(-weekdays);

        fields[18] = timer.isSeries() ? "1" : "0";
        fields[19] = escape(timer.getFolder());
        fields[20] = Integer.toString(timer.getPriority());
        fields[21] = Integer.toString(timer.getLifetime());
        fields[22] = Integer.toString(timer.getTimeMarginStart());
        fields[23] = Integer.toString(timer.getTimeMarginStop());
        fields[24] = "0"; // do not use VPS
        fields[25] = "0"; // action: create a timer
        fields[26] = "0"; // do not use extended EPG info

        fields[28] = timer.isAvoidRepeats() ? "1" : "0";
        fields[29] = "0"; // number of allowed repeats
        fields[30] = timer.isRepeatsCompareTitle() ? "1" : "0";
        fields[31] = timer.isRepeatsCompareSubtitle() ? "1" : "0";
        fields[32] = timer.isRepeatsCompareDescription() ? "1" : "0";
        fields[33] = "0"; // repeats: do not compare extended EPG info
        fields[34] = "0"; // accept repeats within x days
        fields[35] = Integer.toString(timer.getDeleteAfterDays());
        fields[36] = Integer.toString(timer.getKeepRecordings());

        fields[40] = "0"; // do not use blacklists

        fields[49] = "0"; // do not ignore missing EPG categories
        fields[50] = "0"; // do not unmute sound when switching channel
        fields[51] = Integer.toString(timer.getRepeatsMatchDescriptionPercent());

        return StringUtils.join(fields, ':');
    }

    private static String escape(String str)
    {
        if (str == null || str == "")
            return "";

        return str.replace("|", "!^pipe^!").replace(':', '|');
    }

    private static String searchModeStr(Searchtimer.SearchMode mode)
    {
        switch (mode)
        {
            case SUBSTRING:
                return "0";
            case ALL_WORDS:
                return "1";
            case ANY_WORD:
                return "2";
            case EXACT:
                return "3";
        }
        throw new RuntimeException("Unsupported search mode encountered: " + mode);
    }

    /**
     * Convert minutes since midnight to a string in the format HHMM (hours + minutes).
     *
     * @param min The minutes since midnight
     * @return The minutes as time in the format HHMM
     */
    public static String toHHMM(int min)
    {
        return String.format("%02d%02d", min / 60, min % 60);
    }

    private VdrEpgsearchUtils()
    {
    }
}
