package com.origin.hangingpot.port.control;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.origin.hangingpot.domain.User;
import com.origin.hangingpot.domain.error.UserNotFoundError;

public class RequestUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class) &&
                parameter.hasParameterAnnotation(RequestUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
            NativeWebRequest request, WebDataBinderFactory factory) throws Exception {

        Object value = request.getAttribute(UserHandlerInterceptor.ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        // FIXME: If the required flag is false and the user does not exist,
        // return null instead of throwing an exception.
        if (value == null) {
            throw new UserNotFoundError().setDetails("find user from http request by @RequestUser");
        }
        return (User) value;
    }
}
