package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoStub;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import javax.ws.rs.core.Response;

class ImageServiceTest {

    @Test
    public void givenGetImage_whenExistingImageIDGiven_thenResponseIsOK() {
        ImageService imageService = new ImageService(new ImageDaoStub());
        Response response = imageService.getImage(ImageDaoStub.EXISTING_ID);
        assertEquals(
                "HTTP Response should be 200",
                Response.Status.OK.getStatusCode(),
                response.getStatus()
        );
    }

    @Test
    public void givenGetImage_whenWrongImageIDGiven_thenResponseIsNotFound() {
        ImageService imageService = new ImageService(new ImageDaoStub());
        Response response = imageService.getImage(ImageDaoStub.NOT_EXISTING_ID);
        assertEquals(
                "HTTP Response should be 404",
                Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus()
        );
    }

    @Test
    public void givenGetImage_whenIDisNull_thenResponseIsBadRequest() {
        ImageService imageService = new ImageService(new ImageDaoStub());
        Response response = imageService.getImage(ImageDaoStub.BAD_ID);
        assertEquals(
                "HTTP Response should be 400",
                Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus()
        );
    }

}
