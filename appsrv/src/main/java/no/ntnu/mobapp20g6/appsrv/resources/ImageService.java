package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoProducer;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.core.Response;


public class ImageService {

    private final ImageDao dao;
    @Inject
    public ImageService(@ImageDaoProducer ImageDao dao) {
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

    public Response storeImage(Task task, Group group, FormDataMultiPart image) {
        Task updatedTask = dao.storeImage(task, group, image);
        return Response.ok(updatedTask).build();
    }
    public Response testStoreImage(
            @FormDataParam("image") FormDataMultiPart image) {

        if (image == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Task updatedTask = dao.testStoreImage(image);
        return Response.ok(updatedTask).build();
    }
}
