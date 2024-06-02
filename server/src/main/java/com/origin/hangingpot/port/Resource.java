package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.User;

import lombok.Data;

@Data
class UserResource {
	private long id;
	private String username;
	private long regTime;

	public UserResource() {
		this.username = "";
	}

	public static UserResource of(User user) {
		UserResource resource = new UserResource();
		resource.setId(user.getId());
		resource.setUsername(user.getUsername());
		resource.setRegTime(user.getCreateTime());
		return resource;
	}

}