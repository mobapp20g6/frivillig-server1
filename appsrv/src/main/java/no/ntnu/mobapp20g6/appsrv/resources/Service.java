package no.ntnu.mobapp20g6.appsrv.resources;

import com.sun.org.apache.xpath.internal.operations.Bool;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.GroupDAO;
import no.ntnu.mobapp20g6.appsrv.dao.TaskDAO;
import no.ntnu.mobapp20g6.appsrv.dao.UserDAO;
import no.ntnu.mobapp20g6.appsrv.model.Group;
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
    GroupDAO groupDAO;

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
        if(taskId != null) {
            Task task = taskDAO.getTaskById(taskId);
            if (task == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(task).build();
            }
        } else {
            //taskId is null.
            return Response.status(Response.Status.BAD_REQUEST).build();
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
        Group taskGroup = null;
        if(groupId != null) {
            taskGroup = groupDAO.getGroupById(groupId);
        }
        if(groupId == null || taskGroup != null) {
            Task createdTask = taskDAO.addTask(userDAO.findUserById(principal.getName()),
                    title, description, maxUsers, scheduleDate, taskGroup);
            if (createdTask != null) {
                return Response.ok(createdTask).build();
            } else {
                //Needed parameters missing or date is before today's date.
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } else {
            //No group with groupId found.
            System.out.println("No group with id: " + groupId + " found!");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/removetask")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response removeTask(
            @QueryParam("id") Long taskId) {
        if(taskId != null) {
            Task taskToBeRemoved = taskDAO.getTaskById(taskId);
            if(taskToBeRemoved != null) {
                boolean taskWasRemoved = taskDAO.removeTask(userDAO.findUserById(principal.getName()), taskToBeRemoved);
                if(taskWasRemoved) {
                    //Task was successfully removed.
                    return Response.status(Response.Status.OK).build();
                } else {
                    //User is not owner of task.
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } else {
                //No task with id found.
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            //taskId is null.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
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
        if(taskId != null) {
            Task task = taskDAO.getTaskById(taskId);
            Group group = groupDAO.getGroupById(groupId);
            if (task != null) {
                task = taskDAO.updateTask(userDAO.findUserById(principal.getName()), task, title,
                        description, maxUsers, scheduleDate, group);
                if (task != null) {
                    //Task was successfully changed.
                    return Response.ok(task).build();
                } else {
                    //User is not the owner of the task.
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } else {
                //No task with taskId found.
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            //taskId was null.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
