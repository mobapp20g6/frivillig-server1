package no.ntnu.mobapp20g6.appsrv.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author trymv
 */
@Entity(name = "groups")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude={"ownerUser","memberUsers","associatedTasks"})
@NamedQuery(name = Group.FIND_ALL_GROUPS, query = "SELECT g FROM groups g" )
public class Group implements Serializable {
    public static final String FIND_ALL_GROUPS = "findAllGroups";

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
    @JsonbTransient
    @JoinColumn(name = "owner_user_id", referencedColumnName = "user_id")
    private User ownerUser;

    // 1-1 Owner
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;

    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
    private Picture picture;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "memberOfGroup")
    @JsonbTransient
    private List<User> memberUsers;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "associatedGroup")
    @JsonbTransient
    private List<Task> associatedTasks;

    /**
     * Constructor for group.
     * @param tile name of group.
     * @param description of group
     * @param orgId
     * @param creator user which created the group.
     */
    public Group(String tile, String description, Long orgId, User creator) {
        this.name = tile;
        this.description = description;
        this.originationId = orgId;
        this.ownerUser = creator;
        if(this.memberUsers == null) {
            this.memberUsers = new ArrayList<>();
        }
    }
}
