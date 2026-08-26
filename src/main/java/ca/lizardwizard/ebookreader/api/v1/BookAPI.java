package ca.lizardwizard.ebookreader.api.v1;

import ca.lizardwizard.ebookreader.model.Book;
import ca.lizardwizard.ebookreader.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/books")
public class BookAPI {
    private final BookRepository bookRepository;

    public BookAPI(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "books/list"; // Renders list.html template
    }
}
