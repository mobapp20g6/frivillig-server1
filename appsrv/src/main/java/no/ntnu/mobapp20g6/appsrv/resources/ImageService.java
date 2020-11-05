package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoProducer;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("image")
@Stateless
public class ImageService {

    private final ImageDao dao;
    @Inject
    public ImageService(@ImageDaoProducer ImageDao dao) {
        this.dao = dao;
    }

    @GET
    @Path("getimage")
    @RolesAllowed(value = {RoleGroup.USER})
    public Response getImage(@QueryParam("imageid") Long id) {
        if (id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Picture image = dao.getImage(id);
        if (image == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(image).build();
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
}
