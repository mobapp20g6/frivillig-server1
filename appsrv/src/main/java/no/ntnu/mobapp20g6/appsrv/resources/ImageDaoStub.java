package no.ntnu.mobapp20g6.appsrv.resources;

import no.ntnu.mobapp20g6.appsrv.dao.ImageDao;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class ImageDaoStub implements ImageDao {

    public static final Long EXISTING_ID = 1L;
    public static final Long NOT_EXISTING_ID = 2L;
    public static final Long BAD_ID = null;

    @Override
    public Picture getImage(Long id) {
        if (EXISTING_ID.equals(id)) {
            return new Picture();
        }
        return null;
    }

    @Override
    public Task storeImage(Task t, FormDataMultiPart data) {
        return null;
    }
}
