package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.dao.ImageDaoStub;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.*;

@RunWith(Enclosed.class)
public class ImageServiceTest {


    @Nested
    class GivenGetImage {

        @Test
        void whenCorrectRequest_thenResponseIsOK() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(
                    ImageDaoStub.EXISTING_IMAGE_ID,
                    ImageDaoStub.INCOMING_PHOTO_WIDTH_NOT_GIVEN
            );
            assertEquals(
                    "HTTP Response should be 200",
                    Response.Status.OK.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenIDNotGiven_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(
                    null,
                    ImageDaoStub.INCOMING_PHOTO_WIDTH_NOT_GIVEN
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenPictureDoesNotExist_thenResponseIsNotFound() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.getImage(
                    ImageDaoStub.NOT_EXISTING_IMAGE_ID,
                    ImageDaoStub.INCOMING_PHOTO_WIDTH_NOT_GIVEN
            );
            assertEquals(
                    "HTTP Response should be 404",
                    Response.Status.NOT_FOUND.getStatusCode(),
                    response.getStatus()
            );
        }
    }

    @Nested
    class GivenSetTaskImage {
        @Test
        void whenCorrectRequest_thenResponseIsOk() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setTaskImage(
                    ImageDaoStub.EXISTING_TASK_ID,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should be 200",
                    Response.Status.OK.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenCorrectRequest_thenResponseContainsTask() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setTaskImage(
                    ImageDaoStub.EXISTING_TASK_ID,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should contain Task",
                    Task.class,
                    response.getEntity().getClass()
            );
        }

        @Test
        void whenMissingTaskID_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setTaskImage(
                    null,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenMissingImage_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setTaskImage(
                    ImageDaoStub.EXISTING_TASK_ID,
                    null
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }
    }

    @Nested
    class GivenSetGroupLogo {
        @Test
        void whenCorrectRequest_thenResponseIsOk() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setGroupLogo(
                    ImageDaoStub.EXISTING_GROUP_ID,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should be 200",
                    Response.Status.OK.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenCorrectRequest_thenResponseContainsGroup() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setGroupLogo(
                    ImageDaoStub.EXISTING_GROUP_ID,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should contain Group",
                    Group.class,
                    response.getEntity().getClass()
            );
        }

        @Test
        void whenMissingGroupID_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setGroupLogo(
                    null,
                    ImageDaoStub.INCOMING_PHOTO
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }

        @Test
        void whenMissingImage_thenResponseIsBadRequest() {
            ImageService imageService = new ImageService(new ImageDaoStub());
            Response response = imageService.setGroupLogo(
                    ImageDaoStub.EXISTING_GROUP_ID,
                    null
            );
            assertEquals(
                    "HTTP Response should be 400",
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    response.getStatus()
            );
        }


    }
}
