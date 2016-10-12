/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.util;

import java.util.ArrayList;

/**
 * <p>StringUtils class.</p>
 *
 * Helper for string manipulations.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * <p>splitTrim.</p>
     * Splits the given string by whitespace and comma
     * and trims the whitespace remains from the parts.
     *
     * @param s a string to be splitted.
     * @return an array of the splitted and trimmed parts.
     */
    public static String [] splitTrim(String s) {
        String [] parts = s.split(",\\s*");
        String [] tparts = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            tparts[i] = parts[i].trim();
        }
        return parts;
    }

    /**
     * <p>removeDuplicates.</p>
     * Removes the duplicates from an array of strings.
     *
     * @param s an array of strings which may contain duplicates.
     * @return an array of unique strings.
     */
    public static String [] removeDuplicates(String [] s) {
        ArrayList<String> unique = new ArrayList<String>(s.length);
        for (String t: s) {
            if (!unique.contains(t)) {
                unique.add(t);
            }
        }
        return unique.toArray(new String[unique.size()]);
    }

    /**
     * <p>join.</p>
     * Joins an array of strings with a separator between them.
     *
     * @param parts an array of strings to be joined.
     * @param sep the separator between the strings.
     * @return the joined string.
     */
    public static String join(String [] parts, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
