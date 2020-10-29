package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import no.ntnu.mobapp20g6.appsrv.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class TaskDAO {

    @PersistenceContext
    EntityManager em;

    @Inject
    GroupDAO groupDAO;

    public List<Task> getAllTasks() {
        Query query = em.createNamedQuery(Task.FIND_ALL_TASKS);
        return query.getResultList();
    }

    public Task getTaskById(Long id) {
        Query query = em.createNamedQuery(Task.FIND_TASK_BY_ID);
        query.setParameter("id", id);
        return (Task) query.getResultList().get(0);
    }

    /**
     * Add a new task to the database.
     * @param creator of the task.
     * @param title of the task.
     * @param description of the task.
     * @param maxUsers which can join th task
     * @param scheduleDate day the task is scheduled to be done.
     * @param groupId of group task should be associated with. If null group is public.
     * @return the new task. Null if no task is found with given groupId.
     */
    public Task addTask(User creator, String title, String description,
                        Long maxUsers, Date scheduleDate, Long groupId) {
        Task task = createTask(creator, title, description, maxUsers, scheduleDate, groupId);
        if(task != null) {
            em.merge(task);
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
     * @param groupId of group task should be associated with. If null group is public.
     * @return the new task. Null if no task is found with given groupId.
     */
    private Task createTask(User creator, String title, String description,
                        Long maxUsers, Date scheduleDate, Long groupId) {
        Group taskGroup = groupDAO.getGroupById(groupId);
        if(groupId == null) {
            //Group is public
            return new Task(title, description, scheduleDate,maxUsers, creator, null);
        } else {
            if(taskGroup == null) {
                //If no groups with id was found.
                return null;
            } else {
                return new Task(title, description, scheduleDate,maxUsers, creator, taskGroup);
            }
        }
    }
}
