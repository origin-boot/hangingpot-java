package com.origin.hangingpot.port.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private LogHandlerInterceptor logHandlerInterceptor;

	@Autowired
	private IdentityHandlerInterceptor identityHandlerInterceptor;

	@Autowired
	private UserHandlerInterceptor userHandlerInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logHandlerInterceptor);

		identityHandlerInterceptor.addInterceptors(registry);
		userHandlerInterceptor.addInterceptors(registry);

		// FIXME: Interceptor to implement permission verification
		// FIXME: The data returned to the user from the production environment needs to
		// be removed,
		// but Error.details should be retained in the log.

		// FIXME: Add more interceptors to increase security and robustness
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new RequestUserHandlerMethodArgumentResolver());
	}
}
