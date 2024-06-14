package com.origin.hangingpot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

// FIXME: Use the generic User<T> to be compatible with users whose id is String
// And the basic User implementation should be an abstract class
@Entity
@Table(name = "users")
@Proxy(lazy=false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Column(name = "create_time")
	private long createTime;

	public boolean isMatchPassword(String inputPassword) {
		return this.password != null && this.password.equals(inputPassword);
	}
}
