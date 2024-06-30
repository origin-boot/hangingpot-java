package com.origin.library.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @Column(name = "borrow_time")
  private long borrowTime;

  @Column(name = "return_time")
  private long returnTime;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "create_time")
  private long createTime;

  public boolean couldBeBorrowed() {
    return (borrowTime == 0 && returnTime == 0)
        || (borrowTime != 0 && returnTime != 0);
  }

  public boolean couldBeReturned() {
    return borrowTime != 0 && returnTime == 0;
  }

  public boolean isBorrowedBy(long userId) {
    return this.userId == userId;
  }
}
