package no.ntnu.mobapp20g6.appsrv.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "tasks")
@Data
@NoArgsConstructor
@NamedQuery(name = Task.FIND_ALL_TASKS, query = "SELECT t FROM tasks t" )
public class Task implements Serializable {
    public static final String FIND_ALL_TASKS = "getAllTasks";

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
    @Getter
    private Date created;

    @Column(name = "scheduel_date")
    private Timestamp scheduleDate;

    @Size(max = 280)
    private String description;

    @Column(name = "participant_limit")
    @Min(value = 0L)
    @NotNull
    private Long participantLimit;

    @Column(name = "participant_count")
    @Min(value = 0L)
    private Long participantCount;

    @PrePersist
    protected void onCreate() {
        created = new Date();

        //Owner is not participant
        participantCount = 0L;
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
    @OneToOne(cascade = CascadeType.REMOVE)
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
    private List<User> users;


    public List<User> getUsers() {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        return this.users;
    }

    public void addToParticipantCount(int participantsToAdd) {
        participantCount += participantsToAdd;
    }

    /**
     * Task constructor.
     * @param title of task.
     * @param description to describe the task.
     * @param scheduled date of when task is scheduled to be done.
     * @param participantLimit maximum of participants allowed to join task. Will be set
     *                         to 1 if null or 0.
     * @param creator of the task.
     * @param associatedGroup group the task is associated with. If null group is public.
     */
    public Task(String title, String description, Timestamp scheduled, Long participantLimit,
                User creator, Group associatedGroup) {
        this.title = title;
        this.description = description;
        this.scheduleDate = scheduled;
        if(participantLimit == null || participantLimit == 0) {
            this.participantLimit = 1L;
        } else {
            this.participantLimit = participantLimit;
        }
        this.creatorUser = creator;
        this.associatedGroup = associatedGroup;
    }

}
