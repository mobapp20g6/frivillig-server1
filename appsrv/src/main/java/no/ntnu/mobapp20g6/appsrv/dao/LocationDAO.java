package no.ntnu.mobapp20g6.appsrv.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import no.ntnu.mobapp20g6.appsrv.model.Group;
import no.ntnu.mobapp20g6.appsrv.model.Location;
import no.ntnu.mobapp20g6.appsrv.model.Task;
import org.eclipse.persistence.exceptions.DatabaseException;

public class LocationDAO {

    @PersistenceContext
    EntityManager em;

    @Inject
    TaskDAO tdao;

    @Inject
    GroupDAO gdao;

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
        try {
            Location l = new Location(lat,lon);
            Location o = em.merge(l);
            System.out.println("DAO-LOC: Added GPS " + o.getId());
            return o;
        } catch (Exception e) {
            return null;
        }
    }


    public Location createAddressLocation(String street, String city, Long postCode, String country) {
        if (country.length() == 2) {
            try {
                Location l = new Location(street, city, postCode, country);
                Location o = em.merge(l);
                System.out.println("DAO-LOC: Added Address " + o.getId());
                return o;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isLocationGps(Location l) {
        if (l.getGpsLat() == null && l.getGpsLong() == null) {
            return false;
        } else {
            return true;
        }
    }


    public boolean deleteLocation(Location l) {
        boolean success = false;
        if (l != null) {
            em.refresh(l);
            String locationUid = l.getId();
            if (locationUid != null) {
                em.remove(l);
                em.flush();
                if (getLocation(locationUid) == null) {
                    success = true;
                }
            }
        }
        return success;
    }

}
