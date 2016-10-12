/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.wps.model;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;

import org.opengis.feature.simple.SimpleFeature;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTWriter;

import de.intevation.pg.PGConverter;
import de.intevation.pg.PGConverter.GeometryException;

import de.intevation.util.Statements;
import de.intevation.util.SymbolicStatement;

/**
 * <p>Calculation class.</p>
 *
 * The calculation of the dose reconstruction.
 */
public class Calculation {

    private static Logger log = Logger.getLogger(
            Calculation.class.getCanonicalName());

    private static final String SET_SEARCH_PATH = "set.search.path";
    private static final String SELECT_SEGMENT = "select.segment";

    private String ageGroup;
    private String stadiumFoetus;

    /**
     * <p>Constructor for Calculation.</p>
     */
    public Calculation() {
    }

    /**
     * <p>Constructor for Calculation.</p>
     *
     * @param ageGroup the age group the simulated person is in.
     * @param stadiumFoetus the stadium of the foetus if any..
     */
    public Calculation(
            String ageGroup,
            String stadiumFoetus
    ) {
        this.ageGroup = ageGroup;
        this.stadiumFoetus = stadiumFoetus;
    }

    private void processLineSegment(
            LineString segment,
            LineParams params,
            Sum sum,
            SymbolicStatement.Instance selectSegment,
            Connection conn
    ) throws SQLException {

        selectSegment.clearParameters();

        String wkt = new WKTWriter().write(segment);

        selectSegment
            .setString("actions", params.getActionsAsString())
            .setString("geom", wkt)
            .setString("foetus", stadiumFoetus)
            .setString("age", ageGroup)
            .setString("residence", params.getResidence())
            .setTimestamp("begin", params.getBegin())
            .setTimestamp("end", params.getEnd());

        ResultSet rs = selectSegment.executeQuery();
        try {
            while (rs.next()) {
                String key = rs.getString(1);
                double value = rs.getDouble(2);
                sum.add(key, value);
            }
        } finally {
            rs.close();
        }
    }

    /**
     * <p>iterateRoute.</p>
     * Iterates the route of line segments and accumulates the dose
     * given the constraints.
     *
     * @param route the route to follow.
     * @param conn database connection to fetch the relevant data.
     * @return returns the accumulated dose splitted by organ.
     * @throws java.sql.SQLException if any.
     */
    public Sum iterateRoute(SimpleFeatureCollection route, Connection conn)
        throws SQLException {

        Sum sum = new Sum();

        SymbolicStatement.Instance setSearchPath =
            Statements.INSTANCE.getStatement(SET_SEARCH_PATH, conn);

        SymbolicStatement.Instance selectSegment =
            Statements.INSTANCE.getStatement(SELECT_SEGMENT, conn);

        try {
            setSearchPath.execute();

            SimpleFeatureIterator iterator = route.features();
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                LineParams params = new LineParams(feature);
                if (!params.isValid()) {
                    log.log(Level.WARNING,
                            "path segment params are invalid -> ignore.");
                    continue;
                }
                LineString segment;
                try {
                    segment = PGConverter.convertToLineString(feature);
                } catch (GeometryException ge) {
                    log.log(Level.WARNING,
                            "path segment geometry is invalid -> ignore.");
                    continue;
                }

                processLineSegment(
                        segment, params,
                        sum, selectSegment, conn);
            }
        } finally {
            selectSegment.close();
            setSearchPath.close();
        }
        return sum;
    }
}
