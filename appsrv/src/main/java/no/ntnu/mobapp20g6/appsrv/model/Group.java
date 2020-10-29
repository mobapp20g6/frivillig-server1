package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {

    @Id
    @Column(name="group_id")
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


    // 1-1 Owner
    @ManyToOne
    @JoinColumn(name = "owner_user_id", referencedColumnName = "user_id")
    private User ownerUser;

    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;

    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
    private Picture picture;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "memberOfGroup")
    private List<User> memberUsers;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "associatedGroup")
    private List<Task> associatedTasks;
}
