package com.booking.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "Role")
@Table(
        name = "role_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unq_role_name",
                        columnNames = "role_name"
                )
        }
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority, Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @SequenceGenerator(
            name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "role_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "role_name", nullable = false)
    @NotEmpty
    @Size(min = 10, max = 100, message = "Role name must be between 10 to 100 chars")
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @JsonIgnore
    private Set<User> users;

    @Override
    public String getAuthority() {
        return this.roleName;
    }

    // TO STRING

    @Override
    public String toString() {
        return roleName.replaceFirst("ROLE_", "");
    }
}
