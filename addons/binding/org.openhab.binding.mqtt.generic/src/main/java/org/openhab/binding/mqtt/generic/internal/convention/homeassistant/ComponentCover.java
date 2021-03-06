/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mqtt.generic.internal.convention.homeassistant;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.generic.internal.values.RollershutterValue;

/**
 * A MQTT Cover component, following the https://www.home-assistant.io/components/cover.mqtt/ specification.
 *
 * Only Open/Close/Stop works so far.
 *
 * @author David Graeff - Initial contribution
 */
@NonNullByDefault
public class ComponentCover extends AbstractComponent<ComponentCover.ChannelConfiguration> {
    public static final String switchChannelID = "cover"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends BaseChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Cover");
        }

        protected @Nullable String state_topic;
        protected @Nullable String command_topic;
        protected String payload_open = "OPEN";
        protected String payload_close = "CLOSE";
        protected String payload_stop = "STOP";
    }

    public ComponentCover(CFactory.ComponentConfiguration builder) {
        super(builder, ChannelConfiguration.class);

        RollershutterValue value = new RollershutterValue(channelConfiguration.payload_open, channelConfiguration.payload_close,
                channelConfiguration.payload_stop);
        channels.put(switchChannelID, new CChannel(this, switchChannelID, value, //
                channelConfiguration.state_topic, channelConfiguration.command_topic, channelConfiguration.name, "", builder.getUpdateListener()));
    }
}
