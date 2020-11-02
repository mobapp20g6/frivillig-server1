package no.ntnu.mobapp20g6.appsrv.dao;

import lombok.extern.java.Log;
import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Picture;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import no.ntnu.mobapp20g6.appsrv.resources.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Log
@Stateless
@ImageDaoProducer
public class ImageDaoImpl implements ImageDao{

    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource ds;

    @PersistenceContext
    EntityManager em;

    @Inject
    @ConfigProperty(name = "image.storage.path", defaultValue = "images")
    String imagePath;

    private String getImagePath() {
        return imagePath;
    }

    public Picture getImage(Long id) {
        if (id == null) return null;

        Picture found = em.find(Picture.class, id);
        if (found == null) return null;

        em.refresh(found);
        return found;
    }

    public Task storeImage(Task task, Group group, FormDataMultiPart multiPart) {
        String path = imagePath;
        try {
            List<FormDataBodyPart> images = multiPart.getFields("image");
            if (images != null && task != null) {
                for (FormDataBodyPart imagePart : images) {
                    InputStream is = imagePart.getEntityAs(InputStream.class);
                    ContentDisposition meta = imagePart.getContentDisposition();

                    String iid = UUID.randomUUID().toString();
                    if (!(Files.exists(Paths.get(path)))) {
                        Files.createDirectory(Paths.get(path));
                    }
                    Long size = Files.copy(is, Paths.get(path, iid));

                    Picture picture = new Picture(iid, meta.getFileName(), size, meta.getType());
                    task.setPicture(picture);
                    em.persist(picture);
                    em.persist(task);
                    em.flush();
                    em.refresh(task);
                    return task;
                }
            }
        } catch (IOException ioe) {

        }
    return null;
    }
}
