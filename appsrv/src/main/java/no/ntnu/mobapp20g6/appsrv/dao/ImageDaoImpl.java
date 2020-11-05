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
import java.util.logging.Level;

@Log
@Stateless
@ImageDaoProducer
public class ImageDaoImpl implements ImageDao{

    private static final String FORM_DATA_KEY = "image";

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

    public Task testStoreImage(FormDataMultiPart multiPart) {
        if (multiPart == null) {
            return null;
        }
        Task task = em.find(Task.class, multiPart);
        if (task == null) {
            return null;
        }
        List<FormDataBodyPart> images = multiPart.getFields(FORM_DATA_KEY);
        try {
            if (images != null) {
                for (FormDataBodyPart imagePart : images) {
                    InputStream is = imagePart.getEntityAs(InputStream.class);
                    ContentDisposition meta = imagePart.getContentDisposition();

                    String iid = UUID.randomUUID().toString();
                    if (!(Files.exists(Paths.get(getImagePath())))) {
                        Files.createDirectory(Paths.get(getImagePath()));
                    }
                    Long size = Files.copy(is, Paths.get(getImagePath(), iid));

                    Picture picture = new Picture(iid, meta.getFileName(), size, meta.getType());
                    task.setPicture(picture);
                    em.persist(picture);
                    em.flush();
                }
            }
        } catch (IOException ioe) {
            log.log(Level.INFO, ioe.getMessage());
        }
        return task;
    }

    @Override
    public Group setGroupLogo(Long groupId, FormDataMultiPart data) {
        if (groupId == null || data == null) {
            return null;
        }
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            return null;
        }
        List<FormDataBodyPart> logos = data.getFields(FORM_DATA_KEY);
        try {
            if (logos != null) {
                for (FormDataBodyPart logoPart : logos) {
                    InputStream is = logoPart.getEntityAs(InputStream.class);
                    ContentDisposition meta = logoPart.getContentDisposition();

                    String iid = UUID.randomUUID().toString();
                    if (!(Files.exists(Paths.get(getImagePath())))) {
                        Files.createDirectory(Paths.get(getImagePath()));
                    }
                    Long size = Files.copy(is, Paths.get(getImagePath(), iid));

                    Picture picture = new Picture(iid, meta.getFileName(), size, meta.getType());
                    group.setPicture(picture);
                    em.persist(picture);
                    em.flush();
                }
            }
        } catch (IOException ioe) {
            log.log(Level.INFO, ioe.getMessage());
        }
        return group;
    }

    @Override
    public Task setTaskImage(Long taskID, FormDataMultiPart data) {
        if (taskID == null || data == null) {
            return null;
        }
        Task task = em.find(Task.class, taskID);
        if (task == null) {
            return null;
        }
        List<FormDataBodyPart> images = data.getFields(FORM_DATA_KEY);
        try {
            if (images != null) {
                for (FormDataBodyPart imagePart : images) {
                    InputStream is = imagePart.getEntityAs(InputStream.class);
                    ContentDisposition meta = imagePart.getContentDisposition();

                    String iid = UUID.randomUUID().toString();
                    if (!(Files.exists(Paths.get(getImagePath())))) {
                        Files.createDirectory(Paths.get(getImagePath()));
                    }
                    Long size = Files.copy(is, Paths.get(getImagePath(), iid));

                    Picture picture = new Picture(iid, meta.getFileName(), size, meta.getType());
                    task.setPicture(picture);
                    em.persist(picture);
                    em.flush();
                }
            }
        } catch (IOException ioe) {
            log.log(Level.INFO, ioe.getMessage());
        }
        return task;
    }

}
