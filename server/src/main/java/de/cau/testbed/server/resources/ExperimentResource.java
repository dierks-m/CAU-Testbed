package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.ExperimentId;
import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.api.TimeFrame;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.service.ExperimentService;
import io.dropwizard.auth.Auth;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

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
    public Response scheduleExperiment(
            @Auth User user,
            @Valid ExperimentId experimentId
    ) {
        service.scheduleExperiment(experimentId.id, user);
        return Response.ok().build();
    }

    @Path("create-experiment")
    @POST
    public Response createExperiment(
            @Auth User user,
            @Valid ExperimentTemplate experimentTemplate
    ) {
        return Response.ok(new ExperimentId(service.createNewExperiment(experimentTemplate, user))).build();
    }

    @Path("list-experiments")
    @GET
    public Response listExperiments(
            TimeFrame timeframe
    ) {
        if (timeframe == null) {
            timeframe = new TimeFrame(LocalDateTime.now(), LocalDateTime.now().plusHours(12));
        }

        return Response.ok(service.listAnonymizedExperiments(timeframe.start, timeframe.end)).build();
    }
}