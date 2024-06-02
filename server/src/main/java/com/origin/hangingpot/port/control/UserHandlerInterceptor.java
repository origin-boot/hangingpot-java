package com.origin.hangingpot.port.control;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.hangingpot.domain.User;
import com.origin.hangingpot.domain.error.Error;
import com.origin.hangingpot.domain.error.UnauthorizedError;
import com.origin.hangingpot.infrastructure.repository.UserRepository;
import com.origin.hangingpot.infrastructure.util.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Component
public class UserHandlerInterceptor implements HandlerInterceptor {

	private final Logger logger = Logger.getLogger(UserHandlerInterceptor.class.getName());
	public static final String ATTRIBUTE = "User";

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		try {
			User user = getUser(request);
			request.setAttribute(ATTRIBUTE, user);
			return true;
		} catch (Exception ex) {
			String stackTrace = ExceptionUtil.getStackTrace(ex, true, 1000);
			logger.warning(stackTrace);

			// Return 401 Unauthorized to client with error message in JSON format
			Error e = new UnauthorizedError().setDetails(stackTrace);
			ObjectMapper objectMapper = new ObjectMapper();
			String responseBody = objectMapper.writeValueAsString(e);

			response.setHeader("Content-Type", "application/json");
			response.setStatus(e.getStatus());
			response.getWriter().write(responseBody);

			return false;
		}
	}

	private User getUser(HttpServletRequest request) throws Exception {
		Object id = request.getAttribute(IdentityHandlerInterceptor.ATTRIBUTE);
		if (id == null) {
			throw new Exception("Identity not found");
		}
		long userId = Long.parseLong(id.toString());
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new Exception("User not found: " + id));
		return user;
	}

	void addInterceptors(InterceptorRegistry registry) {
		List<String> guestPathPatterns = List.of("/api/login");

		registry.addInterceptor(this)
				.excludePathPatterns(guestPathPatterns);
	}
}