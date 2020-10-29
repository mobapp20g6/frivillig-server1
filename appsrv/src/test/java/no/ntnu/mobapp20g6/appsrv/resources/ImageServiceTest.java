package no.ntnu.mobapp20g6.appsrv.resources;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import javax.ws.rs.core.Response;

class ImageServiceTest {

    @Test
    public void getImageWithID() {
        ImageService imgserv = new ImageService();
        Long id = Long.valueOf(1);
        Response response = imgserv.getImage(id);
        assertEquals("HTTP Response should be 200", Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
