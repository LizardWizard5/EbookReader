package ca.lizardwizard.ebookreader.model;


import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false, length=50)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length=50)
    private String password;
}
