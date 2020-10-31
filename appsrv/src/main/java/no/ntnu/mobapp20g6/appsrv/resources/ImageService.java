package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.model.Picture;

import javax.ws.rs.core.Response;

public class ImageService {

    private ImageDao dao;
    public ImageService(ImageDao dao) {
        this.dao = dao;
    }

    public Response getImage(Long id) {
        if (id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Picture image = dao.getImage(id);
        if (image == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(image).build();
    }
}
