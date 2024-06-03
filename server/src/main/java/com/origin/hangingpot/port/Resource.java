package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.User;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

@Data
class PageResource {
	private List list;
	private PageInfo pageInfo;
	public static   PageResource of(Page pageInfo){
		PageResource resource = new PageResource();
		resource.setList(pageInfo.getContent());
		resource.setPageInfo(PageInfo.builder().total(pageInfo.getTotalElements()).build());
		return resource;
	}
}