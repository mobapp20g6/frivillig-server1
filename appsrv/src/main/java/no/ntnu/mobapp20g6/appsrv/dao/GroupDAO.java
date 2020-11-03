package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GroupDAO {
    @PersistenceContext
    EntityManager em;

    public Group getGroupById(Long groupId) {
        return em.find(Group.class, groupId);
    }

    public List<Group> getAllGroups() {
        Query query = em.createNamedQuery(Group.FIND_ALL_GROUPS);
        return query.getResultList();
    }

    /**
     * Add a new group to the database.
     * @param title name of the group.
     * @param description of the group.
     * @param orgId Id of the origination.
     * @param creator of the group.
     * @return the group. Null if title is null or empty.
     */
    public Group addGroup(String title, String description, Long orgId, User creator) {
        Group group = createGroup(title, description, orgId, creator);
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
    private Group createGroup(String title, String description, Long orgId, User creator) {
        if(title == null || title.isEmpty()) {
            //Title can't be null or empty.
            return null;
        } else {
            return new Group(title, description, orgId, creator);
        }
    }

    private boolean isUserOwnerOfGroup(User user, Group group) {
        return user.equals(group.getOwnerUser());
    }

    /**
     * Update the group if the user is the owner.
     * @param newTitle new title of the group.
     * @param newDescription new description of the group.
     * @param groupToBeUpdated group to be updated.
     * @param updaterUser user trying to update group.
     * @return updated group if successful. Null if user is not owner or helping method fail.
     */
    public Group updateGroup(String newTitle, String newDescription, Group groupToBeUpdated, User updaterUser) {
        System.out.println("Trying to update group.");
        if(isUserOwnerOfGroup(updaterUser, groupToBeUpdated)) {
            prepareGroupForEdit(groupToBeUpdated);
            if(newTitle != null && !newTitle.isEmpty()) {
                groupToBeUpdated.setName(newTitle);
            }
            groupToBeUpdated.setDescription(newDescription);
            return saveGroup(groupToBeUpdated);
        } else {
            System.out.println("User is not owner of group!");
            return null;
        }
    }

    private void prepareGroupForEdit(Group group) {
        System.out.println("Group getting ready for edit.");
        if(group != null) {
            try {
                em.lock(group, LockModeType.PESSIMISTIC_WRITE);
            } catch (Exception e) {
                System.out.println("Exception in prepareGroupForEdit: " + e.getMessage());
            }
        }
    }

    /**
     * Merge and lock the database.
     * @param groupToSave group to be merged.
     * @return group if merge was successful else null.
     */
    private Group saveGroup(Group groupToSave) {
        System.out.println("Trying to save group.");
        if(groupToSave != null) {
            try {
                em.merge(groupToSave);
                em.lock(groupToSave, LockModeType.NONE);
                em.flush();
                return groupToSave;
            } catch (Exception e) {
                System.out.println("Exception in saveGroup: " + e.getMessage());
            }
        }
        return null;
    }
}
