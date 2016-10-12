/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.wps.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Sum class.</p> A generic string key to double value
 * data structure to accumulate values.
 */
public class Sum {

    private LinkedHashMap<String, double[]> sums;

    /**
     * <p>Constructor for Sum.</p>
     */
    public Sum() {
        sums = new LinkedHashMap<String, double[]>();
    }

    /**
     * <p>add.</p>
     * Adds a value to the already stored value
     * corresponding to a key.
     *
     * @param key of the value.
     * @param value to add.
     */
    public void add(String key, double value) {
        double [] sum = sums.get(key);
        if (sum == null) {
            sum = new double[1];
            sums.put(key, sum);
        }
        sum[0] += value;
    }

    /**
     * <p>value.</p>
     * Returns the accumulated values corresponding to a key.
     *
     * @param key of the value.
     * @return the accumulated value.
     */
    public double value(String key) {
        double [] sum = sums.get(key);
        return sum != null ? sum[0] : Double.NaN;
    }

    /**
     * <p>toJSON.</p>
     * Builds a JSON representation of keys and their accumulated values.
     * @return the string representation.
     */
    public String toJSON() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, double[]> entry: sums.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append('"').append(entry.getKey()).append("\": ")
                .append(entry.getValue()[0]);
        }
        sb.append('}');
        return sb.toString();
    }
}
