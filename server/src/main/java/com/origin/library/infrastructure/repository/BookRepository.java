package com.origin.library.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.origin.library.domain.Book;
import com.origin.library.domain.Page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

interface AdvancedBookRepository {
	Page<Book> searchBooks(String keyword, int offset, int limit);
}

public interface BookRepository extends JpaRepository<Book, Long>, AdvancedBookRepository {

}

@Service
class AdvancedBookRepositoryImpl implements AdvancedBookRepository {
	@Autowired
	private EntityManager em;

	@Override
	public Page<Book> searchBooks(String keyword, int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Book> q = cb.createQuery(Book.class);
		Root<Book> a = q.from(Book.class);

		Predicate[] w = searchBooksPredicates(cb, a, keyword);
		q = q.select(a.alias("a")) // FIXME: alias not working, remove damn b1_0 in SQL
				.where(w)
				.orderBy(cb.asc(a.get("id")));

		List<Book> books = em.createQuery(q)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();

		// FIXME: too many copies
		CriteriaQuery<Long> q2 = cb.createQuery(Long.class);
		Root<Book> a2 = q2.from(Book.class);
		Predicate[] w2 = searchBooksPredicates(cb, a2, keyword);
		q2 = q2.select(cb.count(a2))
				.where(w2);
		long count = em.createQuery(q2).getSingleResult();

		return new Page<Book>(books, count);
	}

	Predicate[] searchBooksPredicates(CriteriaBuilder cb, Root<Book> a, String keyword) {
		List<Predicate> predicates = new ArrayList<>();
		if (keyword != null && !keyword.isEmpty()) {
			predicates.add(cb.like(a.get("name"), "%" + keyword + "%"));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
}