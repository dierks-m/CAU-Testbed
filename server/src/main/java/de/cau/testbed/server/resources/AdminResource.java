package de.cau.testbed.server.resources;

import de.cau.testbed.server.api.UserTemplate;
import de.cau.testbed.server.constants.UserType;
import de.cau.testbed.server.service.NodeService;
import de.cau.testbed.server.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {
    private final UserService userService;
    private final NodeService nodeService;

    public AdminResource(UserService userService, NodeService nodeService) {
        this.userService = userService;
        this.nodeService = nodeService;
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-user")
    @POST
    public Response createUser(UserTemplate template) {
        return Response.ok(userService.createUser(template.name, UserType.USER)).build();
    }

    @Path("get-node-status")
    @GET
    public Response getNodeStatus() {
        return Response.ok(nodeService.getNodeStatus()).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    @Path("create-custom-user")
    @POST
    public Response createCustomUser(UserTemplate template) {
        return Response.ok(userService.createUser(template.name, template.type)).build();
    }
}
