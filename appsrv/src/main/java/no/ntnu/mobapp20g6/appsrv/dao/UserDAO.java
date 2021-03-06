/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.mobapp20g6.appsrv.dao;

import lombok.extern.java.Log;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.User;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nils
 */
@Stateless
@Log
public class UserDAO {

	/**
	 * The application server will inject a EntityManager as a way to
	 * communicate with the database via JPA.
	 */
	@PersistenceContext
	EntityManager em;

	@Inject
	PasswordHash hasher;

	public User findUserById(String id) {
		System.out.println("=== USER EJB: FIND USER ===");
		System.out.print("Query parameters: id:" + id);
		if (id == null) {
			return null;
		}
		User found = em.find(User.class, id);

		if (found == null) {
			System.out.println("- Status...........: " + "Not in database");
			System.out.println();
			log.info("Unable to find user in DB " + id);
			return null;
		} else {
			System.out.println("- Status.........: " + "In database");
			System.out.println("- Id.............: " + found.getId());
			System.out.println();
			log.info("Found user in DB " + id);
			return found;
		}
	}

	public User findUserByEmail(String email) {
		System.out.println("=== USER EJB: FIND USER ===");
		Query query = em.createNamedQuery(User.FIND_USER_BY_EMAIL);
		if (email == null) {
			return null;
		}
		email = email.toLowerCase();
		query.setParameter("email", email);
		System.out.print("Query parameters: mail:" + email);
		List<User> foundUsers = query.getResultList();
		if (foundUsers.size() == 1) {
			User u = foundUsers.get(0);
			System.out.println("- Status.........: " + "In database");
			System.out.println("- Id.............: " + u.getId());
			//System.out.println("- Password....: " + u.getPassword());	
			System.out.println();
			return u;
		} else {

			System.out.println("- Status...........: " + "Not in database");
			System.out.println();
			return null;
		}
	}

	/**
	 * Does an insert into the users and user_has_RoleGroup tables. It creates a
	 * SHA-256 hash of the password and Base64 encodes it before the u is
	 * created in the database. The authentication system will read the
	 * AUSER table when doing an authentication.
	 *
	 * @param email
	 * @param password
	 * @return
	 */
	public User createUser(String email, String password, String firstName, String lastName) {
		System.out.println("=== USER EJB: CREATE USER ===");
		System.out.print("Query parameters: mail:" + email
			+ ", pass:" + password);
		if (email == null || password == null) return null;
		email = email.toLowerCase();
		User u = findUserByEmail(email);

		if (u != null) {
			System.out.println("=== USER EJB: CREATE USER ===");
			System.out.println("- Id...............: " + u.getId());
			System.out.println("- Status...........: " + "Already Exist");
			//System.out.println("- Password....: " + u.getPassword());
			//log.log(Level.INFO, "User already exists {0}", email);
			System.out.println();
			return null;
		} else {
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(hasher.generate(password.toCharArray()));
			newUser.setFirstName(firstName);
			newUser.setLastName(lastName);
			try {
				RoleGroup userRoleGroup = em.find(RoleGroup.class,
						RoleGroup.USER);
				newUser.getRoleGroups().add(userRoleGroup);
				User created = em.merge(newUser);

				System.out.println("=== USER EJB: CREATE USER ===");
				System.out.println("- Status...........: " + "Created OK");
				System.out.println("- In database as id: " + created.getId());
				System.out.println("- UserRoleGroup(s).........: " + returnRoleGroupNames(created.getRoleGroups()));
				System.out.println();
				return created;
			} catch (ConstraintViolationException e) {
				return null;
			}
		}
	}

	public void getUserInfo(User user) {
		if (user != null) {
			user = em.find(User.class, user.getId());
			System.out.println("=== USER EJB: USERINFO ===");
			System.out.println("- In database as id: " + user.getId());
			System.out.println("- UserRoleGroup(s).........: " + returnRoleGroupNames(user.getRoleGroups()));
		}

	}

	public User addRoleGroup(User user, String roleGroup, boolean add) {
		RoleGroup roleGroupToChange = findRoleGroupByName(roleGroup);
		if (roleGroupToChange == null || user == null) {
			System.out.println("=== USER EJB: UserRoleGroup MGMT ===");
			System.out.println("- Status...........: " + "Parameters invalid");
			return null;
		} else {
			List<RoleGroup> currentRoleGroups = user.getRoleGroups();
			List<RoleGroup> predictedRoleGroups = new ArrayList<>(currentRoleGroups);
			String action = "add";
			if (add) {
				if (!(predictedRoleGroups.contains(roleGroupToChange))) {
					predictedRoleGroups.add(roleGroupToChange);
				}
			} else {
				action = "remove";
				if (predictedRoleGroups.contains(roleGroupToChange)) {
					predictedRoleGroups.remove(roleGroupToChange);
				}
			}

			System.out.println("=== USER EJB: UserRoleGroup MGMT ===");
			System.out.println("- User.............: " + user.getId());
			System.out.println("- Current RoleGroups...: " + returnRoleGroupNames(currentRoleGroups));
			System.out.println("- RoleGroups to " + action + ": " + roleGroupToChange.getName());
			System.out.println("- Predicted update...: " + returnRoleGroupNames(predictedRoleGroups));

			if (currentRoleGroups.equals(predictedRoleGroups)) {
				// NO UPDATE NESSECARY
				System.out.println("- Status...........: " + "NO CHANGE, SKIPPING");
				return user;
			} else {
				if (add) {
					currentRoleGroups.add(roleGroupToChange);
					System.out.println("- Action.............: " + "ADD");
				} else {
					currentRoleGroups.remove(roleGroupToChange);
					System.out.println("- Action.............: " + "REVOKE");

				}
				System.out.println("- Completed update...: " + returnRoleGroupNames(currentRoleGroups));
				em.flush();
				return user;
			}
		}
	}

	private String returnRoleGroupNames(List<RoleGroup> list) {
		if (list.isEmpty()) {
			return "<none>";
		}
		StringBuilder sb = new StringBuilder();
		for (RoleGroup element : list) {
			sb.append(element.getName());
			sb.append(" ");
		}
		return sb.toString();
	}

	private RoleGroup findRoleGroupByName(String name) {
		if (roleGroupExists(name)) {
			return em.find(RoleGroup.class, name);
		} else {
			return null;
		}
	}

	private boolean roleGroupExists(String input) {
		boolean result = false;
		if (input!= null) {
			switch (input) {
				case RoleGroup.ADMIN:
				case RoleGroup.USER:
					result = true;
					break;
				default:
					break;
			}
		}
		return result;
	}

	public void prepareUserForEdit(User user) {
		System.out.println("user getting ready for edit.");
		if(user != null) {
			try {
				em.lock(user, LockModeType.PESSIMISTIC_WRITE);
			} catch (Exception e) {
				System.out.println("Exception in prep user: " + e.getMessage());
			}
		}
	}

	/**
	 * Merge and lock the database.
	 * @param userToSave group to be merged.
	 * @return group if merge was successful else null.
	 */
	public User saveUser(User userToSave) {
		System.out.println("Trying to save u.");
		if(userToSave != null) {
			try {
				em.merge(userToSave);
				em.lock(userToSave, LockModeType.NONE);
				em.flush();
				return userToSave;
			} catch (Exception e) {
				System.out.println("Exception in save uer: " + e.getMessage());
			}
		}
		return null;
	}
}
