package com.kaustack.auth.model;

import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "flags")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Flag {

    public static final String SUPER_ADMIN = "super-admin";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @Setter
    @Column(length = 255)
    private String description;
}
