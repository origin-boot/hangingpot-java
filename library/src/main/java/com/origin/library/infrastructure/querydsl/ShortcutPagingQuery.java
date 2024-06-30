package com.origin.library.infrastructure.querydsl;

import java.util.List;

import com.origin.library.domain.Page;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ShortcutPagingQuery implements ShortcutExecutor {
  @PersistenceContext
  private EntityManager em;

  public <T> Page<T> findAll(ShortcutQueryFunction<T> queryFunction, int pageNumber, int pageSize) {
    JPAQuery<T> query = new JPAQuery<>(em);
    queryFunction.apply(query);

    @SuppressWarnings("deprecation")
    long total = query.fetchCount();

    if (total == 0) {
      return Page.empty();
    }

    List<T> list = query
        .offset(pageOffset(pageNumber, pageSize))
        .limit(pageSize(pageSize))
        .fetch();

    return new Page<>(list, total);
  }

  public <T> Page<T> findAll(ShortcutQueryFunction<T> queryFunction, int pageSize) {
    return findAll(queryFunction, 0, pageSize);
  }
}
