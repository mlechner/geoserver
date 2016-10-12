/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.wps.process;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import de.intevation.wps.BfSProcess;

import de.intevation.wps.model.Calculation;

import de.intevation.pg.PGConn;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

/**
 * Reconstruct personal dose from track with waypoints by integrating
 * spatio-temporal dose rate data also considering reduction factors.
 */
@DescribeProcess(title = "DoseReconstructionWPS",
    description = "Calculates the dose.")
public class DoseReconstructionProcess implements BfSProcess {

    private static Logger log = Logger.getLogger(
            DoseReconstructionProcess.class.getCanonicalName());

    /**
     * Start the WPS process.
     *
     * @param route the track with the waypoints and attributes.
     * @param ageGroup the persons age group.
     * @return JSON-representation of dose per organ.
     * @param stadiumFoetus stadium of foetus if any.
     * @throws java.sql.SQLException if any.
     * @throws javax.naming.NamingException if any.
     */
    @DescribeResult(name = "result", description = "The calculated dose")
    public String execute(
        @DescribeParameter(name = "route",
            description = "Route that is defined by simple point features")
        SimpleFeatureCollection route,
        @DescribeParameter(name = "ageGroup",
            description = "The persons age group")
        String ageGroup,
        @DescribeParameter(name = "stadiumFoetus",
            description = "")
        String stadiumFoetus
    ) throws SQLException, NamingException {

        Calculation calc = new Calculation(
                ageGroup,
                stadiumFoetus);

        Connection conn = null;

        try {
            conn = PGConn.getConnection();
            return calc.iterateRoute(route, conn).toJSON();
        } catch (SQLException | NamingException ex) {
            log.log(Level.SEVERE, "SQL problem", ex);
            throw ex;
        } finally {
            PGConn.close(conn);
        }
    }
}
