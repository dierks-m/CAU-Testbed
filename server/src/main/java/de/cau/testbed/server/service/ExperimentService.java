package de.cau.testbed.server.service;

import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.HardwareNode;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.datastore.yaml.YAMLExperimentDescriptor;
import de.cau.testbed.server.config.exception.TimeCollisionException;
import de.cau.testbed.server.config.exception.UnknownModuleException;
import de.cau.testbed.server.config.exception.UnknownNodeException;
import de.cau.testbed.server.config.experiment.*;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.util.List;

public class ExperimentService {
    private final Database database;
    private final List<HardwareNode> availableNodes;

    public ExperimentService(Database database, List<HardwareNode> availableNodes) {
        this.database = database;
        this.availableNodes = availableNodes;
    }

    public long createNewExperiment(ExperimentTemplate template) throws TimeCollisionException, UnknownNodeException, UnknownModuleException {
        checkTimeCollision(template);
        checkModules(template);

        final ExperimentDescriptor experiment = database.addExperiment(template);

        return experiment.getId();
    }

    private void checkTimeCollision(ExperimentTemplate template) throws TimeCollisionException {
        if (!database.getExperimentsInTimeFrame(template.start.minusMinutes(5), template.end.plusMinutes(5)).isEmpty())
            throw new TimeCollisionException("Experiment's start or end time is too close to another experiment");
    }

    private void checkModules(ExperimentTemplate template) throws UnknownModuleException, UnknownNodeException {
        for (ExperimentNode node : template.nodes) {
            assertHardwareNodeExists(node);
        }
    }

    private void assertHardwareNodeExists(ExperimentNode node) throws UnknownModuleException, UnknownNodeException {
        for (HardwareNode hardwareNode : availableNodes) {
            if (hardwareNode.id.equals(node.id)) {
                for (ExperimentModule module : node.modules) {
                    if (!hardwareNode.capabilities.contains(module.moduleType))
                        throw new UnknownModuleException("Module type " + module.moduleType + " is not supported by " + hardwareNode.id);
                    else
                        return;
                }
            }
        }

        throw new UnknownNodeException("No node called " + node.id + " exists");
    }
}
