package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ImageDaoStub implements ImageDao {

    public static final Long EXISTING_IMAGE_ID = 1L;
    public static final Long NOT_EXISTING_ID = 2L;
    public static final Long BAD_ID = null;
    public static final Long EXISTING_TASK_ID = 3L;
    public static final Long EXISTING_GROUP_ID = 4L;
    public static final FormDataMultiPart INCOMING_PHOTO = new FormDataMultiPart();
    public static final String EXISTING_IMAGE_NAME = "image";
    public static final int INCOMING_PHOTO_WIDTH_NOT_GIVEN = 0;
    public static final String NOT_EXISTING_IMAGE_NAME = "noImage";

    @Override
    public Task testStoreImage(FormDataMultiPart image) {
        return null;
    }

    @Override
    public Group setGroupLogo(Long groupId, FormDataMultiPart data) {
        Group fakeGroup = new Group();
        fakeGroup.setId(EXISTING_GROUP_ID);
        return fakeGroup;
    }

    @Override
    public Task setTaskImage(Long taskID, FormDataMultiPart data) {
        Task fakeTask = new Task();
        fakeTask.setId(EXISTING_TASK_ID);
        return fakeTask;
    }

    @Override
    public Picture getImage(String name) {
        if (EXISTING_IMAGE_NAME.equals(name))
            return new Picture();
        return null;
    }
}
