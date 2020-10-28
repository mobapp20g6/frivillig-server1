package no.ntnu.mobapp20g6.appsrv.auth;

import lombok.*;
import no.ntnu.mobapp20g6.appsrv.model.User;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author mikael
 */
@Entity
@Table(name = "rolegroups")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "users")
public class RoleGroup implements Serializable {

	public static final String USER = "user";
	public static final String ADMIN = "admin";
	public static final String[] PERMISSIONS = {USER, ADMIN};

	@Id
	String name;

	String project;

	// M-N REF
	@JsonbTransient
	@Getter
	@ManyToMany
	@JoinTable(name = "user_has_rolegroup",
		joinColumns = @JoinColumn(
			name = "name",
			referencedColumnName = "name"),
		inverseJoinColumns = @JoinColumn(
			name = "id",
			referencedColumnName = "id"))
	List<User> users;

	public RoleGroup(String name) {
		this.name = name;
	}
}
