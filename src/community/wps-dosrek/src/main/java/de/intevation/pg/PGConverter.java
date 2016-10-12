/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.pg;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.LineString;

/**
 * <p>PGConverter class.</p>
 *
 * A converter from SimpleFeature to PostGIS geometries.
 * Currently converts to JTS LineString, which can be passed to PostGIS as WKT.
 */
public class PGConverter {

    private PGConverter() {
    }

    /**
     * <p>class GeometryException</p>
     * Thrown if a conversion failed.
     * */
    public static class GeometryException extends Exception {
        public GeometryException() {
        }

        public GeometryException(String msg) {
            super(msg);
        }
    }

    /**
     * <p>findFirstLineString.</p>
     * Attempts to find a line string in SimpleFeature.
     *
     * @param sf a {@link org.opengis.feature.simple.SimpleFeature} object.
     * @return a {@link com.vividsolutions.jts.geom.LineString} object.
     * @throws de.intevation.pg.PGConverter$GeometryException if any.
     */
    public static final LineString findFirstLineString(SimpleFeature sf)
        throws GeometryException {
        for (Object attr: sf.getAttributes()) {
            if (attr instanceof LineString) {
                return (LineString)attr;
            }
        }
        throw new GeometryException(
                "Cannot find line string in simple feature");
    }


    /**
     * <p>convertToLineString.</p>
     * Attempts to convert a SimpleFeature to a jts line string.
     *
     * @param sf a {@link org.opengis.feature.simple.SimpleFeature} object.
     * @return a {@link com.vividsolutions.jts.geom.LineString} object.
     * @throws de.intevation.pg.PGConverter$GeometryException if any.
     */
    public static LineString convertToLineString(
            SimpleFeature sf) throws GeometryException {

        return findFirstLineString(sf);
    }

    /*
    public static org.postgis.LineString convertToLineString(
            SimpleFeature sf, int srid) throws GeometryException {

        LineString ls = findFirstLineString(sf);

        Coordinate [] coords = ls.getCoordinates();
        org.postgis.Point [] points = new org.postgis.Point[coords.length];

        for (int i = 0; i < coords.length; i++) {
            Coordinate c = coords[i];
            points[i] = new org.postgis.Point(c.x, c.y);
            points[i].setSrid(srid);
        }

        return new org.postgis.LineString(points);
    }
    */
}
