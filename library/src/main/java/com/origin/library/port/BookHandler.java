package com.origin.library.port;

import org.springframework.stereotype.Service;

import com.origin.library.domain.Book;
import com.origin.library.domain.Borrow;
import com.origin.library.domain.Page;
import com.origin.library.domain.User;
import com.origin.library.domain.error.BookNotFoundError;
import com.origin.library.domain.error.RequestForbiddenError;
import com.origin.library.infrastructure.repository.BookRepository;
import com.origin.library.infrastructure.repository.BorrowRepository;
import com.origin.library.infrastructure.util.TimeUtil;

@Service
public class BookHandler {

	private BookRepository bookRepository;
	private BorrowRepository borrowRepository;

	public BookHandler(final BookRepository bookRepository, final BorrowRepository borrowRepository) {
		this.bookRepository = bookRepository;
		this.borrowRepository = borrowRepository;
	}

	public Book getBookById(Long id) throws BookNotFoundError {
		return bookRepository.findById(id).orElseThrow(
				() -> new BookNotFoundError().setDetails("id: " + id));
	}

	public SearchBooksResponse searchBooks(User user, SearchBooksQuery query) {
		Page<Book> pagedBooks = bookRepository.searchBooks(query.getName(), query.getPageNum(), query.getPageSize());
		SearchBooksResponse response = SearchBooksResponse.of(pagedBooks)
				.getUserView(user);

		return response;
	}

	public SearchBooksResponse searchMyBooks(User user, SearchBooksQuery query) {
		Page<Borrow> pagedBorrows = borrowRepository.searchMyBorrows(user.getId(), query.getName(),
				query.getPageNum(), query.getPageSize());
		SearchBooksResponse response = SearchBooksResponse.ofPagedBorrows(pagedBorrows)
				.getUserView(user);

		return response;
	}

	public void borrowBook(User user, Long bookId) throws BookNotFoundError, RequestForbiddenError {
		Book book = getBookById(bookId);

		if (!book.couldBeBorrowed()) {
			throw new RequestForbiddenError().setDetails("Book is not available for borrowing");
		}

		book.setUserId(user.getId());
		book.setReturnTime(0);
		book.setBorrowTime(TimeUtil.getUnixTimestamp());
		book = bookRepository.save(book);

		Borrow borrow = new Borrow();
		borrow.setUserId(user.getId());
		borrow.setBookId(book.getId());
		borrow.setBorrowTime(book.getBorrowTime());
		borrow.setReturnTime(book.getReturnTime());
		borrow.setCreateTime(TimeUtil.getUnixTimestamp());
		borrow = borrowRepository.save(borrow);

		return;
	}

	public void returnBook(User user, Long bookId) throws BookNotFoundError, RequestForbiddenError {
		Book book = getBookById(bookId);

		if (!book.couldBeReturned()) {
			throw new RequestForbiddenError().setDetails("Book is not available for returning");
		}

		if (!book.isBorrowedBy(user.getId())) {
			throw new RequestForbiddenError().setDetails("Book is not borrowed by the user");
		}

		book.setReturnTime(TimeUtil.getUnixTimestamp());
		book = bookRepository.save(book);

		Borrow borrow = new Borrow();
		borrow.setUserId(user.getId());
		borrow.setBookId(book.getId());
		borrow.setBorrowTime(book.getBorrowTime());
		borrow.setReturnTime(book.getReturnTime());
		borrow.setCreateTime(TimeUtil.getUnixTimestamp());
		borrow = borrowRepository.save(borrow);

		return;
	}
}
