package com.github.stefantt.vdrcontroller.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.entity.AutoTimer;
import com.github.stefantt.vdrcontroller.entity.AutoTimer.SearchMode;

/**
 * A parser for auto timers in epgsearch format.
 *
 * @see http://manpages.ubuntu.com/manpages/xenial/en/man5/epgsearch.conf.5.html
 *
 * @author Stefan Taferner
 */
public class EpgsearchAutoTimerParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EpgsearchAutoTimerParser.class);
    private static final Pattern PIPE_PATTERN = Pattern.compile("!^pipe^!");


    /**
     * Parse multiple lines of auto timers.
     *
     * @param lines The lines to parse
     * @return The list of autotimers
     */
    public List<AutoTimer> parse(String lines)
    {
        List<AutoTimer> timers = new ArrayList<>(128);

        StringTokenizer tokenizer = new StringTokenizer(lines, "\n");
        while (tokenizer.hasMoreTokens())
        {
            String line = tokenizer.nextToken();
            if (line.startsWith("End")) break;

            AutoTimer timer = parseLine(line);
            if (timer != null)
                timers.add(timer);
        }

        return timers;
    }

    /**
     * Parse a single auto timer line.
     *
     * @param line The line to parse
     * @return The parsed auto timer
     */
    public AutoTimer parseLine(String line)
    {
        if (line == null)
            return null;

        String[] fields = line.split(":");
        if (fields.length < 10) return null;

        AutoTimer timer = new AutoTimer();
        timer.setId(Integer.parseInt(fields[0]));
        timer.setSearch(unescape(fields[1]));
        timer.setSearchMode(searchMode(fields[8]));
        timer.setCaseSensitive("1".equals(fields[7]));
        timer.setSearchTitle("1".equals(fields[9]));
        timer.setSearchSubtitle("1".equals(fields[10]));
        timer.setSearchDescription("1".equals(fields[11]));

        String channelMode = fields[5];
        if ("1".equals(channelMode))
        {
            String[] channels = StringUtils.split(fields[6], '|');
            timer.setFromChannel(channels[0]);
            timer.setToChannel(channels.length > 1 ? channels[1] : channels[0]);
        }
        else if ("2".equals(channelMode))
        {
            timer.setChannelGroup(fields[6]);
        }
        else
        {
            LOGGER.warn("Ignoring unsupported epgsearch channel mode \"{}\"", channelMode);
        }

        if ("1".equals(fields[2]))
        {
            timer.setStartTimeRange(hhmmToMinutes(fields[3]), hhmmToMinutes(fields[4]));
        }

        if ("1".equals(fields[12]))
        {
            timer.setDurationRange(hhmmToMinutes(fields[13]), hhmmToMinutes(fields[14]));
        }

        // TODO fields[17] -> day of week

        timer.setSeries("1".equals(fields[18]));
        timer.setFolder(fields[19]);
        timer.setPriority(parseInt(fields[20], AutoTimer.DEFAULT_PRIORITY));
        timer.setLifetime(parseInt(fields[21], AutoTimer.DEFAULT_LIFETIME));
        timer.setTimeMarginStart(parseInt(fields[22], AutoTimer.DEFAULT_TIMEMARGIN_START));
        timer.setTimeMarginStop(parseInt(fields[23], AutoTimer.DEFAULT_TIMEMARGIN_STOP));

        timer.setAvoidRepeats("1".equals(fields[28]));
        timer.setRepeatsCompareTitle("1".equals(fields[30]));
        timer.setRepeatsCompareSubtitle(!"0".equals(fields[31]));
        if ("1".equals(fields[32]))
            timer.setRepeatsMatchDescriptionPercent(parseInt(fields[51], 90));

        return timer;
    }

    private static int parseInt(String str, int defaultValue)
    {
        if (str == null || str.length() == 0)
            return defaultValue;
        return Integer.parseInt(str);
    }

    private static String unescape(String str)
    {
        return PIPE_PATTERN.matcher(str.replace('|', ':')).replaceAll("|");
    }

    private static Integer hhmmToMinutes(String hhmm)
    {
        if (hhmm == null || hhmm.length() != 4)
            return null;

        return (hhmm.charAt(0) - '0') * 600
             + (hhmm.charAt(1) - '0') * 60
             + (hhmm.charAt(2) - '0') * 10
             + (hhmm.charAt(3) - '0');
    }

    private static SearchMode searchMode(String str)
    {
        switch (str)
        {
        case "0":
            return SearchMode.SUBSTRING;
        case "1":
            return SearchMode.ALL_WORDS;
        case "2":
            return SearchMode.ANY_WORD;
        case "3":
            return SearchMode.EXACT;
        default:
            LOGGER.warn("Ignoring unsupported epgsearch search mode \"{}\"", str);
            return null;
        }
    }
}
