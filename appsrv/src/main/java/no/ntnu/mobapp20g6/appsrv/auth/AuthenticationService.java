package no.ntnu.mobapp20g6.appsrv.auth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import io.jsonwebtoken.security.InvalidKeyException;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

import no.ntnu.mobapp20g6.appsrv.dao.UserDAO;
import no.ntnu.mobapp20g6.appsrv.model.User;
import no.ntnu.mobapp20g6.appsrv.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *  The authentication REST endpoint
 * @author nils
 */
@Path("auth")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Log
public class AuthenticationService {

	@Inject
	KeyService keyService;

	@Inject
	IdentityStoreHandler identityStoreHandler;

	@Inject
	@ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
	String issuer;

	@Resource(lookup = DatasourceProducer.JNDI_NAME)
	DataSource dataSource;

	@PersistenceContext
	EntityManager em;

	@Inject
	PasswordHash hasher;

	@Inject
	JsonWebToken principal;

	// Data class to find user
	@Inject
	UserDAO userDao;

	/**
	 * @param email email address of user logging in
	 * @param pwd password of user logging in
	 * @param request
	 * @return
	 */
	@POST
	@Path("login")
	public Response login(
			@FormParam("email") @NotBlank String email,
			@FormParam("pwd") @NotBlank String pwd,
			@Context HttpServletRequest request) {
		System.out.println("=== INVOKING REST-AUTH: LOGON ===");
		System.out.print("Query parameters: email:" + email + ", password:" + pwd);

		User exsistingUser = userDao.findUserByEmail(email);

		if (!(exsistingUser == null)) {
			UsernamePasswordCredential ucred
					= new UsernamePasswordCredential(exsistingUser.getId(), pwd);

			System.out.println("=== INVOKING REST-AUTH: LOGON ===");
			System.out.println("- Found user......................: " + exsistingUser.getId());
			System.out.println("- Found credentials...............: " + ucred.getCaller());

			CredentialValidationResult result
					= identityStoreHandler.validate(ucred);

			if (result.getStatus() == CredentialValidationResult.Status.VALID) {
				String token = issueToken(result.getCallerPrincipal().getName(),
						result.getCallerGroups(), request);

				System.out.println("=== INVOKING REST-AUTH: LOGON ===");
				System.out.println("- Logged on with ID...............: " + exsistingUser.getId());
				System.out.println();
				return Response
						.ok()
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.build();
			}
		}
		System.out.println("=== INVOKING REST-AUTH: LOGON ===");
		System.out.println("- Unable to logon..................: " + email);

		return Response.status(Response.Status.UNAUTHORIZED)
				.build();
	}

	/**
	 *  Get a new JWT token of the currently logged on user
	 */
	@GET
	@RolesAllowed(value = {RoleGroup.USER})
    @Path("renew")
	public Response updateToken(
			@Context SecurityContext sc,
			@Context HttpServletRequest request) {
		if (sc.isUserInRole(RoleGroup.USER)) {
			String oldtoken = request.getHeader("Authorization");

			String token = issueToken(principal.getName(),
					principal.getGroups(), request);
			return Response
					.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.build();
		} else {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}


	/**
     * Helper function to issue tokens
	 * @return A JWT token
	 */
	private String issueToken(String name, Set<String> groups, HttpServletRequest request) {
		try {
			Date now = new Date();
			Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
			JwtBuilder jb = Jwts.builder()
					.setHeaderParam("typ", "JWT")
					.setHeaderParam("kid", "abc-1234567890")
					.setSubject(name)
					.setId("a-123")
					//.setIssuer(issuer)
					.claim("iss", issuer)
					.setIssuedAt(now)
					.setExpiration(expiration)
					.claim("upn", name)
					.claim("groups", groups)
					.claim("aud", "aud")
					.claim("auth_time", now)
					.signWith(keyService.getPrivate());
			return jb.compact();
		} catch (InvalidKeyException t) {
			log.log(Level.SEVERE, "Failed to create token", t);
			throw new RuntimeException("Failed to create token", t);
		}
	}

	/**
	 * Helper function for creating a new user and building a HTTP response for use in REST APIs
	 * @param email email of user to create
	 * @param pwd passwd of user to create
	 * @param firstName first name of user to create
	 * @param lastName last name of user to create
	 * @return the result of the operation, either bad request due to input or success
	 */
	private Response buildCreatedUserResponse(String email, String pwd, String firstName, String lastName) {
		System.out.println("=== INVOKING REST-AUTH: CREATE USER ===");
		System.out.print("Query parameters: email:" + email + ", password:" + pwd);
		User createdUser = userDao.createUser(email, pwd, firstName, lastName);
		if (createdUser == null) {

			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {

			return Response.ok(createdUser).build();
		}
	}

	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserFormService(
			@FormParam("email") String email,
			@FormParam("pwd") String pwd,
			@FormParam("firstname") String firstN,
			@FormParam("lastname") String lastN) {
		return buildCreatedUserResponse(email, pwd, firstN, lastN);
	}

	/**
	 * @return
	 */
	@GET
	@Path("currentuser")
	@RolesAllowed(value = {RoleGroup.USER})
	@Produces(MediaType.APPLICATION_JSON)
	public User getCurrentUser() {
		return em.find(User.class,
				principal.getName());
	}

	/**
	 * @param email
	 * @param role
	 * @return
	 */
	@PUT
	@Path("addrole")
	@RolesAllowed(value = {RoleGroup.ADMIN})
	public Response addRole(@QueryParam("email") String email, @QueryParam("role") String role) {
		System.out.println("=== INVOKING REST-AUTH: ADD GROUP ===");
		System.out.print("Query parameters:");
		System.out.print("email:" + email);
		System.out.print(", role:" + role);
		User foundUser = userDao.findUserByEmail(email);
		if (foundUser != null) {
			if (!(userDao.addRoleGroup(foundUser, role, true) == null)) {
				return Response.ok().build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 * @param email
	 * @param role
	 * @return
	 */
	@PUT
	@Path("removerole")
	@RolesAllowed(value = {RoleGroup.ADMIN})
	public Response removeRole(@QueryParam("email") String email, @QueryParam("role") String role) {
		System.out.println("=== INVOKING REST-AUTH: REMOVE GROUP ===");
		System.out.print("Query parameters:" + "email:" + email + ", role:" + role);
		User foundUser = userDao.findUserByEmail(email);
		if (foundUser != null) {
			if (!(userDao.addRoleGroup(foundUser, role, false) == null)) {
				return Response.ok().build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 * @param emailAccess Email address of user to change password
	 * @param newPasswd New password
	 * @param oldPasswd Old Password (not required for admins)
	 * @param sc
	 * @return
	 */
	@PUT
	@Path("changepwd")
	@RolesAllowed(value = {RoleGroup.USER})
	public Response changePassword(
			@FormParam("email") String emailAccess,
			@FormParam("pwd") String newPasswd,
			@FormParam("oldpwd") String oldPasswd,
			@Context SecurityContext sc) {
		System.out.println("=== INVOKING REST-AUTH: CHANGE PASSWORD ===");
		System.out.print("Query parameters: email:" + emailAccess + ", role:" + newPasswd);

		if (emailAccess == null || newPasswd == null) {
			return Response.status(Response.Status.BAD_REQUEST).build();

		}
		User accessUser = userDao.findUserByEmail(emailAccess);
		if (accessUser == null) {
			System.out.println("- Access User.......................: " + "<No User>");
			System.out.println();
			return Response.status(Response.Status.FORBIDDEN).build();
		}


		String id = accessUser.getId();
		System.out.println("=== INVOKING REST-AUTH: CHANGE PASSWORD ===");
		System.out.println("- Access User.......................: " + id);

		Boolean authorizedToChange = false;

		// The user initiating the password change request (aka the caller)
		String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;

		Response.Status state = Response.Status.BAD_REQUEST;

		if ((newPasswd == null || newPasswd.length() < 6)) {
			log.log(Level.SEVERE, " #1 Failed to change password on u {0}", id);
			System.out.println("- Password unsatisfied..............: " + newPasswd);
			System.out.println();
		} else {
			// Admin rolegroup has permission to change password for other users
			if (sc.isUserInRole(RoleGroup.ADMIN)) {
				state = Response.Status.OK;
				authorizedToChange = true;

				// 1. Verify caller has RoleGroup.USER
				// 2. Verify caller IS SAME user as will change password to
				// 3, Verify that caller has entered old password (admins shall never req usr old passwd!)
			} else if (sc.isUserInRole(RoleGroup.USER) && authuser.compareToIgnoreCase(id) == 0  && oldPasswd != null) {
				CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(id, oldPasswd));

				switch (result.getStatus()) {
					case VALID:
						authorizedToChange = true;
						state = Response.Status.OK;
						System.out.println(" - OK");
						break;

					case INVALID:
						state = Response.Status.FORBIDDEN;
						System.out.println(" - Forbidden");
						break;

					case NOT_VALIDATED:
						//FIXME: Currently here seperate the return code from the others
						// The database or something went horribly wrong
						state = Response.Status.SERVICE_UNAVAILABLE;
						break;
				}
			} else {
				state = Response.Status.UNAUTHORIZED;
			}
		}

		if (authorizedToChange) {
			accessUser.setPassword(hasher.generate(newPasswd.toCharArray()));
			em.merge(accessUser);
			System.out.println("REST-AUTH: Password changed for user " + emailAccess);
			System.out.println("- Password updated..................: " + newPasswd);
			System.out.println();
			state = Response.Status.fromStatusCode(200);
		} else {
			System.out.println("REST-AUTH: ERROR password not changed for user " + emailAccess);
			System.out.println();

		}

		return Response.status(state).build();
	}
}
