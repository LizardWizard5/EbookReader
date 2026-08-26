package ca.lizardwizard.ebookreader.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recently_listened")
public class RecentlyListened {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Maps the Foreign Key (book_id) to the Book entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Map the Foreign Key (user_id) to the User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Integer timestamp; // Stores seconds played or UNIX timestamp

    @Column(nullable = false)
    private LocalDateTime date;

    // Standard Default Constructor
    public RecentlyListened() {
    }

    // Parameterized Constructor
    public RecentlyListened(Book book, Integer timestamp, LocalDateTime date, User user) {
        this.book = book;
        this.timestamp = timestamp;
        this.date = date;
        this.user = user;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}