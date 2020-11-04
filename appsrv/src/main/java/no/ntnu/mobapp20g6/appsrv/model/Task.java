package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = Task.FIND_ALL_TASKS, query = "SELECT t FROM tasks t" )
@NamedQuery(name = Task.FIND_TASK_BY_ID, query = "SELECT t FROM tasks t WHERE t.id = :id") //<-- Need testing
public class Task implements Serializable {
    public static final String FIND_ALL_TASKS = "getAllTasks";
    public static final String FIND_TASK_BY_ID = "findTaskById";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="task_id")
    private Long id;


    public enum Status {
        ACTIVE, ARCHIVED
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status currentStatus = Status.ACTIVE;

    @NotEmpty
    private String title;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date created;

    @Size(max = 280)
    private String description;

    @Column(name = "participant_limit")
    @Positive
    @NotEmpty
    private int participantLimit;

    @Column(name = "participant_count")
    @Positive
    private int participantCount;

    @PrePersist
    protected void onCreate() {
        created = new Date();

        //Owner is not participant
        participantCount = 0;
    }

    // N-1 Owner
    @ManyToOne
    @JoinColumn(name = "creator_user_id", referencedColumnName = "user_id")
    private User creatorUser;

    // N-1 Owner
    @ManyToOne
    @JoinColumn(name = "member_group_id", referencedColumnName = "group_id")
    private Group associatedGroup;

    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;

    // 1-1 Owner
    @OneToOne
    @JoinColumn(name = "picture_id", referencedColumnName = "picture_id")
    private Picture picture;


    // M-N Owner
    @ManyToMany
    @JoinTable(name = "task_has_user",
            joinColumns = @JoinColumn(
                    name = "task_id",
                    referencedColumnName = "task_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id"))
    List<User> users;


    public List<User> getUsers() {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        return this.users;

    }

}
