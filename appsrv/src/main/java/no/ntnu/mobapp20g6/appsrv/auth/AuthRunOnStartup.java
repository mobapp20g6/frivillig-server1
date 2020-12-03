package no.ntnu.mobapp20g6.appsrv.auth;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.java.Log;
import no.ntnu.mobapp20g6.appsrv.dao.UserDAO;
import no.ntnu.mobapp20g6.appsrv.model.User;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *  This class sets up authentication rolesgroups and an admin user
 *  for administrating the server
 * @author nils
 */
@Singleton
@Startup
@Log
public class AuthRunOnStartup {

	@PersistenceContext
	EntityManager em;

	@Inject
	UserDAO userDao;

	@PostConstruct
	public void init() {
		long RoleGroups = (long) em.createQuery("SELECT count(g.name) from RoleGroup g").getSingleResult();
		System.out.println("Groups found " +  RoleGroups);
		if (RoleGroups == 0) {
			em.persist(new RoleGroup(RoleGroup.USER));
			em.persist(new RoleGroup(RoleGroup.ADMIN));

		}

		System.out.println(
			"PSQL-RESULT: Found "
			+ em.createQuery("SELECT count(g.name) from RoleGroup g")
				.getSingleResult() + " RoleGroups in DB");

		/**
		 *  Inject the microprofile config variables
		 */
		Config config = ConfigProvider.getConfig();
		String adminUser = config.getValue("userConfig.adminUser", String.class);
		String adminPassword = config.getValue("userConfig.adminPass", String.class);
		String adminFirstName = config.getValue("userConfig.adminFirstName", String.class);
		String adminLastName = config.getValue("userConfig.adminLastName", String.class);

		User admin = userDao.createUser(adminUser, adminPassword, adminFirstName, adminLastName);
		System.out.println("New email parsing");
		// TEST DUP USER
		//userBean.addRoleGroup(admin, "user", true);
		// TEST REMOVE USER
		//userBean.addRoleGroup(admin, "user", false);
		// TEST ADD USER
		//userBean.addRoleGroup(admin, "user", true);
		// TEST ADD ADMIN
		userDao.addRoleGroup(admin, "admin", true);
		userDao.getUserInfo(admin);
	}
}
