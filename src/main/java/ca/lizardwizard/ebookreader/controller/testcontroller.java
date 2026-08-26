package ca.lizardwizard.ebookreader.controller;

import ca.lizardwizard.ebookreader.model.Book;
import ca.lizardwizard.ebookreader.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class testcontroller {

    private final BookRepository bookRepository;

    // Inject the repository into the controller
    public testcontroller(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookRepository.findAll();

        // Explicitly attach the 'books' list to the model
        // Key: "books" (this name will be used in HTML)
        // Value: books (your Java List<Book>)
        model.addAttribute("books", books);

        return "index"; // Renders index.html
    }
}