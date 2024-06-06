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
		// where username like '%user%'
		// and id>0 and (id>0 and id<1000) and (id>-1 or id<1001)
		// or id<0 or (id<-1 or id<-2) or (id<-3 and id<-4)
		Predicate p = predicate()
				.and(isNotBlank(keyword), a.username.like(quoteLike(keyword)))
				.and(a.id.gt(0))
				.and(a.id.gt(0), a.id.lt(1000))
				.andAnyOf(true, a.id.gt(-1), a.id.lt(1001))
				.or(a.id.lt(0))
				.or(a.id.lt(-1), a.id.lt(-2))
				.orAllOf(true, a.id.lt(-3), a.id.lt(-4))
				.build();
		Page<User> r = findAll(
				q -> q.select(a)
						.from(a)
						.where(p),
				pageSize);
		return r;
	}
}