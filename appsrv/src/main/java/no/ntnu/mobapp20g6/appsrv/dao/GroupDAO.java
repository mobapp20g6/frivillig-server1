package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class GroupDAO {
    @PersistenceContext
    EntityManager em;

    public Group getGroupById(Long groupId) {
        Query query = em.createNamedQuery(Group.FIND_GROUP_BY_ID);
        query.setParameter("id", groupId);
        return (Group) query.getResultList().get(0);
    }
}
