package com.origin.hangingpot.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.origin.hangingpot.domain.Page;
import com.origin.hangingpot.domain.QUser;
import com.origin.hangingpot.domain.User;
import com.origin.hangingpot.infrastructure.querydsl.ShortcutPagingQuery;
import com.origin.hangingpot.infrastructure.querydsl.ShortcutPredicateExecutor;
import com.querydsl.core.types.Predicate;

public interface UserRepository
		extends JpaRepository<User, Long>, ShortcutPredicateExecutor<User>, AdvancedUserRepository {
	Optional<User> findByUsername(String username);

	// FIXME: remove the example
	default Page<User> searchUsers(String keyword, int pageNumber, int pageSize) {
		QUser a = QUser.user;
		Predicate p = predicate()
				.and(isNotBlank(keyword), a.username.like(quoteLike(keyword)))
				.build();
		Sort sort = asc("id");
		return findAll(p, sort, pageNumber, pageSize);
	}
}

interface AdvancedUserRepository {
	Page<User> searchUsers(String keyword, int pageSize);
}

@Service
class AdvancedUserRepositoryImpl extends ShortcutPagingQuery implements AdvancedUserRepository {
	// FIXME: remove the example
	@Override
	public Page<User> searchUsers(String keyword, int pageSize) {
		QUser a = QUser.user;
		Predicate p = predicate()
				.and(isNotBlank(keyword), a.username.like(quoteLike(keyword)))
				.build();
		Page<User> r = findAll(
				q -> q.select(a)
						.from(a)
						.where(p),
				pageSize);
		return r;
	}
}