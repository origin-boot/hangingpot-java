package com.origin.library.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.origin.library.domain.Borrow;
import com.origin.library.domain.Page;
import com.origin.library.domain.QBook;
import com.origin.library.domain.QBorrow;
import com.origin.library.infrastructure.querydsl.ShortcutPagingQuery;
import com.querydsl.core.types.Predicate;

interface AdvancedBorrowRepository {
  Page<Borrow> searchMyBorrows(long userId, String keyword, int offset, int limit);
}

public interface BorrowRepository extends JpaRepository<Borrow, Long>, AdvancedBorrowRepository {

}

@Service
class AdvancedBorrowRepositoryImpl extends ShortcutPagingQuery implements AdvancedBorrowRepository {

  @Override
  public Page<Borrow> searchMyBorrows(long userId, String keyword, int pageNumber, int pageSize) {
    QBorrow a = QBorrow.borrow;
    QBook b = QBook.book;

    Predicate p = predicate()
        .and(userId > 0, a.userId.eq(userId))
        .and(isNotBlank(keyword), b.name.like(quoteLike(keyword)))
        .build();

    Page<Borrow> r = findAll(
        q -> q.select(a)
            .from(a)
            .join(b)
            .on(a.bookId.eq(b.id))
            .where(p)
            .orderBy(a.id.desc()),
        pageNumber, pageSize);

    return r;
  }
}