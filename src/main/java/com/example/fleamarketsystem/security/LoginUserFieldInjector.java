package com.example.fleamarketsystem.security;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * フィールドレベルの@LoginUserアノテーションをサポートするBeanPostProcessor
 * 
 * 使用例:
 * @Component
 * public class MyService {
 *     @LoginUser
 *     private User loginUser;
 *     
 *     public void doSomething() {
 *         // loginUserは自動的に現在のログインユーザーで設定されます
 *     }
 * }
 */
@Component
@RequiredArgsConstructor
public class LoginUserFieldInjector implements BeanPostProcessor {

  private final UserRepository userRepository;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    injectLoginUserFields(bean);
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  private void injectLoginUserFields(Object bean) {
    Class<?> beanClass = bean.getClass();

    // クラスとそのスーパークラスのすべてのフィールドを検査
    for (Class<?> cls = beanClass; cls != null && cls != Object.class; cls =
        cls.getSuperclass()) {
      for (Field field : cls.getDeclaredFields()) {
        if (field.isAnnotationPresent(LoginUser.class)
            && field.getType().equals(User.class)) {
          injectLoginUser(bean, field);
        }
      }
    }
  }

  private void injectLoginUser(Object bean, Field field) {
    try {
      // フィールドへのアクセスを許可
      field.setAccessible(true);

      // リクエストコンテキストが存在する場合のみ注入（HTTP例外を避ける）
      if (RequestContextHolder.getRequestAttributes() != null) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt) {
          Jwt jwt = (Jwt) auth.getPrincipal();
          User user = userRepository.findByAuth0Id(jwt.getSubject())
              .orElseThrow(() -> new UserNotFoundException("User not found"));
          field.set(bean, user);
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to set @LoginUser field: " + field.getName(), e);
    }
  }

  private static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
      super(message);
    }
  }
}
