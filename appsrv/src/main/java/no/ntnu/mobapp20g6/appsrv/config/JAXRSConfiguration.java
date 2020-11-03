package no.ntnu.mobapp20g6.appsrv.config;

import org.eclipse.microprofile.auth.LoginConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@ApplicationPath("resources")
public class JAXRSConfiguration extends ResourceConfig {
    public JAXRSConfiguration() {

        packages(true,"no.ntnu.mobapp20g6.appsrv.resources")
                .register(MultiPartFeature.class);

    }

}
