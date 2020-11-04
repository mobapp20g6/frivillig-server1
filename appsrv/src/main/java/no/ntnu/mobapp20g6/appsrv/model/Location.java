package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity(name = "locations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {

    @Id
    @Column(name="location_id")
    private Long Id;

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
