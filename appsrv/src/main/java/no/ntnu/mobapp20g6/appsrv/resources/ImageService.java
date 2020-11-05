package no.ntnu.mobapp20g6.appsrv.resources;

import net.coobird.thumbnailator.Thumbnails;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoProducer;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("image")
@Stateless
public class ImageService {
    private final ImageDao dao;
    @Inject
    public ImageService(@ImageDaoProducer ImageDao dao) {
        this.dao = dao;
    }

    @Inject
    @ConfigProperty(name = "image.storage.path", defaultValue = "images")
    private String imagePath;

    private String getImagePath() {
        return imagePath;
    }

    @POST
    @Path("testsetimage")
    public Response testStoreImage(
            FormDataMultiPart image) {

        if (image == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Task updatedTask = dao.testStoreImage(image);
        return Response.ok(updatedTask).build();
    }

    @POST
    @Path("setgrouplogo")
    @RolesAllowed(value = {RoleGroup.USER})
    public Response setGroupLogo(
            @FormDataParam("groupid") Long groupID,
            @FormDataParam("image") FormDataMultiPart image) {
        if (groupID == null || image == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Group updatedGroup = dao.setGroupLogo(groupID, image);
        return Response.ok(updatedGroup).build();
    }

    @POST
    @Path("settaskimage")
    @RolesAllowed(value = {RoleGroup.USER})
    public Response setTaskImage(
            @FormDataParam("taskid")Long taskID,
            @FormDataParam("image") FormDataMultiPart image) {
        if (taskID == null || image == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Task updatedTask = dao.setTaskImage(taskID, image);
        return Response.ok(updatedTask).build();
    }

    @GET
    @Path("getimage")
    @Produces("image/jpeg")
    public Response getImage(@QueryParam("name") String id,
                             @QueryParam("width") int width) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Picture picture = dao.getImage(id);
        if (picture == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        StreamingOutput result = os -> {
            java.nio.file.Path image = Paths.get(getImagePath(), id);
            if (width == 0) {
                Files.copy(image, os);
                os.flush();
            } else {
                Thumbnails.of(image.toFile())
                          .size(width, width)
                          .outputFormat("jpeg")
                          .toOutputStream(os);
            }
        };
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        cc.setPrivate(true);

        return Response.ok(result).cacheControl(cc).build();
    }
}
