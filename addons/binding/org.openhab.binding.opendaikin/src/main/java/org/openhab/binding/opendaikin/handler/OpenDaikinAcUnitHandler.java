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
package org.openhab.binding.opendaikin.handler;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.opendaikin.OpenDaikinBindingConstants;
import org.openhab.binding.opendaikin.internal.OpenDaikinWebTargets;
import org.openhab.binding.opendaikin.internal.api.ControlInfo;
import org.openhab.binding.opendaikin.internal.config.OpenDaikinConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenDaikinAcUnitHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(OpenDaikinAcUnitHandler.class);

    private long refreshInterval;

    private final Client client = ClientBuilder.newClient();
    private OpenDaikinWebTargets webTargets;
    private ScheduledFuture<?> pollFuture;

    public OpenDaikinAcUnitHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        switch (channelUID.getId()) {
            case OpenDaikinBindingConstants.CHANNEL_AC_POWER:
                if (command instanceof OnOffType) {
                    changePower(((OnOffType) command).equals(OnOffType.ON));
                } else {
                    logger.warn("Received command of wrong type");
                }
                break;
            case OpenDaikinBindingConstants.CHANNEL_AC_TEMPC:
                if (command instanceof DecimalType) {
                    changeSetPointC(((DecimalType) command).doubleValue());
                }
                break;
            case OpenDaikinBindingConstants.CHANNEL_AC_TEMPF:
                if (command instanceof DecimalType) {
                    changeSetPointF(((DecimalType) command).doubleValue());
                }
                break;
        }

        poll();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing HDPowerView HUB");
        OpenDaikinConfiguration config = getConfigAs(OpenDaikinConfiguration.class);
        if (config.host == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Host address must be set");
        }
        webTargets = new OpenDaikinWebTargets(client, config.host);
        refreshInterval = config.refresh;

        schedulePoll();
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        stopPoll();
    }

    @Override
    public void dispose() {
        super.dispose();
        stopPoll();
    }

    void pollNow() {
        if (isInitialized()) {
            schedulePoll();
        }
    }

    private void schedulePoll() {
        if (pollFuture != null) {
            pollFuture.cancel(false);
        }
        logger.debug("Scheduling poll for 500ms out, then every {} ms", refreshInterval);
        pollFuture = scheduler.scheduleWithFixedDelay(pollingRunnable, 500, refreshInterval, TimeUnit.MILLISECONDS);
    }

    private synchronized void stopPoll() {
        if (pollFuture != null && !pollFuture.isCancelled()) {
            pollFuture.cancel(true);
            pollFuture = null;
        }
    }

    private synchronized void poll() {
        try {
            logger.debug("Polling for state");
            pollStatus();
        } catch (IOException e) {
            logger.debug("Could not connect to bridge", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, e.getMessage());
        } catch (Exception e) {
            logger.warn("Unexpected error connecting to bridge", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    private void pollStatus() throws IOException {
        ControlInfo controlInfo = webTargets.getControlParameters();
        updateStatus(ThingStatus.ONLINE);
        if (controlInfo != null) {
            updateState(OpenDaikinBindingConstants.CHANNEL_AC_POWER, controlInfo.power ? OnOffType.ON : OnOffType.OFF);
        }
    }

    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            poll();
        }

    };

    private void changePower(boolean power) {
        ControlInfo info = webTargets.getControlParameters();
        info.power = power;
        webTargets.setControlParameters(info);
    }

    private void changeSetPointC(double tempc) {
        ControlInfo info = webTargets.getControlParameters();
        info.temp = tempc;
        webTargets.setControlParameters(info);
    }

    private void changeSetPointF(double tempf) {
        changeSetPointC((tempf - 32.0) / 1.8);
    }
}
