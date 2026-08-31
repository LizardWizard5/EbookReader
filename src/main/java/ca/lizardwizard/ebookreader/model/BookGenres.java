package ca.lizardwizard.ebookreader.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book_genres", uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "genre_id"}))
public class BookGenres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genres genre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    protected BookGenres() {}
    public BookGenres(Genres genre, Book book) {
        this.genre = genre;
        this.book = book;
    }
}
