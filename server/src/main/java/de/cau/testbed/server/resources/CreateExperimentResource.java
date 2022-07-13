package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.api.ExperimentId;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/create-experiment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateExperimentResource {
    @POST
    public ExperimentId createExperiment(@Valid ExperimentTemplate experimentTemplate) {
        System.out.println("Posted: " + experimentTemplate + "!");
        return new ExperimentId("aBC");
    }
}
