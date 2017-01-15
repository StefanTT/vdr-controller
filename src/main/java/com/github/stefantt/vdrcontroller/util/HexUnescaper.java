package com.github.stefantt.vdrcontroller.util;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

/**
 * Translates escaped hex characters back to their character.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class HexUnescaper extends CharSequenceTranslator
{
    @Override
    public int translate(CharSequence input, int index, Writer out) throws IOException
    {
        if (input.charAt(index) == '#' && input.length() - index >= 3)
        {
            int high = valueOfHexDigit(input.charAt(index + 1));
            int low = valueOfHexDigit(input.charAt(index + 2));
            if (high >= 0 && low >= 0)
            {
                out.write(high * 16 + low);
                return 3;
            }
        }

        return 0;
    }

    /**
     * Convert a character to it's hex value.
     *
     * @param ch The char to convert
     * @return The hex value, -1 if the character is no valid hex digit
     */
    private int valueOfHexDigit(char ch)
    {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        return -1;
    }
}
