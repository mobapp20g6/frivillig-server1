package no.ntnu.mobapp20g6.appsrv.resources;

import net.coobird.thumbnailator.Thumbnails;
import no.ntnu.mobapp20g6.appsrv.auth.AuthenticationService;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoProducer;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import no.ntnu.mobapp20g6.appsrv.model.User;
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

/**
 * The REST endpoint for handeling image operations
 * @author maardal
 */
@Path("image")
@Stateless
public class ImageService {
    private final ImageDao dao;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    @ConfigProperty(name = "image.storage.path", defaultValue = "images")
    private String imagePath;

    @Inject
    public ImageService(@ImageDaoProducer ImageDao dao) {
        this.dao = dao;
    }

    private String getImagePath() {
        return imagePath;
    }

    @POST
    @Path("setgrouplogo")
    @RolesAllowed(value = {RoleGroup.USER})
    public Response setGroupLogo(@FormDataParam("groupid") Long groupID,
                                 FormDataMultiPart image) {
        if (groupID == null || image == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Group updatedGroup = dao.setGroupLogo(groupID, image);
        if (updatedGroup == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User groupOwner = updatedGroup.getOwnerUser();
        if (groupOwner == null || groupOwner != authenticationService.getCurrentUser()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(updatedGroup).build();
    }

    @POST
    @Path("settaskimage")
    @RolesAllowed(value = {RoleGroup.USER})
    public Response setTaskImage(@FormDataParam("taskid") Long taskID,
                                 FormDataMultiPart image) {
        if (taskID == null || image == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Task updatedTask = dao.setTaskImage(taskID, image);
        if (updatedTask == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User taskOwner = updatedTask.getCreatorUser();
        if (taskOwner == null || taskOwner != authenticationService.getCurrentUser()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(updatedTask).build();
    }

    /**
     * Gets an image from data source. If width provided, the image
     * will be resized to the new width and its corresponding height,
     * keeping the same format.
     * Will also ask server to cache the image for 24 hours.
     * @param id the id of the image.
     * @param width the preferred width of the returned image.
     * @return Response containing the image if successful.
     */
    @GET
    @Path("getimage")
    @Produces("image/jpeg")
    @RolesAllowed(value = {RoleGroup.USER})
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
