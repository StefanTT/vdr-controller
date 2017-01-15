package com.github.stefantt.vdrcontroller.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.stefantt.vdrcontroller.entity.Named;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.vdr.VdrConstants;

/**
 * Collection of {@link VirtualFolder virtual folder} utility functions.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public final class FolderUtils
{
    /**
     * Get the folder path of the given file path.
     *
     * E.g. "SciFi~Star Trek~Azati Prime" would return "SciFi~Star Trek", "Azati Prime" would return
     * "".
     *
     * @param path The path to process
     * @return The folder path, empty of the given path contains no folder
     */
    public static String folderPath(String path)
    {
        int idx = path.lastIndexOf(VdrConstants.PATH_SEPARATOR);
        return idx < 0 ? "" : path.substring(0, idx);
    }

    /**
     * Get file name of the given file path.
     *
     * E.g. "SciFi~Star Trek~Azati Prime" would return "Azati Prime", "Azati Prime" would return
     * "Azati Prime".
     *
     * @param path The path to process
     * @return The file name
     */
    public static String fileName(String path)
    {
        int idx = path.lastIndexOf(VdrConstants.PATH_SEPARATOR);
        return idx < 0 ? path : path.substring(idx + 1);
    }

    /**
     * Find a folder by it's path.
     *
     * @param root The root folder
     * @param path The path to the folder
     * @return The folder, null if not found
     */
    public static <T extends Named> VirtualFolder<T> findFolder(VirtualFolder<T> root, String path)
    {
        VirtualFolder<T> folder = root;

        for (String name : path.split(Pattern.quote("" + VdrConstants.PATH_SEPARATOR)))
        {
            VirtualFolder<T> childFolder = folder.getFolder(name);
            if (childFolder == null)
                return null;

            folder = childFolder;
        }

        return folder;
    }

    /**
     * Find the parent of a folder by it's path.
     *
     * @param root The root folder
     * @param path The path to the folder
     * @return The parent folder, null if not found
     */
    public static <T extends Named> VirtualFolder<T> findParentFolder(VirtualFolder<T> root, String path)
    {
        int idx = path.lastIndexOf(VdrConstants.PATH_SEPARATOR);
        if (idx < 0) return root;
        return findFolder(root, path.substring(0, idx));
    }

    /**
     * Get the folder for the give path. If the folder or one of the parent folders does not exist,
     * then they are created.
     *
     * @param root The root folder
     * @param path The path to the folder
     * @param incRecCounters True to increment the records counters of the folders
     * @return The folder
     */
    public static <T extends Named> VirtualFolder<T> getOrCreateFolder(VirtualFolder<T> root, String path,
            boolean incRecCounters)
    {
        if (StringUtils.isEmpty(path))
            return root;

        VirtualFolder<T> folder = root;
        if (incRecCounters)
            folder.incNumRecordings();

        for (String name : path.split(Pattern.quote("" + VdrConstants.PATH_SEPARATOR)))
        {
            VirtualFolder<T> childFolder = folder.getFolder(name);
            if (childFolder == null)
            {
                childFolder = new VirtualFolder<T>(name, folder);
                folder.add(childFolder);
            }

            folder = childFolder;

            if (incRecCounters)
                folder.incNumRecordings();
        }

        return folder;
    }

    private FolderUtils()
    {
    }
}
