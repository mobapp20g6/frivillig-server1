package no.ntnu.mobapp20g6.appsrv.config;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


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
