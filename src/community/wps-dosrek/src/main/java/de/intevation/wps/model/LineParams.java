/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.wps.model;

import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.sql.Timestamp;

import org.opengis.feature.simple.SimpleFeature;

import de.intevation.util.StringUtils;

/**
 * <p>LineParams class.</p>
 * Parses the dose reconstruction data out of
 * a SimpleFeature and makes them available via getters.
 */
public class LineParams {

    private static Logger log = Logger.getLogger(
            LineParams.class.getCanonicalName());

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private static final String BEGIN = "begin";
    private static final String END = "end";
    private static final String RESIDENCE = "residence";
    private static final String PROTECTIONS = "actions";

    private Timestamp begin;
    private Timestamp end;
    private String residence;
    private String [] actions;

    /**
     * <p>Constructor for LineParams.</p>
     */
    public LineParams() {
    }

    /**
     * <p>Constructor for LineParams.</p>
     *
     * @param sf a SimpleFeature containing the track with waypoints
     * and relevant attributes.
     */
    public LineParams(SimpleFeature sf) {
        begin = extractTimestamp(sf, BEGIN);
        end = extractTimestamp(sf, END);
        residence = extractString(sf, RESIDENCE);
        actions = StringUtils.removeDuplicates(
                extractStringArray(sf, PROTECTIONS));
    }

    /**
     * <p>isValid.</p>
     * Returns true if all relevant data is valid and given
     * by the SimpleFeature.
     *
     * @return if the data is given and valid.
     */
    public boolean isValid() {
        return begin != null
            && end != null
            && residence != null;
    }

    /**
     * <p>Getter for the field <code>begin</code>.</p>
     *
     * @return the beginning of the dose reconstruction.
     */
    public Timestamp getBegin() {
        return begin;
    }

    /**
     * <p>Getter for the field <code>end</code>.</p>
     *
     * @return the end of the dose reconstruction.
     */
    public Timestamp getEnd() {
        return end;
    }

    /**
     * <p>Getter for the field <code>residence</code>.</p>
     *
     * @return the residence while following the path of the SimpleFeature.
     */
    public String getResidence() {
        return residence;
    }

    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return the active protection actions while following
     * the path of the SimpleFeature.
     */
    public String [] getActions() {
        return actions;
    }

    /**
     * <p>getActionsAsString.</p>
     *
     * @return same as getActions() joined by ",".
     */
    public String getActionsAsString() {
        return StringUtils.join(actions, ",");
    }

    private static String [] extractStringArray(SimpleFeature sf, String name) {
        Object o = sf.getAttribute(name);
        if (o == null) {
            return new String[0];
        }
        if (o instanceof String) {
            return StringUtils.splitTrim((String)o);
        }
        if (o instanceof String[]) {
            return (String[])o;
        }
        if (o instanceof List<?>) {
            return ((List<String>)o).toArray(new String[0]);
        }
        log.log(Level.WARNING, "string array \"" + name + "\" is invalid.");
        return new String[0];
    }

    private static String extractString(SimpleFeature sf, String name) {
        Object o = sf.getAttribute(name);
        if (o instanceof String) {
            return (String)o;
        }
        log.log(Level.WARNING, "string \"" + name + "\" not found.");
        return null;
    }

    private static Timestamp extractTimestamp(SimpleFeature sf, String name) {
        Object o = sf.getAttribute(name);
        if (!(o instanceof String)) {
            log.log(Level.WARNING, "date \"" + name + "\" not found.");
            return null;
        }
        try {
            return new Timestamp(DATE_FORMAT.parse((String)o).getTime());
        } catch (ParseException pe) {
            log.log(Level.WARNING, "not a date \"" + o + "\".", pe);
        }
        return null;
    }
}
