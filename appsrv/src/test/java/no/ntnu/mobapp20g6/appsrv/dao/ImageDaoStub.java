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

    @Override
    public Picture getImage(Long id) {
        if (EXISTING_IMAGE_ID.equals(id)) {
            return new Picture();
        }
        return null;
    }

    @Override
    public Task setImage(Long t, Long g, FormDataMultiPart data) {
        Task fakeTask = new Task();
        fakeTask.setId(EXISTING_TASK_ID);
        return fakeTask;
    }

    @Override
    public Task testStoreImage(FormDataMultiPart image) {
        return null;
    }
}
