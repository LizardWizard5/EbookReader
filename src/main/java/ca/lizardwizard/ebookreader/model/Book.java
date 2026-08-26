package ca.lizardwizard.ebookreader.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 500)
    private String description;

    @Column(name = "pdfName", nullable = false, length = 50)
    private String pdfName;

    @Column(name = "audioName", nullable = false, length = 50)
    private String audioName;

    @Column(name = "coverName", nullable = false, length = 50)
    private String coverName;

    @Column(name = "audioLength", nullable = false)
    private Integer audioLength;

    // Map the Foreign Key (user_id) to the User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Standard Default Constructor (Required by JPA)
    public Book() {
    }

    // Parameterized Constructor (Convenient for creating new instances)
    public Book(String name, String author, String description, String pdfName, String audioName, String coverName, Integer audioLength, User user) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.pdfName = pdfName;
        this.audioName = audioName;
        this.coverName = coverName;
        this.audioLength = audioLength;
    }


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }

    public Integer getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(Integer audioLength) {
        this.audioLength = audioLength;
    }

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}
