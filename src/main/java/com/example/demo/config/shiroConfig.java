package com.example.demo.config;

import com.example.demo.modules.oAuth.CustomRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class shiroConfig {

    @Bean("sessionManager")
    public SessionManager sessionManager(SessionDAO sessionDao) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 超时时间设置（6小时）
        sessionManager.setGlobalSessionTimeout(3600 * 24L * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(true);
        sessionManager.setSessionDAO(sessionDao);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    @Bean("sessionDao")
    public SessionDAO sessionDaoManager() {
        MemorySessionDAO session = new MemorySessionDAO();
        return session;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    // 权限管理，配置主要是Realm的管理认证
    @Bean("securityManager")
    public SecurityManager securityManager(CustomRealm customRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        // securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    // Filter工厂，设置对应的过滤条件和跳转条件
    @Bean("shirFilter")
    public ShiroFilterFactoryBean shirFilter(RemotePropertiesConfig remotePropertiesConfig,SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(remotePropertiesConfig.getAdminPath() + "/login");
        shiroFilterFactoryBean.setUnauthorizedUrl(remotePropertiesConfig.getAdminPath() + "/notRole");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/**/*.css", "anon");
        filterChainDefinitionMap.put("/**/*.js", "anon");
        filterChainDefinitionMap.put("/**/*.html", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/plugins/**", "anon");
        filterChainDefinitionMap.put("/swagger/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        // 放行登录注册接口
        filterChainDefinitionMap.put(remotePropertiesConfig.getAdminPath() + "/captcha.jpg", "anon");// 放行获取验证码
        filterChainDefinitionMap.put(remotePropertiesConfig.getAdminPath() + "/login", "anon");// 放行登录
        filterChainDefinitionMap.put(remotePropertiesConfig.getAdminPath() + "/register", "anon");// 放行注册
        // 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
        filterChainDefinitionMap.put(remotePropertiesConfig.getAdminPath() + "/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
