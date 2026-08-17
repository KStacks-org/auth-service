package com.kaustack.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @Email
    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Gender gender = Gender.UNKNOWN;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_flags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "flag_id")
    )
    @Builder.Default
    private Set<Flag> flags = new HashSet<>();
}
