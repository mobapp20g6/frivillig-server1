package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public interface ImageDao {

    Picture getImage(Long id);

    Task setImage(Long taskId, Long groupId, FormDataMultiPart data);

    Task testStoreImage(FormDataMultiPart image);
}
