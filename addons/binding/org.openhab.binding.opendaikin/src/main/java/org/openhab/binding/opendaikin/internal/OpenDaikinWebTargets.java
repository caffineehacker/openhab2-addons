package org.openhab.binding.opendaikin.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.openhab.binding.opendaikin.internal.api.ControlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenDaikinWebTargets {

    private WebTarget base;
    private WebTarget setControlInfo;
    private WebTarget getControlInfo;
    private Logger logger = LoggerFactory.getLogger(OpenDaikinWebTargets.class);

    public OpenDaikinWebTargets(Client client, String ipAddress) {
        base = client.target("http://" + ipAddress);
        setControlInfo = base.path("aircon/set_control_info");
        getControlInfo = base.path("aircon/get_control_info");
    }

    public ControlInfo getControlParameters() {
        String response = invoke(getControlInfo.request().buildGet(), getControlInfo);
        return ControlInfo.parse(response);

    }

    private String invoke(Invocation invocation, WebTarget target) {
        Response response;
        synchronized (this) {
            response = invocation.invoke();
        }

        if (response.getStatus() != 200) {
            logger.error("Bridge returned {} while invoking {} : {}", response.getStatus(), target.getUri(),
                    response.readEntity(String.class));
            return null;
        } else if (!response.hasEntity()) {
            logger.error("Bridge returned null response while invoking {}", target.getUri());
            return null;
        }

        return response.readEntity(String.class);
    }
}
