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
public class Task implements Serializable {

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
}
