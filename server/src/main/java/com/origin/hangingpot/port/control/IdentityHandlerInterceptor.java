package com.origin.hangingpot.port.control;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IdentityHandlerInterceptor implements HandlerInterceptor {

	private final Logger logger = Logger.getLogger(IdentityHandlerInterceptor.class.getName());
	public static final String ATTRIBUTE = "Identity";

	@Autowired
	private JwtService jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		try {
			request.removeAttribute(ATTRIBUTE);
			String token = jwtService.extractToken(request);
			if (token == null || jwtService.isTokenExpired(token)) {
				return true;
			}

			String id = jwtService.extractId(token);
			request.setAttribute(ATTRIBUTE, id);
		} catch (Exception e) {
			logger.warning(e.getMessage());
			return true;
		}

		return true;
	}

	public void save(HttpServletResponse response, String id) throws Exception {
		// FIXME: The renewal implementation of jwt token can be placed in the
		// interceptor,
		// or a separate renewal interface can be implemented,
		// or it can be implemented in a common API.

		response.setHeader(ATTRIBUTE, id);

		String token = jwtService.generateToken(id);
		jwtService.injectToken(response, token);
	}

	void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this);
	}
}