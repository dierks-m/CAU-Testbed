package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.UserTemplate;
import de.cau.testbed.server.constants.UserType;
import de.cau.testbed.server.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {
    private final UserService userService;

    public AdminResource(UserService userService) {
        this.userService = userService;
    }

    @Path("create-user")
    @POST
    public Response createUser(UserTemplate template) {
        return Response.ok(userService.createUser(template.name, UserType.USER)).build();
    }

    @RolesAllowed("ADMIN")
    @Path("create-custom-user")
    @POST
    public Response createCustomUser(UserTemplate template) {
        return Response.ok(userService.createUser(template.name, template.type)).build();
    }
}
