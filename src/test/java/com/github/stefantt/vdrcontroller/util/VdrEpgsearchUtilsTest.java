package com.github.stefantt.vdrcontroller.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.entity.Searchtimer.SearchMode;
import com.github.stefantt.vdrcontroller.util.VdrEpgsearchUtils;

/**
 * Tests for {@link VdrEpgsearchUtils}.
 *
 * @author Stefan Taferner
 */
public class VdrEpgsearchUtilsTest
{
    @Test
    public void toHHMM()
    {
        assertEquals("0000", VdrEpgsearchUtils.toHHMM(0));
        assertEquals("0059", VdrEpgsearchUtils.toHHMM(59));
        assertEquals("0100", VdrEpgsearchUtils.toHHMM(60));
        assertEquals("0101", VdrEpgsearchUtils.toHHMM(61));
        assertEquals("2359", VdrEpgsearchUtils.toHHMM(1439));
    }

    @Test
    public void toString_defaults()
    {
        assertEquals("0::0:::0::0:1:0:0:0:0:::1:0:0:0::50:50:2:10:0:0:0::0:0:0:0:0:0:0:0:0::::0:::::::::0:0:90::",
            VdrEpgsearchUtils.toString(new Searchtimer()));
    }

    @Test
    public void toString_1()
    {
        Searchtimer t = new Searchtimer();
        t.setSearch("word1 word:2");
        t.setStartTimeRange(481, 1085);
        t.setFromChannel("102");
        t.setSearchMode(SearchMode.ALL_WORDS);
        t.setSearchTitle(true);
        t.setSeries(true);
        t.setPriority(74);
        t.setLifetime(48);
        t.setTimeMarginStart(5);
        t.setTimeMarginStop(27);
        t.setAvoidRepeats(true);
        t.setRepeatsCompareTitle(true);
        t.setRepeatsCompareSubtitle(true);
        t.setRepeatsMatchDescriptionPercent(75);
        t.setWeekdays(3); // sunday + monday

        assertEquals("0:word1 word|2:1:0801:1805:1:102:0:1:1:0:0:0:::1:1:-3:1::74:48:5:27:0:0:0::1:0:1:1:1:0:0:0:0::::0:::::::::0:0:75::",
            VdrEpgsearchUtils.toString(t));
    }

    @Test
    public void toString_2()
    {
        Searchtimer t = new Searchtimer();
        t.setSearch("search");
        t.setStartTimeRange(481, 1085);
        t.setFromChannel("11");
        t.setToChannel("17");
        t.setCaseSensitive(true);
        t.setSearchMode(SearchMode.ANY_WORD);
        t.setSearchSubtitle(true);
        t.setDurationRange(42, 174);
        t.setFolder("the:folder|here");
        t.setEnabled(false);
        t.setAvoidRepeats(false);
        t.setRepeatsCompareTitle(true);
        t.setRepeatsCompareSubtitle(true);
        t.setRepeatsMatchDescriptionPercent(72);

        assertEquals("0:search:1:0801:1805:1:11|17:1:2:0:1:0:1:42:174:0:0:0:0:the|folder!^pipe^!here:50:50:2:10:0:0:0::0:0:1:1:1:0:0:0:0::::0:::::::::0:0:72::",
            VdrEpgsearchUtils.toString(t));
    }

    @Test
    public void toString_3()
    {
        Searchtimer t = new Searchtimer();
        t.setSearch("search");
        t.setStartTimeRange(481, 1085);
        t.setChannelGroup("ch:Group");
        t.setSearchMode(SearchMode.EXACT);
        t.setSearchDescription(true);
        t.setWeekdays(4); // tuesday
        t.setDeleteAfterDays(12);
        t.setKeepRecordings(4);

        assertEquals("0:search:1:0801:1805:2:ch|Group:0:3:0:0:1:0:::1:1:-4:0::50:50:2:10:0:0:0::0:0:0:0:0:0:0:12:4::::0:::::::::0:0:90::",
            VdrEpgsearchUtils.toString(t));
    }
}
