package com.origin.library.domain.error;

import org.springframework.http.HttpStatus;

public class BookNotFoundError extends Error {
  public BookNotFoundError() {
    super(Code.BOOK_NOT_FOUND.value(), "Book not found", "", HttpStatus.NOT_FOUND.value());
  }

  // FIXME: Use generics to resolve the returned this pointing to sub-instances
  // Without having to use @Override to override each parent class method
  @Override
  public BookNotFoundError setDetails(String details) {
    super.setDetails(details);
    return this;
  }
}
