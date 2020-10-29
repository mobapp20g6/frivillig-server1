package no.ntnu.mobapp20g6.appsrv.resources;

import javax.ws.rs.core.Response;

public class ImageService {
    public Response getImage(Long id) {
        return Response.status(Response.Status.OK).build();
    }
}
