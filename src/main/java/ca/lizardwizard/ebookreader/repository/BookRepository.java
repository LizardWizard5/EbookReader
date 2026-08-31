package ca.lizardwizard.ebookreader.repository;

import ca.lizardwizard.ebookreader.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // JpaRepository provides built-in CRUD methods:
    // - save(Book book)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)

    // Optional: Custom query methods derived from method names
    List<Book> findByAuthor(String author);
    List<Book> findByNameContainingIgnoreCase(String name);

}
