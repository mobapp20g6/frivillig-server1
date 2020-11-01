package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ImageDaoStub implements ImageDao {

    public static final Long EXISTING_ID = 1L;
    public static final Long NOT_EXISTING_ID = 2L;
    public static final Long BAD_ID = null;
    public static final Task EXISTING_TASK = new Task();
    public static final Group EXISTING_GROUP = new Group();
    public static final FormDataMultiPart INCOMING_PHOTO = new FormDataMultiPart();

    public ImageDaoStub() {
        EXISTING_TASK.setId(999L);
        EXISTING_GROUP.setId(999L);
    }

    @Override
    public Picture getImage(Long id) {
        if (EXISTING_ID.equals(id)) {
            return new Picture();
        }
        return null;
    }

    @Override
    public Task storeImage(Task t, Group g, FormDataMultiPart data) {
        return EXISTING_TASK;
    }
}
