/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.util;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>SymbolicStatement class.</p>
 * A wrapper to allow named variables ":name" in SQL statements.
 */
public class SymbolicStatement {

    private static Logger log = Logger.getLogger(
            SymbolicStatement.class.getCanonicalName());

    private static final Pattern VAR = Pattern.compile(":([a-zA-Z0-9_]+)");

    /** The original statement. */
    protected String statement;

    /** The compiled statement. */
    protected String compiled;

    /** The re-substition map names to indices. */
    protected Map<String, List<Integer>> positions;

    /**
     * Instance wraps a prepared statement.
     */
    public class Instance {

        /** TODO: Support more types. */

        /** The wrapped prepared statement. */
        protected PreparedStatement stmnt;

        public Instance(Connection connection) throws SQLException {
            stmnt = connection.prepareStatement(compiled);
        }

        /**
         * Closes the wrapped prepared statement.
         */
        public void close() {
            try {
                stmnt.close();
            } catch (SQLException sqle) {
                log.log(Level.SEVERE, "cannot close statement", sqle);
            }
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setInt setInt}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setInt(String key, int value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setInt(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setString setString}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setString(String key, String value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setString(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setObject setObject}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setObject(String key, Object value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setObject(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setArray setArray}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setArray(String key, Array value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setArray(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setTimestamp setTimestamp}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setTimestamp(String key, Timestamp value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setTimestamp(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setDouble setDouble}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setDouble(String key, double value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setDouble(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setLong setLong}.
         * @param key the key.
         * @param value the value.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setLong(String key, long value)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setLong(p, value);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setNull setNull}.
         * @param key the key.
         * @param sqlType the sqlType.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance setNull(String key, int sqlType)
        throws SQLException {
            List<Integer> pos = positions.get(key.toLowerCase());
            if (pos != null) {
                for (Integer p: pos) {
                    stmnt.setNull(p, sqlType);
                }
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#setObject setObject}
         * for a complete map.
         * @param map the map.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance set(Map<String, Object> map) throws SQLException {
            for (Map.Entry<String, Object> entry: map.entrySet()) {
                setObject(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#clearParameters clearParameters}.
         * @return this instance for parameter chaining.
         * @throws java.sql.SQLException if any.
         */
        public Instance clearParameters() throws SQLException {
            stmnt.clearParameters();
            return this;
        }

        /** Shims
         * {@link java.sql.PreparedStatement#execute execute}.
         * @throws java.sql.SQLException if any.
         * @return same as
         * {@link java.sql.PreparedStatement#execute execute}.
         */
        public boolean execute() throws SQLException {
            try {
                return stmnt.execute();
            } catch (SQLException sqle) {
                throw new SQLException(
                        "statement:" + SymbolicStatement.this.compiled,
                        sqle);
            }
        }

        /** Shims
         * {@link java.sql.PreparedStatement#executeQuery executeQuery}.
         * @throws java.sql.SQLException if any.
         * @return same as
         * {@link java.sql.PreparedStatement#executeQuery executeQuery}.
         */
        public ResultSet executeQuery() throws SQLException {
            try {
                return stmnt.executeQuery();
            } catch (SQLException sqle) {
                throw new SQLException(
                        "statement:" + SymbolicStatement.this.compiled,
                        sqle);
            }
        }

        /** Shims
         * {@link java.sql.PreparedStatement#executeUpdate executeUpdate}.
         * @throws java.sql.SQLException if any.
         * @return same as
         * {@link java.sql.PreparedStatement#executeUpdate executeUpdate}.
         */
        public int executeUpdate() throws SQLException {
            try {
                return stmnt.executeUpdate();
            } catch (SQLException sqle) {
                throw new SQLException(
                        "statement:" + SymbolicStatement.this.compiled,
                        sqle);
            }
        }

    } // class Instance

    /**
     * <p>Constructor for SymbolicStatement.</p>
     *
     * @param statement SQL statement with named variables.
     */
    public SymbolicStatement(String statement) {
        this.statement = statement;
        compile();
    }

    /**
     * <p>Getter for the field <code>statement</code>.</p>
     *
     * @return the original SQL statement with named variables.
     */
    public String getStatement() {
        return statement;
    }

    /**
     * <p>compile.</p>
     * Replaces named variables with numbers to be referenced later.
     */
    protected void compile() {
        positions = new HashMap<String, List<Integer>>();

        StringBuffer sb = new StringBuffer();
        Matcher m = VAR.matcher(statement);
        int index = 1;
        while (m.find()) {
            String key = m.group(1).toLowerCase();
            List<Integer> list = positions.get(key);
            if (list == null) {
                list = new ArrayList<Integer>();
                positions.put(key, list);
            }
            list.add(index++);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        compiled = sb.toString();
    }
}
