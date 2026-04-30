package com.order.api01authgateway.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "use_id")
    private Integer id;

    @Column(name = "use_name", nullable = false, unique = true)
    private String username;

    @Column(name = "use_password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "use_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "use_created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "use_updated_at")
    private LocalDateTime updatedAt;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
