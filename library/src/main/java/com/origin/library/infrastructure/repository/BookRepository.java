package com.origin.library.infrastructure.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.origin.library.domain.Book;
import com.origin.library.domain.Page;
import com.origin.library.domain.QBook;
import com.querydsl.core.BooleanBuilder;

public interface BookRepository
		extends JpaRepository<Book, Long>, ShortcutPredicateExecutor<Book> {
	default Page<Book> searchBooks(String keyword, int pageNumber, int pageSize) {
		QBook a = QBook.book;
		BooleanBuilder q = new BooleanBuilder();
		if (isNotBlank(keyword)) {
			q.and(a.name.likeIgnoreCase(quoteLike(keyword)));
		}
		Sort sort = Sort.by(Sort.Order.asc("id"));
		return findAll(q, sort, pageNumber, pageSize);
	}
}