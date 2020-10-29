package no.ntnu.mobapp20g6.appsrv.config;

import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;
import no.ntnu.mobapp20g6.appsrv.resources.DatasourceProducer;
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;

/**
 *
 * @author nils
 */
@DatabaseIdentityStoreDefinition(
    dataSourceLookup= DatasourceProducer.JNDI_NAME,
    callerQuery="SELECT password FROM users WHERE user_id LIKE ?",
    groupsQuery="SELECT role_name FROM user_has_rolegroup WHERE user_id LIKE ?",
    hashAlgorithm = PasswordHash.class,
    priority = 80)
@DeclareRoles({RoleGroup.ADMIN, RoleGroup.USER})
@LoginConfig(authMethod = "MP-JWT",realmName = "template")
public class SecurityConfiguration {    
}
