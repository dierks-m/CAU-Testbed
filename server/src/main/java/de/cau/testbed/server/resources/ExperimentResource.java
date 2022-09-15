package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.*;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.service.ExperimentService;
import io.dropwizard.auth.Auth;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
        try {
            service.scheduleExperiment(experimentId.id, user);
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @Path("create-experiment")
    @POST
    public Response createExperiment(
            @Auth User user,
            @Valid ExperimentTemplate experimentTemplate
    ) {
        try {
            return Response.ok(anonymizeExperimentInfo(service.createNewExperiment(experimentTemplate, user))).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @Path("queue-experiment")
    @POST
    public Response queueExperiment(
            @Auth User user,
            @Valid QueuedExperimentTemplate experimentTemplate
    ) {
        try {
            return Response.ok(anonymizeExperimentInfo(service.queueNewExperiment(experimentTemplate, user))).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @Path("stop-experiment")
    @POST
    public Response stopExperiment(
            @Auth User user,
            @Valid ExperimentId experimentId
    ) {
        try {
            return Response.ok(service.stopExperiment(experimentId.id, user)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @Path("get-results")
    @GET
    public Response getResults(
            @Auth User user,
            @Valid ExperimentId experimentId
    ) {
        try {
            final File resultsFile = service.createOrGetResultsFile(experimentId.id, user);
            return Response.ok(resultsFile).header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resultsFile.getName() + "\""
            ).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(e.getMessage())).build();
        }
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

    @Path("list-user-experiments")
    @GET
    public Response listUserExperiments(
            @Auth User user
    ) {
        return Response.ok(
                service.listUserExperiments(user).stream()
                        .map(ExperimentResource::anonymizeExperimentInfo)
                        .collect(Collectors.toList())
        ).build();
    }

    private static AnonymizedExperimentInfo anonymizeExperimentInfo(ExperimentDescriptor descriptor) {
        return new AnonymizedExperimentInfo(
                descriptor.getName(),
                descriptor.getStart(),
                descriptor.getEnd(),
                descriptor.getId(),
                descriptor.getStatus()
        );
    }
}
