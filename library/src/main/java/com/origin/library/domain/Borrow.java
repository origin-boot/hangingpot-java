package com.origin.library.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "borrows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "book_id")
	private long bookId;

	@Column(name = "borrow_time")
	private long borrowTime;

	@Column(name = "return_time")
	private long returnTime;

	@Column(name = "create_time")
	private long createTime;

	// FIXME: replace with lazy loading
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", insertable = false, updatable = false)
	private Book book;
}
