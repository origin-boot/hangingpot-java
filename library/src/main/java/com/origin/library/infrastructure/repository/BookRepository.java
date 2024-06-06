package com.origin.library.infrastructure.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.origin.library.domain.Book;
import com.origin.library.domain.Page;
import com.origin.library.domain.QBook;
import com.origin.library.infrastructure.querydsl.ShortcutPredicateExecutor;
import com.querydsl.core.types.Predicate;

public interface BookRepository
		extends JpaRepository<Book, Long>, ShortcutPredicateExecutor<Book> {
	default Page<Book> searchBooks(String keyword, int pageNumber, int pageSize) {
		QBook a = QBook.book;
		Predicate p = predicate()
				.and(isNotBlank(keyword), a.name.like(quoteLike(keyword)))
				.build();
		Sort sort = asc("id");
		return findAll(p, sort, pageNumber, pageSize);
	}
}