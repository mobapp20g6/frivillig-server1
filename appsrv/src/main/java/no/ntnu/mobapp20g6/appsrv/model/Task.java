package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

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
    private Long id;


    public enum Status {
        ACTIVE, ARCHIVED
    }

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
    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    private User ownerUser;

}
