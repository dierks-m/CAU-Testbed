package de.cau.testbed.server.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceStatus;
import de.cau.testbed.server.constants.KafkaConstants;

import java.util.Timer;
import java.util.TimerTask;


public class NodeStatusObject {
    @JsonProperty("id")
    private final String nodeId;

    @JsonProperty("status")
    private DeviceStatus status;

    private Timer nodeDeadTimer;

    public NodeStatusObject(String nodeId) {
        this.nodeId = nodeId;
        this.status = DeviceStatus.WAIT_FOR_INITIAL_CONTACT;
        createOnNoResponseTimer();
    }

    public void onHeartbeat(String heartbeatId) {
        if (!nodeId.equals(heartbeatId))
            return;

        this.status = DeviceStatus.ALIVE;
        nodeDeadTimer.cancel();
        createOnNoResponseTimer();
    }

    public DeviceStatus getStatus() {
        return status;
    }

    private void createOnNoResponseTimer() {
        final NodeStatusObject statusObject = this;

        this.nodeDeadTimer = new Timer(true);
        nodeDeadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                statusObject.status = DeviceStatus.DEAD;
            }
        }, KafkaConstants.HEARTBEAT_INTERVAL + 1_000);
    }
}