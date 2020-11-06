package no.ntnu.mobapp20g6.appsrv.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

@Entity(name = "locations")
@Data
@NoArgsConstructor
public class Location implements Serializable {

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
    }

    public Location(String lat, String lon) {
       this.gpsLat = lat;
       this.gpsLong = lon;
    }

    public Location(String street, String city, Long postCode, String country) {
        this.streetAddress = street;
        this.city = city;
        this.postalCode = postCode;
        this.country = country;
    }


    @Id
    @Generated
    @Column(name="location_id")
    private String id;

    @Column(name = "gps_lat")
    private String gpsLat;

    @Column(name = "gps_long")
    private String gpsLong;

    @Column(name = "street_Address")
    private String streetAddress;

    private String city;

    @Column(name = "postal_code", length = 4)
    private Long postalCode;

    @Column(length = 2)
    private String country;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "location")
    private Group group;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "location")
    private Task task;

}
