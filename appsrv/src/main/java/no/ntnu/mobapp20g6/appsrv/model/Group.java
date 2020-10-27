package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "group_name")
    @NotEmpty
    @Size(max = 35)
    private String name;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date created;

    @Column(name = "org_id", length = 9)
    private Long originationId;

    @Size(max = 280)
    private String description;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }
}
