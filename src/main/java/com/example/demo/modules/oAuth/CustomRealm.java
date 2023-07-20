package com.example.demo.modules.oAuth;

import com.alibaba.druid.util.StringUtils;
import com.example.demo.modules.user.entity.SysUser;
import com.example.demo.modules.user.entity.SysUserToken;
import com.example.demo.service.modules.user.service.LoginService;
import com.example.demo.service.modules.user.service.ShiroService;
import com.example.demo.utils.oAuth.OAuth2Token;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * @MethodName doGetAuthorizationInfo
     * @Description 权限配置类
     * @Param [principalCollection]
     * @Return AuthorizationInfo
     * @Author WangShiLin
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取登录用户名
        // String name = (String) principalCollection.getPrimaryPrincipal();
        // SysUser user1 = (SysUser) principalCollection.getPrimaryPrincipal();
        // Long userId = user1.getUserId();
        // 查询用户名称
        // SysUser user = loginService.getMapById(userId);
        // 添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // for (Role role : user.getRoles()) {
        // // 添加角色
        // simpleAuthorizationInfo.addRole(role.getRoleName());
        // // 添加权限
        // for (Permissions permissions : role.getPermissions()) {
        // simpleAuthorizationInfo.addStringPermission(permissions.getPermissionsName());
        // }
        // }
        return simpleAuthorizationInfo;
    }

    /**
     * @MethodName doGetAuthenticationInfo
     * @Description 认证配置类
     * @Param [authenticationToken]
     * @Return AuthenticationInfo
     * @Author WangShiLin
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // 获取用户信息
        String accessToken = (String) authenticationToken.getPrincipal();
        if (StringUtils.isEmpty(authenticationToken.getPrincipal().toString())) {
            return null;
        }

        // 根据accessToken，查询用户信息
        SysUserToken tokenEntity = shiroService.queryByToken(accessToken);
        // token失效
        if (tokenEntity == null || tokenEntity.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }

        // 获取用户信息
        // String name = authenticationToken.getPrincipal().toString();
        SysUser user = loginService.getMapById(tokenEntity.getUserId());
        // 账号锁定
        if (user == null || user.getStatus() == 0) {
            throw new LockedAccountException("账号不存在或者已被锁定,请联系管理员");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
