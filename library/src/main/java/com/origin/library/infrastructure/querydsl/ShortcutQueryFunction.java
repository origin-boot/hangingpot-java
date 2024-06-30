package com.origin.library.infrastructure.querydsl;

import com.querydsl.jpa.impl.JPAQuery;

@FunctionalInterface
public interface ShortcutQueryFunction<T> {
  void apply(JPAQuery<T> query);
}
