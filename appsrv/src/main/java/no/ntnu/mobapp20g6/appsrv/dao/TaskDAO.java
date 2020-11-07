package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Location;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import no.ntnu.mobapp20g6.appsrv.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class TaskDAO {

    @PersistenceContext
    EntityManager em;

    public List<Task> getAllTasks() {
        Query query = em.createNamedQuery(Task.FIND_ALL_TASKS);
        return query.getResultList();
    }

    public Task getTaskById(Long id) {
        Task taskToFind = em.find(Task.class, id);
        if(taskToFind == null) {
            System.out.println("No task with id: " + id + " found.");
            return null;
        } else {
            System.out.println("Task with id: " + id + " found.\n" +
                    "Task title is: " + taskToFind.getTitle());
            return taskToFind;
        }
    }

    /**
     * Add a new task to the database.
     * @param creator of the task.
     * @param title of the task.
     * @param description of the task.
     * @param maxUsers which can join th task
     * @param scheduleDate day the task is scheduled to be done.
     * @param group the task should be associated with. If null group is public.
     * @return the new task. Null if no task is found with given groupId.
     */
    public Task addTask(User creator, String title, String description,
                        Long maxUsers, Timestamp scheduleDate, Group group) {
        Task task = createTask(creator, title, description, maxUsers, scheduleDate, group);
        if(task != null) {
            task = em.merge(task);
            em.flush();
            return task;
        } else {
            return null;
        }
    }

    /**
     * Create a new task.
     * @param creator of the task.
     * @param title of the task.
     * @param description of the task.
     * @param maxUsers which can join th task
     * @param scheduleDate day the task is scheduled to be done.
     * @param group the task should be associated with. If null group is public.
     * @return the new task. Null if title is missing or schedule date is before today's date.
     */
    private Task createTask(User creator, String title, String description,
                            Long maxUsers, Timestamp scheduleDate, Group group) {
        if((title == null || title.isEmpty())) { //  || (scheduleDate.before(Calendar.getInstance().getTime()))
            //A task must have title and schedule date which is after today's date.
            return null;
        } else {
            if (group == null) {
                //Group is public
                return new Task(title, description, scheduleDate, maxUsers, creator, null);
            } else {
                return new Task(title, description, scheduleDate, maxUsers, creator, group);
            }
        }
    }

    /**
     * Removes a task from the database.
     * @param removerUser user trying to remove the task.
     * @param taskToBeRemoved task to be removed.
     * @return true if task was successfully removed.
     */
    public boolean removeTask(User removerUser, Task taskToBeRemoved) {
        if(isUserOwnerOfTask(removerUser, taskToBeRemoved)) {
            em.remove(taskToBeRemoved);
            em.flush();
            taskToBeRemoved = getTaskById(taskToBeRemoved.getId());
            if(taskToBeRemoved == null) {
                //Task was successfully removed.
                return true;
            } else {
                System.out.println("Something went wrong with removeTask!\n Task with id: "
                + taskToBeRemoved.getId() + " was not removed.");
                return false;
            }
        } else {
            //User is not owner of task.
            System.out.println("This user is not the owner of the task with id: "
                    + taskToBeRemoved.getId() + " and it was not removed.");
            return false;
        }
    }

    private boolean isUserOwnerOfTask(User user, Task task) {
        return user.equals(task.getCreatorUser());
    }

    /**
     * Update the task if the user is the owner.
     * @param updaterUser user trying to update task.
     * @param taskToBeUpdated task to be updated.
     * @param newTitle new title of task.
     * @param newDescription new description of task.
     * @param newMaxUsers new participant limit on task.
     * @param newScheduleDate new schedule date.
     * @param newGroup new group the task is associated with.
     * @return Task if update was successful. Null if user is not owner or helping methods fail.
     */
    public Task updateTask(User updaterUser, Task taskToBeUpdated, String newTitle, String newDescription,
                           Long newMaxUsers, Timestamp newScheduleDate, Group newGroup) {
        System.out.println("Updating task");
        if(isUserOwnerOfTask(updaterUser, taskToBeUpdated)) {
            prepareTaskForEdit(taskToBeUpdated);
            if(newTitle != null && !newTitle.isEmpty()) {
                taskToBeUpdated.setTitle(newTitle);
            }
            if(newMaxUsers != null && newMaxUsers > 0) {
                taskToBeUpdated.setParticipantLimit(newMaxUsers);
            }
            if(newScheduleDate != null && newScheduleDate.after(Calendar.getInstance().getTime())) {
                taskToBeUpdated.setScheduleDate(newScheduleDate);
            }
            taskToBeUpdated.setDescription(newDescription);
            taskToBeUpdated.setAssociatedGroup(newGroup);
            return saveTask(taskToBeUpdated);
        } else {
            //User is not owner of task.
            return null;
        }
    }

    private void prepareTaskForEdit(Task task) {
        System.out.println("Task getting ready for edit.");
        if(task != null) {
            try {
                em.lock(task, LockModeType.PESSIMISTIC_WRITE);
            } catch (Exception e) {
                System.out.println("Exception in prepareTaskForEdit: " + e.getMessage());
            }
        }
    }

    /**
     * Merge and lock the database.
     * @param taskToSave task to be merged.
     * @return task if merge was successful else null.
     */
    private Task saveTask(Task taskToSave) {
        System.out.println("Trying to save task.");
        if(taskToSave != null) {
            try {
                em.merge(taskToSave);
                em.lock(taskToSave, LockModeType.NONE);
                em.flush();
                return taskToSave;
            } catch (Exception e) {
                System.out.println("Exception in saveTask: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Add a user to the task.
     * @param user to add to the task
     * @param task to add user too.
     * @return true if user was successfully added to task.
     */
    public boolean addUserToTask(User user, Task task) {
        boolean userWasAdded = false;
        if(task.getParticipantCount() >= task.getParticipantLimit()) {
            System.out.println("Task is full. So " + user.getFirstName() + " can't join " + task.getTitle() + ".");
        } else if(isUserInTask(user, task)) {
            System.out.println("User " + user.getFirstName() + " is already in task " + task.getTitle() + ".");
        } else {
            prepareTaskForEdit(task);
            task.getUsers().add(user);
            task.addToParticipantCount(1);
            if(saveTask(task) != null) {
                System.out.println("User was successfully added to task.");
                userWasAdded = true;
            } else {
                System.out.println("Something went wrong trying to save task!");
            }
        }
        return userWasAdded;
    }

    /**
     * Checks if a user is in a task.
     * @param user to check if already in the task.
     * @param task to check if user is in.
     * @return true if user is in task.
     */
    private boolean isUserInTask(User user, Task task) {
        for(User userInTask:task.getUsers()) {
            if(userInTask == user) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return all tasks user is the creator of.
     * @param user to get created tasks from.
     * @return tasks user has created.
     */
    public List<Task> getOwnedTasks(User user) {
        if(user != null) {
            em.refresh(user);
            return user.getCreatorOfTasks();
        } else {
            System.out.println("User is null in getOwnedTasks!");
            return Collections.emptyList();
        }
    }

    /**
     * Return all tasks user is assigned to.
     * @param user to get assigned tasks from.
     * @return tasks user is assigned to.
     */
    public List<Task> getAssignedTasks(User user) {
        if(user != null) {
            em.refresh(user);
            return user.getAssignedTasks();
        } else {
            System.out.println("User is null in getAssignedTasks!");
            return Collections.emptyList();
        }
    }

    public Task attachLocationToTask(Task t, Location l, User u) {
        if (t != null && l != null) {
            if (isUserOwnerOfTask(u, t)) {
                prepareTaskForEdit(t);
                t.setLocation(l);
                saveTask(t);
                return t;
            }
        }
        return null;
    }

    public Location detatchLocationFromTask(Task t, User u) {
        Location location = null;
        if (u != null && t != null) {
            if (isUserOwnerOfTask(u, t)) {
                prepareTaskForEdit(t);
                location = t.getLocation();
                t.setLocation(null);
                saveTask(t);
            }
        }
        return location;
    }
}
