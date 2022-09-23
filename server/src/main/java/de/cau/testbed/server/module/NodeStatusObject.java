package de.cau.testbed.server.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceStatus;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Holds information about an individual node's status (ALIVE, DEAD, RECONNECT) and handles heartbeat messages.
 */
public class NodeStatusObject {
    private static final long INITIAL_CONTACT_TIME_MS = 120_000;

    @JsonProperty("id")
    private final String nodeId;
    private final int timeout;

    @JsonProperty("status")
    private DeviceStatus status;

    private Timer nodeDeadTimer;

    public NodeStatusObject(String nodeId, int timeout) {
        this.nodeId = nodeId;
        this.timeout = timeout;
        this.status = DeviceStatus.WAIT_FOR_INITIAL_CONTACT;

        // Kafka Metadata exchange can sometimes take quite some time. Wait a bit longer for initial contact
        createOnNoResponseTimer(INITIAL_CONTACT_TIME_MS);
    }

    public void onHeartbeat(String heartbeatId) {
        if (!nodeId.equals(heartbeatId))
            return;

        if (status.wasDead())
            status = DeviceStatus.RECONNECT;
        else
            status = DeviceStatus.ALIVE;

        nodeDeadTimer.cancel();
        createOnNoResponseTimer(timeout + 1_000); // Allow for some leeway with another second
    }

    public DeviceStatus getStatus() {
        return status;
    }

    private void createOnNoResponseTimer(long interval) {
        final NodeStatusObject statusObject = this;

        this.nodeDeadTimer = new Timer(true);
        nodeDeadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                statusObject.status = DeviceStatus.DEAD;
            }
        }, interval);
    }
}