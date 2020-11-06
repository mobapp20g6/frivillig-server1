package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.dao.GroupDAO;
import no.ntnu.mobapp20g6.appsrv.dao.TaskDAO;
import no.ntnu.mobapp20g6.appsrv.dao.UserDAO;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import no.ntnu.mobapp20g6.appsrv.model.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

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
            @FormParam("scheduledate") String scheduleDate,
            @FormParam("groupid") Long groupId) {
        Group taskGroup = null;
        System.out.println("Trying to create task with:" +
                "\nTitle: " + title + "\nDescription: " + description + "\nMax users: " + maxUsers +
                "\nSchedule date: " + scheduleDate + "\nGroup id: " + groupId);
        Date taskDate;
        try {
            taskDate = new SimpleDateFormat("dd/MM/yyyy").parse(scheduleDate);
        } catch (ParseException e) {
            System.out.println("Exception when trying to parse String to date:\n"+ e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(groupId != null) {
            taskGroup = groupDAO.getGroupById(groupId);
            //Returns FORBIDDEN if user is not a member of the group.
            if(taskGroup != null && !groupDAO.isUserInGroup(userDAO.findUserById(principal.getName()), taskGroup)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }
        if(groupId == null || taskGroup != null) {
            Task createdTask = taskDAO.addTask(userDAO.findUserById(principal.getName()),
                    title, description, maxUsers, taskDate, taskGroup);
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
                    return Response.status(Response.Status.FORBIDDEN).build();
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

    @PUT
    @Path("/updatetask")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response updateTask(
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("maxusers") Long maxUsers,
            @FormParam("scheduledate") String scheduleDate,
            @FormParam("groupid") Long groupId,
            @QueryParam("id") Long taskId) {
        System.out.println("Trying to update task with task id: " + taskId);
        Date taskDate;
        try {
            taskDate = new SimpleDateFormat("dd/MM/yyyy").parse(scheduleDate);
        } catch (ParseException e) {
            System.out.println("Exception when trying to parse String to date:\n"+ e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(taskId != null) {
            System.out.println("Trying to get task.");
            Task task = taskDAO.getTaskById(taskId);
            Group group = null;
            if(groupId != null) {
                System.out.println("Trying to get group.");
                group = groupDAO.getGroupById(groupId);
            }
            if (task != null) {
                System.out.println("Found task with title: " + task.getTitle());
                task = taskDAO.updateTask(userDAO.findUserById(principal.getName()), task, title,
                        description, maxUsers, taskDate, group);
                if (task != null) {
                    //Task was successfully changed.
                    return Response.ok(task).build();
                } else {
                    //User is not the owner of the task.
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            } else {
                //No task with taskId found.
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            //taskId was null.
            System.out.println("TaskId is null!");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/jointask")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response joinTask(
            @FormParam("id") Long taskId) {
        if(taskId != null) {
            Task task = taskDAO.getTaskById(taskId);
            if(task != null) {
                if(taskDAO.addUserToTask(userDAO.findUserById(principal.getName()), task)) {
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/listmytasks")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response listMyTasks(
            @FormParam("ownedtasks") boolean ownedTasks) {
        List<Task> taskList;
        if(ownedTasks) {
            //Listing tasks user made.
            taskList = taskDAO.getOwnedTasks(userDAO.findUserById(principal.getName()));
            if(taskList.isEmpty()) {
                System.out.println("User don't own any tasks or owner was null.");
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(taskList).build();
            }
        } else {
            //Listing tasks user are assigned.
            taskList = taskDAO.getAssignedTasks(userDAO.findUserById(principal.getName()));
            System.out.println("User id is: " + principal.getName());
            if(taskList.isEmpty()) {
                System.out.println("User are not assigned to any tasks or user was null.");
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(taskList).build();
            }
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
        Group group = groupDAO.addGroup(title, description, orgId, userDAO.findUserById(principal.getName()));
            if(group != null) {
                return Response.ok(group).build();
            } else {
                //Title is null or empty.
                return Response.status(Response.Status.BAD_REQUEST).build();
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

    @PUT
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
                    System.out.println("Group was successfully changed!");
                    return Response.ok(group).build();
                } else {
                    //User not owner of group.
                    return Response.status(Response.Status.FORBIDDEN).build();
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

    @POST
    @Path("/addusertogroup")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {RoleGroup.USER})
    public Response addUserToGroup(
            @FormParam("userid") String userId,
            @FormParam("groupid") Long groupId) {
        if(userId != null && groupId != null) {
            User userToBeAdded = userDAO.findUserById(userId);
            Group group = groupDAO.getGroupById(groupId);
            if(userToBeAdded != null && group != null) {
                if(groupDAO.addUserToGroup(userDAO.findUserById(principal.getName()), userToBeAdded, group)) {
                    return Response.ok().build();
                } else {
                    //This should only happen if user is already in group.
                    //But can also happen if user trying to add is not owner of group
                    //Or method failed to add user to group.
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            } else {
                System.out.println("User and/or group was not found:\n" +
                        "User id: " + userId + "\n" +
                        "Group id: " + groupId);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            System.out.println("userId or groupId was null.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
