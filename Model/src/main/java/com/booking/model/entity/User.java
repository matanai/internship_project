package com.booking.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity(name = "User")
@Table(
        name = "user_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unq_username",
                        columnNames = "username"
                )
        }
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, Serializable
{
    private static final long serialVersionUID = 42L;

    @Id
    @GenericGenerator(
            name = "uuid_sequence",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @GeneratedValue(generator = "uuid_sequence")
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    @NotEmpty
    @Size(min = 2, max = 30, message = "First name must be between 2 to 30 chars")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty
    @Size(min = 2, max = 30, message = "Last name must be between 2 to 30 chars")
    private String lastName;

    @Column(name = "username", updatable = false, nullable = false)
    @NotEmpty
    @Size(min = 5, max = 30, message = "Username must be between 5 to 30 chars")
    private String username;

    @Column(name = "password", nullable = false)
    @NotEmpty
    @Size(min = 5, max = 30, message = "Password must be between 5 to 30 chars")
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean isEnabled;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_tb",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @JsonIgnore
    private Set<Tracking> tracking;

    // USER DETAILS IMPLEMENTATION METHODS

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
