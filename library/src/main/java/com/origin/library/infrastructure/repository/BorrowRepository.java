package com.origin.library.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.origin.library.domain.Borrow;
import com.origin.library.domain.Page;
import com.origin.library.domain.QBook;
import com.origin.library.domain.QBorrow;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.core.types.Predicate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

interface AdvancedBorrowRepository {
	Page<Borrow> searchMyBorrows(long userId, String keyword, int offset, int limit);
}

public interface BorrowRepository extends JpaRepository<Borrow, Long>, AdvancedBorrowRepository {

}

@Service
class AdvancedBorrowRepositoryImpl implements AdvancedBorrowRepository, ShortcutExecutor {
	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Borrow> searchMyBorrows(long userId, String keyword, int pageNumber, int pageSize) {
		QBorrow a = QBorrow.borrow;
		QBook b = QBook.book;

		Predicate q = predicate()
				.and(userId > 0, a.userId.eq(userId))
				.and(isNotBlank(keyword), b.name.like(quoteLike(keyword)))
				.build();

		List<Borrow> borrows = new JPAQuery<>(em).select(a)
				.from(a)
				.join(b)
				.on(a.bookId.eq(b.id))
				.where(q)
				.orderBy(a.id.desc())
				.offset((pageNumber - 1) * pageSize)
				.limit(pageSize)
				.fetch();

		@SuppressWarnings("deprecation")
		long total = new JPAQuery<>(em).select(a.id.count())
				.from(a)
				.join(b)
				.on(a.bookId.eq(b.id))
				.where(q)
				.fetchCount();

		return new Page<Borrow>(borrows, total);
	}
}