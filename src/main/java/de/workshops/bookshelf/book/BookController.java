package de.workshops.bookshelf.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
class BookController {

  private final BookService bookService;

  @GetMapping
  String getAllBooks(Model model) {
    model.addAttribute("books", bookService.getAllBooks());

    return "books";
  }
}
