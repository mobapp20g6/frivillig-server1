package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ImageDaoStub implements ImageDao {

    public static final String EXISTING_IMAGE_ID = "image";
    public static final String NOT_EXISTING_IMAGE_ID = "noImage";
    public static final Long EXISTING_TASK_ID = 1L;
    public static final Long EXISTING_GROUP_ID = 3L;
    public static final FormDataMultiPart INCOMING_PHOTO = new FormDataMultiPart();
    public static final int INCOMING_PHOTO_WIDTH_NOT_GIVEN = 0;

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
    public Picture getImage(String id) {
        if (EXISTING_IMAGE_ID.equals(id))
            return new Picture();
        return null;
    }
}
