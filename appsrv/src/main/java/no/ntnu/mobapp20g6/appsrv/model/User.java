package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.ntnu.mobapp20g6.appsrv.auth.RoleGroup;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = User.FIND_ALL_USERS,
                query = "SELECT p FROM users p ORDER BY p.firstName"),
        @NamedQuery(name = User.FIND_USER_BY_EMAIL,
                query = "SELECT p FROM users p WHERE p.email LIKE :email")
})
public class User implements Serializable {

    public final static String FIND_ALL_USERS = "User.findAllUsers";
    public final static String FIND_USER_BY_EMAIL = "User.findUserByEmail";

    public enum State {
        ACTIVE, INACTIVE
    }

    @Id
    @Column(name = "user_id")
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

    // M-N OWNER
    @ManyToMany
    @JoinTable(name = "user_has_rolegroup",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_name",
                    referencedColumnName = "role_name"))
    List<RoleGroup> roleGroups;

    public List<RoleGroup> getRoleGroups() {
        if (this.roleGroups == null) {
            this.roleGroups = new ArrayList<>();
        }
        return this.roleGroups;

    }

    // N-1 Owner
    @ManyToOne
    @JoinColumn(name = "member_group", referencedColumnName = "group_id")
    private Group memberOfGroup;

    // 1-N REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ownerUser")
    private List<Task> ownedTasks;

    // N-1 REF
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ownerUser")
    private List<Group> ownedGroups;

    // M-N REF
    @JsonbTransient
    @Getter
    @ManyToMany
    @JoinTable(name = "task_has_user",
            joinColumns = @JoinColumn(
                    name = "task_task_id",
                    referencedColumnName = "task_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_user_id",
                    referencedColumnName = "user_id"))
    List<Task> assignedTasks;


}
