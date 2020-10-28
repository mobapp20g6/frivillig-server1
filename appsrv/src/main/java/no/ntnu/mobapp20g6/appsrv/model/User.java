package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    public enum State {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "first_name")
    @NotEmpty
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    private String lastName;

    @Email
    @NotEmpty
    private String email;

    @JsonbTransient
    @Size(min = 6)
    @NotEmpty
    private String password;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @Enumerated(EnumType.STRING)
    State currentState = State.ACTIVE;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ownerUser")
    private List<Task> ownedTasks;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "assignedTask")
    private List<Task> assignedTasks;
}
