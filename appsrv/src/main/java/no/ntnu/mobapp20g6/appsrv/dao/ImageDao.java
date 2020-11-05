package no.ntnu.mobapp20g6.appsrv.dao;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public interface ImageDao {

    Task testStoreImage(FormDataMultiPart image);

    Group setGroupLogo(Long groupID, FormDataMultiPart data);

    Task setTaskImage(Long taskID, FormDataMultiPart data);

    Picture getImage(String id);
}
