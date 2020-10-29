package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.TaskDAO;
import no.ntnu.mobapp20g6.appsrv.model.Task;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/service")
@Stateless
public class Service {

    @Inject
    TaskDAO taskDAO;

    @GET
    @Path("/listtasks")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {RoleGroup.USER})
    public Response listTasks() {
        List<Task> taskList = taskDAO.getAllTasks();
        if(taskList == null || taskList.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(taskDAO.getAllTasks()).build();
        }
    }

    @GET
    @Path("/gettask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {RoleGroup.USER})
    public Response getTask(
            @QueryParam("id") Long taskId) {
        //TODO test what happens if QueryParam contains alphabetic
        Task task = taskDAO.getTaskById(taskId);
        if(task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(task).build();
        }
    }

    @POST
    @Path("/createtask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {RoleGroup.USER})
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
    //@RolesAllowed(value = {RoleGroup.USER})
    public Response removeTask(
            @QueryParam("id") Long taskId) {
        return null;
    }

    @POST
    @Path("/updatetask")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed(value = {RoleGroup.USER})
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
