package com.origin.library.port;

import java.util.List;

import com.origin.library.domain.Book;
import com.origin.library.domain.Borrow;
import com.origin.library.domain.Page;
import com.origin.library.domain.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Data
class LoginCommand {
  @NotBlank(message = "username is required")
  @Length(min = 4, max = 20, message = "username length must be between 4 and 20")
  private String username;

  @NotBlank(message = "password is required")
  @Length(min = 4, max = 20, message = "password length must be between 4 and 20")
  private String password;
}

@Data
class SearchBooksQuery {
  @Length(max = 30, message = "name length must be less than 30")
  private String name;

  @NotNull(message = "is whether to filter books that I have borrowed or returned")
  private Boolean mine;

  @Min(value = 1, message = "pageNum must be greater than 0")
  private int pageNum;

  @Range(min = 5, max = 100, message = "pageSize must be between 1 and 100")
  private int pageSize;
}

class SearchBooksResponse extends Page<BookResource> {
  SearchBooksResponse(List<BookResource> records, long total) {
    super(records, total);
  }

  static SearchBooksResponse of(Page<Book> pagedBooks) {
    List<BookResource> records = pagedBooks.getRecords().stream()
        .map(BookResource::of)
        .toList();
    return new SearchBooksResponse(records, pagedBooks.getTotal());
  }

  SearchBooksResponse getUserView(User currentUser) {
    long currentUserId = currentUser.getId();
    records.forEach(r -> {
      if (r.getUserId() != currentUserId) {
        // hide sensitive info of other users
        r.setUserId(0);
        r.setUsername("");
      } else {
        // fill the username of the current user
        r.setUsername(currentUser.getUsername());
      }
    });
    return this;
  }

  static SearchBooksResponse ofPagedBorrows(Page<Borrow> pagedBorrows) {
    List<BookResource> records = pagedBorrows.getRecords().stream()
        .map(BookResource::of)
        .toList();
    return new SearchBooksResponse(records, pagedBorrows.getTotal());
  }
}
