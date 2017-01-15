package com.github.stefantt.vdrcontroller.entity;

/**
 * Types of VDR OSD items.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public enum VdrOsdItemType
{
    /**
     * The title of the OSD page.
     */
    TITLE,

    /**
     * An item.
     */
    ITEM,

    /**
     * An item, currently selected / focused.
     */
    ITEM_SEL,

    /**
     * The label for the blue button.
     */
    BTN_BLUE,

    /**
     * The label for the green button.
     */
    BTN_GREEN,

    /**
     * The label for the red button.
     */
    BTN_RED,

    /**
     * The label for the yellow button.
     */
    BTN_YELLOW,

    /**
     * A text message.
     */
    MESSAGE;

    /**
     * Get the item type for a type character as it is returned by the svdrposd plugin.
     *
     * @param typeChar The type character
     * @return The item type, null if the character is unknown
     */
    public static VdrOsdItemType valueOf(char typeChar)
    {
        switch (typeChar)
        {
        case 'B':
            return BTN_BLUE;
        case 'G':
            return BTN_GREEN;
        case 'I':
            return ITEM;
        case 'M':
            return MESSAGE;
        case 'R':
            return BTN_RED;
        case 'S':
            return ITEM_SEL;
        case 'T':
            return TITLE;
        case 'Y':
            return BTN_YELLOW;
        default:
            return null;
        }
    }
}
