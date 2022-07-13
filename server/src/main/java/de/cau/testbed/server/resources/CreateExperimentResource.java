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

@Path("/create-experiment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateExperimentResource {
    private final ExperimentService service;

    public CreateExperimentResource(ExperimentService service) {
        this.service = service;
    }
    @POST
    public Response createExperiment(@Valid ExperimentTemplate experimentTemplate) {
            return Response.ok(new ExperimentId(service.createNewExperiment(experimentTemplate))).build();
    }
}
