package com.origin.library.port;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.origin.library.domain.User;
import com.origin.library.domain.error.BookNotFoundError;
import com.origin.library.domain.error.RequestForbiddenError;
import com.origin.library.domain.error.UserNotFoundError;
import com.origin.library.domain.success.Empty;
import com.origin.library.domain.success.Ok;
import com.origin.library.port.control.BaseController;
import com.origin.library.port.control.RequestUser;

import jakarta.validation.Valid;

@RestController
public class BookController extends BaseController {

  @Autowired
  private BookHandler bookHandler;

  @GetMapping("/api/books")
  public Ok<SearchBooksResponse> searchBooks(@RequestUser User user, @Valid SearchBooksQuery query)
      throws UserNotFoundError {

    if (query.getMine()) {
      SearchBooksResponse response = bookHandler.searchMyBooks(user, query);
      return Ok.of(response);
    }

    SearchBooksResponse response = bookHandler.searchBooks(user, query);
    return Ok.of(response);
  }

  @PostMapping("/api/books/{id}/borrow")
  public Ok<Empty> borrowBook(@RequestUser User user, @PathVariable("id") final long id)
      throws BookNotFoundError,
      RequestForbiddenError,
      UserNotFoundError {

    bookHandler.borrowBook(user, id);

    return Ok.empty();
  }

  @PostMapping("/api/books/{id}/return")
  public Ok<Empty> returnBook(@RequestUser User user, @PathVariable("id") final long id)
      throws BookNotFoundError,
      RequestForbiddenError,
      UserNotFoundError {

    bookHandler.returnBook(user, id);

    return Ok.empty();
  }
}
