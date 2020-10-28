package no.ntnu.mobapp20g6.appsrv.auth;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.java.Log;
import no.ntnu.mobapp20g6.appsrv.dao.UserDao;
import no.ntnu.mobapp20g6.appsrv.model.User;

/**
 *
 * @author mikael
 */
@Singleton
@Startup
@Log
public class AuthRunOnStartup {

	@PersistenceContext
	EntityManager em;

	@Inject
	UserDao userDao;

	@PostConstruct
	public void init() {
		long RoleGroups = (long) em.createQuery("SELECT count(g.name) from RoleGroup g").getSingleResult();
		if (RoleGroups == 0) {
			em.persist(new RoleGroup(RoleGroup.USER));
			em.persist(new RoleGroup(RoleGroup.ADMIN));

		}

		System.out.println(
			"PSQL-RESULT: Found "
			+ em.createQuery("SELECT count(g.name) from RoleGroup g")
				.getSingleResult() + " RoleGroups in DB");

		User admin = userDao.createUser("admin@admin.ad", "123456");
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
