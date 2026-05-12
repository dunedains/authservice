package org.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import javax.management.relation.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email",nullable = false,unique = true,length = 100)
    @Email
    private String email;
    @Column(name = "password_hash",nullable = false,length = 200)
    private String password;
    @Column(name ="first_name",nullable = false,length = 100)
    private String firstname;
    @Column(name ="last_name",nullable = false,length = 100)
    private String lastname;

}
