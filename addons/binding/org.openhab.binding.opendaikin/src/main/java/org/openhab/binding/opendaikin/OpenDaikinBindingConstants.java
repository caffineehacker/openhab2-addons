/**
 * Copyright (c) 2014,2017 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.opendaikin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link OpenDaikinBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Tim Waterhouse <tim@timwaterhouse.com> - Initial contribution
 */
@NonNullByDefault
public class OpenDaikinBindingConstants {

    private static final String BINDING_ID = "opendaikin";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_AC_UNIT = new ThingTypeUID(BINDING_ID, "ac_unit");

    // List of all Channel ids
    public static final String CHANNEL_AC_TEMPC = "settempc";
    public static final String CHANNEL_AC_TEMPF = "settempf";
    public static final String CHANNEL_ROOM_TEMPC = "roomtempc";
    public static final String CHANNEL_ROOM_TEMPF = "roomtempf";
    public static final String CHANNEL_OUTSIDE_TEMPC = "outsidetempc";
    public static final String CHANNEL_OUTSIDE_TEMPF = "outsidetempf";
    public static final String CHANNEL_AC_POWER = "power";
    public static final String CHANNEL_AC_FAN_SPEED = "fanSpeed";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>();

    static {
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_AC_UNIT);
    }

}
