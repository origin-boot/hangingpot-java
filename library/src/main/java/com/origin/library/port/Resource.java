package com.origin.library.port;

import com.origin.library.domain.User;
import com.origin.library.domain.Book;
import com.origin.library.domain.Borrow;

import lombok.Data;

@Data
class UserResource {
  private long id;
  private String username;
  private long regTime;

  public UserResource() {
    this.username = "";
  }

  public static UserResource of(User user) {
    UserResource resource = new UserResource();
    resource.setId(user.getId());
    resource.setUsername(user.getUsername());
    resource.setRegTime(user.getCreateTime());
    return resource;
  }

}

@Data
class BookResource {
  private long id;
  private String name;
  private long borrowTime;
  private long returnTime;
  private long userId;
  private String username;

  public BookResource() {
    this.name = "";
    this.username = "";
  }

  static BookResource of(Book book) {
    BookResource resource = new BookResource();
    resource.setId(book.getId());
    resource.setName(book.getName());
    resource.setBorrowTime(book.getBorrowTime());
    resource.setReturnTime(book.getReturnTime());
    resource.setUserId(book.getUserId());
    return resource;
  }

  static BookResource of(Borrow borrow) {
    BookResource resource = new BookResource();
    resource.setId(borrow.getBook().getId());
    resource.setName(borrow.getBook().getName());
    resource.setBorrowTime(borrow.getBorrowTime());
    resource.setReturnTime(borrow.getReturnTime());
    resource.setUserId(borrow.getUserId());
    return resource;
  }
}
