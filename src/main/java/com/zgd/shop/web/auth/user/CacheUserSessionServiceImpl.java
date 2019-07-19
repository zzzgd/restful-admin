package com.zgd.shop.web.auth.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zgd.shop.web.config.auth.UserAuthProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * CacheUserSessionServiceImpl
 * 使用guava的cache实现的，存储用户会话session
 * @author zgd
 * @date 2019/7/19 14:29
 */
@Component
public class CacheUserSessionServiceImpl implements UserSessionService , InitializingBean {

  @Autowired
  private UserAuthProperties userAuthProperties;

  private Cache<String,CustomerUserDetails> userDetailsCache;
  private static final String USER_SESSION_PREFIX = "USER-SESSION:";


  @Override
  public void saveSession(CustomerUserDetails userDetails) {
    String username = userDetails.getUsername();
    String key = USER_SESSION_PREFIX + username;
    userDetailsCache.put(key,userDetails);
  }

  @Override
  public CustomerUserDetails getSessionByUsername(String username) {
    String key = USER_SESSION_PREFIX + username;
    return userDetailsCache.getIfPresent(key);
  }

  @Override
  public void destroySession(String username) {
    String key = USER_SESSION_PREFIX + username;
    userDetailsCache.invalidate(key);
  }

  @Override
  public void afterPropertiesSet() {
    userDetailsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(userAuthProperties.getSessionExpirationTime(), TimeUnit.MILLISECONDS)
            .build();
  }
}
