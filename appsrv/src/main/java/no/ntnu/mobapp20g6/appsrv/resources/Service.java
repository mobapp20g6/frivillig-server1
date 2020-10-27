package no.ntnu.mobapp20g6.appsrv.resources;

import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/service")
@Stateless
public class Service {

    @GET
    @Path("/listtasks")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {Role.USER})
    public Response listTasks() {
        return null;
    }

    @GET
    @Path("/gettask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {Role.USER})
    public Response getTask(
            @QueryParam("id") String taskId) {
        return null;
    }

    @POST
    @Path("/createtask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {Role.USER})
    public Response createTask(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("maxusers") Long maxUsers,
            @FormParam("scheduledate") Date scheduleDate,
            @FormParam("groupid") Long groupId) {
        return null;
    }

    @DELETE
    @Path("/removetask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {Role.USER})
    public Response removeTask(
            @QueryParam("id") Long taskId) {
        return null;
    }

    @POST
    @Path("/updatetask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {Role.USER})
    public Response updateTask(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("maxusers") Long maxUsers,
            @FormParam("scheduledate") Date scheduleDate,
            @FormParam("groupid") Long groupId,
            @QueryParam("id") Long taskId) {
        return null;
    }
}
