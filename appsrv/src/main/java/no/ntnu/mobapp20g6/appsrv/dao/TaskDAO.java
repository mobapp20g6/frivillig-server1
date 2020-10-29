package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Task;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        Query query = em.createNamedQuery(Task.FIND_TASK_BY_ID);
        query.setParameter("id", id);
        return (Task) query.getResultList().get(0);
    }
}
