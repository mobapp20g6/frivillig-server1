package no.ntnu.mobapp20g6.appsrv.resources;

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
            return Response.ok(taskList).build();
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
        System.out.println("Trying to create task with:" +
                "\nTitle: " + title + "\nDescription: " + description + "\nMax users: " + maxUsers +
                "\nSchedule date: " + scheduleDate.toString() + "\nGroup id: " + groupId);
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

    @POST
    @Path("/creategroup")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response createGroup(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("orgid") Long orgId) {
        if(orgId != null) {
            //TODO make group with orgId.
            System.out.println("Trying to make group with orgId: " + orgId + ".");
            return null;
        } else {
            Group group = groupDAO.addGroup(title, description, userDAO.findUserById(principal.getName()));
            if(group != null) {
                return Response.ok(group).build();
            } else {
                //Title is null or empty.
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    @GET
    @Path("/listgroups")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response listGroups() {
        List<Group> groupList = groupDAO.getAllGroups();
        if(groupList == null || groupList.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(groupList).build();
        }
    }

    @POST
    @Path("/updategroup")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response updateGroup(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @QueryParam("id") Long groupId) {
        if(groupId != null) {
            Group group = groupDAO.getGroupById(groupId);
            if(group != null) {
                group = groupDAO.updateGroup(title, description, group, userDAO.findUserById(principal.getName()));
                if(group != null) {
                    //Group was successfully changed.
                    return Response.ok(group).build();
                } else {
                    //User not owner of group.
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } else {
                //Group not found.
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            //id was null.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
