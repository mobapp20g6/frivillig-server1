package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoStub;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import javax.ws.rs.core.Response;

@RunWith(Enclosed.class)
public class ImageServiceTest {

    @Nested
    class GivenGetImage {
        @Test
        void whenExistingImageIDGiven_thenResponseIsOKAndContainsImage() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(ImageDaoStub.EXISTING_IMAGE_ID);
            assertEquals(
                    "HTTP Response should be 200",
                    Response.Status.OK.getStatusCode(),
                    response.getStatus()
            );
            assertEquals(
                    "HTTP Response should contain Image",
                    Picture.class,
                    response.getEntity().getClass()
            );
        }

        @Test
        void whenWrongImageIDGiven_thenResponseIsNotFound() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(ImageDaoStub.NOT_EXISTING_ID);
            assertEquals(
                    "HTTP Response should be 404",
                    Response.Status.NOT_FOUND.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenIDisNull_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(ImageDaoStub.BAD_ID);
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }
    }

    @Nested
    class GivenStoreImage {
        @Test
        void whenCorrectRequest_theResponseIsOKAndContainsTask() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.storeImage(
                    ImageDaoStub.EXISTING_TASK_ID,
                    ImageDaoStub.EXISTING_GROUP_ID,
                    ImageDaoStub.INCOMING_PHOTO);
            assertEquals(
                    "HTTP Response should be 200",
                    Response.Status.OK.getStatusCode(),
                    response.getStatus()
            );
            assertEquals(
                    "HTTP Response should contain Task",
                    Task.class,
                    response.getEntity().getClass()
            );
        }

        @Test
        void whenMissingImage_theResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.storeImage(
                    ImageDaoStub.EXISTING_TASK_ID,
                    ImageDaoStub.EXISTING_GROUP_ID,
                    null
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenMissingBothTaskAndGroup_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.storeImage(
                    null,
                    null,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus());
        }
    }
}
