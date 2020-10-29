package no.ntnu.mobapp20g6.appsrv.config;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("resources")
public class JAXRSConfiguration extends Application {
    
}
