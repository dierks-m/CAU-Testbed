package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.ExperimentId;
import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.service.ExperimentService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExperimentResource {
    private final ExperimentService service;

    public ExperimentResource(ExperimentService service) {
        this.service = service;
    }

    @Path("schedule-experiment")
    @POST
    public Response scheduleExperiment(@Valid ExperimentId experimentId) {
        service.scheduleExperiment(experimentId.id);
        return Response.ok().build();
    }

    @Path("create-experiment")
    @POST
    public Response createExperiment(@Valid ExperimentTemplate experimentTemplate) {
        return Response.ok(new ExperimentId(service.createNewExperiment(experimentTemplate))).build();
    }
}
