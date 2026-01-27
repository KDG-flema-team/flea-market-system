package com.example.fleamarketsystem.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  private final UserService userService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
	  
    return parameter.hasParameterAnnotation(LoginUser.class)
        && User.class.equals(parameter.getParameterType());
    
  }

  @Override
  public Object resolveArgument(
		  
		  MethodParameter parameter,
		  ModelAndViewContainer mavContainer,
		  NativeWebRequest webRequest,
		  WebDataBinderFactory binderFactory
		  
		  ) throws Exception {
	  
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth == null) {
      return null;
    }
    
    Object principal = auth.getPrincipal();
    
    if (!(principal instanceof Jwt)) {
      return null;
    }
    
    Jwt jwt = (Jwt) principal;
    
    return userService.getOrCreateUserFromAuth0(jwt);
    
  }
}
