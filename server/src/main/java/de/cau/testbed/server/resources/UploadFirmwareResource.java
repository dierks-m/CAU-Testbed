package de.cau.testbed.server.resources;

import de.cau.testbed.server.config.exception.FirmwareDoesNotExistException;
import de.cau.testbed.server.service.FirmwareService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.IOException;
import java.io.InputStream;

@Path("/upload-firmware")
public class UploadFirmwareResource {
    private final FirmwareService firmwareService;

    public UploadFirmwareResource(FirmwareService firmwareService) {
        this.firmwareService = firmwareService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFirmware(
            @FormDataParam("file") InputStream uploadInputStream,
            @FormDataParam("experimentId") long experimentId,
            @FormDataParam("name") String firmwareName) {
        try {
            firmwareService.writeFile(uploadInputStream, experimentId, firmwareName);
            return Response.ok().build();
        } catch (IOException e) {
            throw new FirmwareDoesNotExistException(e.getMessage());
        }
    }
}
