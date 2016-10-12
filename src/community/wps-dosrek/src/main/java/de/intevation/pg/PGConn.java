/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.pg;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

/**
 * <p>PGConn class.</p>
 * Looks up a database connection via JNDI.
 */
public class PGConn {

    private static final String CONTEXT_PATH =
        "java:/comp/env/jdbc/postgres_dosrek";

    private PGConn() {
    }

    /**
     * <p>getConnection.</p>
     * Attempts to get a database connection.
     *
     * @return a {@link java.sql.Connection} object.
     * @throws java.sql.SQLException if any.
     * @throws javax.naming.NamingException if any.
     */
    public static Connection getConnection()
        throws SQLException, NamingException {

        Context ctx = new InitialContext();
        DataSource ds = (DataSource)ctx.lookup(CONTEXT_PATH);
        if (ds == null) {
            return null;
        }

        Connection conn = ds.getConnection();

        /*
        PGConnection pgConn = conn.unwrap(PGConnection.class);
        pgConn.addDataType("geometry", PGgeometry.class);
        */

        return conn;
    }

    /**
     * <p>close.</p>
     * Close a database connection tolerantly.
     *
     * @param conn a {@link java.sql.Connection} object.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                 // Ignore me!
            }
        }
    }
}
