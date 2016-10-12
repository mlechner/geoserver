/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Statements class.</p>
 * A resolver to get symbolic statements from a properties file
 * being in the resources.
 */
public class Statements {

    private static Logger log = Logger.getLogger(
            Statements.class.getCanonicalName());

    private static final String RESOURCE_PATH = "/sql/statements.properties";

    /** Constant <code>INSTANCE</code>. */
    public static final Statements INSTANCE = new Statements();

    private Map<String, SymbolicStatement> statements;

    private Statements() {
        statements = loadStatements();
    }

    /**
     * <p>getStatement.</p>
     *
     * @param key the key of the statement to look for,
     * @param conn a database connection to compile a prepared statement.
     * @return a {@link de.intevation.util.SymbolicStatement.Instance} object.
     * @throws java.sql.SQLException if any.
     */
    public SymbolicStatement.Instance getStatement(
            String key,
            Connection conn
    ) throws SQLException {
        SymbolicStatement ss = statements.get(key);
        if (ss == null) {
            throw new SQLException("no such statement: \"" + key + "\"");
        }
        return ss.new Instance(conn);
    }

    private Map<String, SymbolicStatement> loadStatements() {
        Map<String, SymbolicStatement> stmnts =
            new HashMap<String, SymbolicStatement>();

        Properties properties = loadProperties();

        for (Enumeration<?> e = properties.propertyNames();
                e.hasMoreElements();) {
            String key = (String)e.nextElement();
            String value = properties.getProperty(key);
            SymbolicStatement symbolic = new SymbolicStatement(value);
            stmnts.put(key, symbolic);
        }

        return stmnts;
    }

    /**
     * <p>loadProperties.</p>
     * Loads the SQL properties file from the resources.
     *
     * @return a {@link java.util.Properties} object.
     */
    protected Properties loadProperties() {
        Properties common = new Properties();

        InputStream in = Statements.class.getResourceAsStream(RESOURCE_PATH);

        if (in != null) {
            try {
                common.load(in);
            } catch (IOException ioe) {
                log.log(Level.SEVERE, "cannot load defaults: "
                        + RESOURCE_PATH, ioe);
            } finally {
                try {
                    in.close();
                } catch (IOException ioe) {
                    // Ignore me!
                }
            }
        } else {
            log.log(Level.WARNING, "cannot find: " + RESOURCE_PATH);
        }

        return common;
    }
}
