package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.TaskDAO;
import no.ntnu.mobapp20g6.appsrv.dao.UserDAO;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/service")
@Stateless
public class Service {

    @Inject
    TaskDAO taskDAO;

    @Inject
    UserDAO userDAO;

    @Inject
    JsonWebToken principal;

    @GET
    @Path("/listtasks")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
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
    @RolesAllowed(value = {RoleGroup.USER})
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
    @RolesAllowed(value = {RoleGroup.USER})
    public Response createTask(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("maxusers") Long maxUsers,
            @FormParam("scheduledate") Date scheduleDate,
            @FormParam("groupid") Long groupId) {
        if((title == null || title.isEmpty()) || (scheduleDate.before(Calendar.getInstance().getTime()))) {
            Task createdTask = taskDAO.addTask(userDAO.findUserById(principal.getName()),
                    title, description, maxUsers, scheduleDate, groupId);
            if (createdTask != null) {
                return Response.ok(createdTask).build();
            } else {
                //No group with groupId found.
                System.out.println("No group with id: " + groupId + " found!");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            //Needed parameters missing or date is before today's date.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/removetask")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response removeTask(
            @QueryParam("id") Long taskId) {
        return null;
    }

    @POST
    @Path("/updatetask")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
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
