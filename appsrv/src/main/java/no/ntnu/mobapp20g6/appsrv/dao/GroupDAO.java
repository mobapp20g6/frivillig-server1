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
import java.util.Collections;
import java.util.List;

@Stateless
public class GroupDAO {

    @Inject
    UserDAO userDAO;
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
            group = em.merge(group);
            em.flush();
            addUserToGroup(creator, creator, group);
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

    public boolean isUserOwnerOfGroup(User user, Group group) {
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

    /**
     * Add a user the a group.
     * @param groupOwner owner of the group.
     * @param userToBeAdded user to be added to the group.
     * @param group to add user to.
     * @return true if user was successfully added to the group.
     */
    public boolean addUserToGroup(User groupOwner, User userToBeAdded, Group group) {
        if(groupOwner != null && userToBeAdded != null && group != null) {
            em.refresh(group);
            if (groupOwner == group.getOwnerUser()) {
                if(!isUserInGroup(userToBeAdded, group)) {
                    userDAO.prepareUserForEdit(userToBeAdded);
                    userToBeAdded.setMemberOfGroup(group);
                    userDAO.saveUser(userToBeAdded);
                    //Returns true if user was successfully added to the group.
                    return isUserInGroup(userToBeAdded, group);
                } else {
                    System.out.println("User is already member of the group.");
                }
            } else {
                System.out.println("User is not owner of group!");
            }
        }
        //User was not added to group.
        return false;
    }

    /**
     * Checks if a user is in a group.
     * @param user to see if is in group.
     * @param group to see if user is in.
     * @return true if user is in group.
     */
    public boolean isUserInGroup(User user, Group group) {
        em.refresh(group);
        for (User userInGroup:group.getMemberUsers()) {
            if(userInGroup == user) {
                //User is in group.
                return true;
            }
        }
        //User is not in group.
        return false;
    }

    /**
     * Return all tasks which is associated with the group.
     * @param group to find all associated tasks in.
     * @return List of tasks or empty list if group is null.
     */
    public List<Task> getAllGroupTasks(Group group) {
        if (group != null) {
            em.refresh(group);
            return group.getAssociatedTasks();
        } else {
            return Collections.emptyList();
        }
    }

    public Group attachLocationToGroup(Group g, Location l, User u) {
        if (g != null && l != null) {
            if (isUserOwnerOfGroup(u,g)) {
                prepareGroupForEdit(g);
                g.setLocation(l);
                saveGroup(g);
                return g;
            }
        }
        return null;
    }

    public Location detatchLocationFromGroup(Group g, User u) {
        Location location = null;
        if (u != null && g != null) {
            if (isUserOwnerOfGroup(u, g)) {
                prepareGroupForEdit(g);
                location = g.getLocation();
                g.setLocation(null);
                saveGroup(g);
            }
        }
        return location;
    }
}
