package no.ntnu.mobapp20g6.appsrv.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import no.ntnu.mobapp20g6.appsrv.model.Location;
import no.ntnu.mobapp20g6.appsrv.model.Task;

public class LocationDAO {

    @PersistenceContext
    EntityManager em;

    public Location getLocation(String uid) {
        Location found = null;
        if (uid != null) {
            found = em.find(Location.class, uid);
            if (found != null) {
                em.refresh(found);
            }
        }
        return found;
    }

    public Location createGpsLocation(String lat, String lon) {
        Location l = new Location(lat,lon);
        Location o = em.merge(l);
        System.out.println("DAO-LOC: Added GPS " + o.getId());
        return o;
    }


    public Location createAddressLocation(String street, String city, Long postCode, String country) {
        Location l = new Location(street, city, postCode, country);
        Location o = em.merge(l);
        System.out.println("DAO-LOC: Added Address " + o.getId());
        return o;
    }

}
