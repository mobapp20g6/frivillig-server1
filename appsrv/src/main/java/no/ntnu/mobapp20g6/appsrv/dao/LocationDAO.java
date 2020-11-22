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

    /**
     * Return a location object from a specified ID
     * @param uid the id of the location
     * @return returned location object
     */
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

    /**
     * Create a GPS location object
     * @param lat the latitude
     * @param lon the longitude
     * @return the created location object
     */
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


    /**
     * Like GPS location function-wise, only now holds address data instread
     * @param street the street name and house number
     * @param city the city
     * @param postCode the postal code
     * @param country the country code - must be 2 letters of ISO3166
     * @return
     */
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

    /**
     * Helper to determine if a location is of type GPS
     * @param l the location object to assess
     * @return true if a GPS type location
     */
    public boolean isLocationGps(Location l) {
        if (l.getGpsLat() == null && l.getGpsLong() == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     *  Delete the specified location from the database
     * @param l the location object to delete
     * @return returns true if the operation succeeded
     */
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
