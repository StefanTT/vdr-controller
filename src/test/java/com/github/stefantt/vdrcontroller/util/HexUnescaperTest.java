package com.github.stefantt.vdrcontroller.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link HexUnescaper}.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class HexUnescaperTest
{
    private HexUnescaper target = new HexUnescaper();

    @Test
    public void translate()
    {
        assertEquals("", target.translate(""));
        assertEquals("abcd", target.translate("abcd"));
        assertEquals(":", target.translate("#3A"));
        assertEquals("ab : cd", target.translate("ab #3a cd"));
        assertEquals(":ab", target.translate("#3Aab"));
        assertEquals("ab:", target.translate("ab#3A"));
        assertEquals("#3", target.translate("#3"));
        assertEquals("#", target.translate("#"));
        assertEquals("#xy", target.translate("#xy"));
        assertEquals("#3y", target.translate("#3y"));
        assertEquals("#:", target.translate("##3A"));
    }
}
