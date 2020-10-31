package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class GroupDAO {
    @PersistenceContext
    EntityManager em;

    public Group getGroupById(Long groupId) {
        Query query = em.createNamedQuery(Group.FIND_GROUP_BY_ID);
        query.setParameter("id", groupId);
        return (Group) query.getResultList().get(0);
    }

    public List<Group> getAllGroups() {
        Query query = em.createNamedQuery(Group.FIND_ALL_GROUPS);
        return query.getResultList();
    }

    /**
     * Add a new group to the database.
     * @param title name of the group.
     * @param description of the group.
     * @param creator of the group.
     * @return the group. Null if title is null or empty.
     */
    public Group addGroup(String title, String description, User creator) {
        Group group = createGroup(title, description, creator);
        if(group != null) {
            em.merge(group);
            em.flush();
            return group;
        } else {
            return null;
        }
    }

    /**
     * Create a new group.
     * @param title name of the group.
     * @param description of the group.
     * @param creator of the group.
     * @return the new group. Null if title is null or empty.
     */
    private Group createGroup(String title, String description, User creator) {
        if(title == null || title.isEmpty()) {
            //Title can't be null or empty.
            return null;
        } else {
            return new Group(title, description, null, creator);
        }
    }
}
