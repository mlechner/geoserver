/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY!
 * See LICENSE.txt for details.
 */
package de.intevation.wps;

import org.geoserver.wps.jts.SpringBeanProcessFactory;

/**
 * Factory to look up processes in the application context.
 */
public class BfsProcessFactory extends SpringBeanProcessFactory {

    /**
     * <p>Constructor for BfsProcessFactory.</p>
     *
     * @param arg0 a {@link java.lang.String} object.
     * @param arg1 a {@link java.lang.String} object.
     * @param arg2 a {@link java.lang.Class} object.
     */
    public BfsProcessFactory(String arg0, String arg1, Class arg2) {
        super(arg0, arg1, arg2);
    }
}
